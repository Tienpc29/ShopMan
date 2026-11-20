package web;

import org.junit.jupiter.api.*;
import org.apache.catalina.startup.Tomcat;
import support.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;
import static org.junit.jupiter.api.Assertions.*;

/** Kiểm tra JSP được biên dịch thật trên Tomcat, đăng nhập và cả hai luồng. */
class ShopManWebTest {
    static Tomcat tomcat;
    static String base, db;
    HttpClient client;
    @BeforeAll static void start() throws Exception {
        db = DbFixture.create(); tomcat = DemoServer.start(0, db);
        base = "http://127.0.0.1:" + tomcat.getConnector().getLocalPort() + "/ShopMan/";
    }
    @AfterAll static void stop() throws Exception { if (tomcat != null) { tomcat.stop(); tomcat.destroy(); } }
    @BeforeEach void client() { client = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).followRedirects(HttpClient.Redirect.NORMAL).build(); }
    HttpResponse<String> get(String path) throws Exception { return client.send(HttpRequest.newBuilder(URI.create(base+path)).GET().build(), HttpResponse.BodyHandlers.ofString()); }
    HttpResponse<String> post(String path, String form) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base+path)).header("Content-Type", "application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString(form)).build(), HttpResponse.BodyHandlers.ofString());
    }
    String hidden(String html, String name) {
        Matcher m = Pattern.compile("name=\""+name+"\" value=\"([^\"]+)\"").matcher(html);
        assertTrue(m.find(), "Không tìm thấy input " + name); return m.group(1);
    }
    void login(String name, String password) throws Exception {
        String csrf = hidden(get("gdDangNhap.jsp").body(), "csrf");
        HttpResponse<String> response = post("doDangNhap.jsp", "csrf="+csrf+"&username="+name+"&password="+URLEncoder.encode(password, StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode()); assertFalse(response.body().contains("Không thực hiện được yêu cầu"));
    }
    @Test void guestAndRoleGuards() throws Exception {
        assertTrue(get("gdQuanLySP.jsp").uri().getPath().endsWith("gdDangNhap.jsp"));
        login("khachhang", "Khach@123"); assertEquals(403, get("gdThemSP.jsp").statusCode());
        assertEquals(403, post("doLuuDonHang.jsp", "checkoutToken=fake").statusCode());
    }
    @Test void managerAddsProductViaMultipartAndChecksDuplicate() throws Exception {
        login("quanly", "Quanly@123");
        for (String page : new String[]{"gdChinhQL.jsp", "gdQuanLySP.jsp", "gdThemSP.jsp"}) assertEquals(200, get(page).statusCode());
        String csrf = hidden(get("gdThemSP.jsp").body(), "csrf");
        String boundary = "ShopManBoundary0123456789";
        String[][] values = {{"csrf",csrf},{"code","HTTP01"},{"name","Mặt hàng HTTP"},{"unit","Hộp"},{"category","Khác"},{"importPrice","5000"},{"salePrice","7000"},{"stockQuantity","10"},{"description","Thử từ giao diện"}};
        StringBuilder body = new StringBuilder();
        for (String[] value : values) body.append("--").append(boundary).append("\r\nContent-Disposition: form-data; name=\"").append(value[0]).append("\"\r\n\r\n").append(value[1]).append("\r\n");
        body.append("--").append(boundary).append("\r\nContent-Disposition: form-data; name=\"image\"; filename=\"sample.png\"\r\nContent-Type: image/png\r\n\r\n");
        java.io.ByteArrayOutputStream payload = new java.io.ByteArrayOutputStream();
        payload.write(body.toString().getBytes(StandardCharsets.UTF_8));
        javax.imageio.ImageIO.write(new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_RGB), "png", payload);
        payload.write(("\r\n--"+boundary+"--\r\n").getBytes(StandardCharsets.UTF_8));
        HttpRequest req = HttpRequest.newBuilder(URI.create(base+"doThemSP.jsp")).header("Content-Type", "multipart/form-data; boundary="+boundary).POST(HttpRequest.BodyPublishers.ofByteArray(payload.toByteArray())).build();
        HttpResponse<String> first = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, first.statusCode()); assertTrue(first.body().contains("Thêm mặt hàng thành công"), first.body());
        assertTrue(get("gdQuanLySP.jsp").body().contains("HTTP01"));
        try (java.sql.Connection con = DbFixture.factory(db).open(); java.sql.Statement st = con.createStatement(); java.sql.ResultSet rs = st.executeQuery("SELECT image FROM tblProduct3 WHERE code='HTTP01'")) {
            assertTrue(rs.next()); String image = rs.getString(1); assertNotNull(image);
            HttpResponse<byte[]> uploaded = client.send(HttpRequest.newBuilder(URI.create(base+"product-images/"+image)).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
            assertEquals(200, uploaded.statusCode()); assertEquals("image/png", uploaded.headers().firstValue("Content-Type").orElse(""));
        }
        HttpResponse<String> duplicate = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertTrue(duplicate.body().contains("Mã mặt hàng đã tồn tại"), duplicate.body());
        assertEquals(1, DbFixture.scalar(db, "SELECT COUNT(*) FROM tblProduct3 WHERE code='HTTP01'"));
    }
    @Test void customerCompletesOrderAndRefreshDoesNotDuplicate() throws Exception {
        login("khachhang", "Khach@123");
        assertEquals(200, get("gdDatHang.jsp").statusCode());
        assertTrue(get("gdTimSP.jsp?name=Fami").body().contains("SP04"));
        String detail = get("gdChiTietSP.jsp?id=1").body();
        assertTrue(detail.contains("Chi tiết mặt hàng"));
        String csrf = hidden(detail, "csrf");
        assertTrue(post("gdChiTietSP.jsp", "csrf="+csrf+"&id=1&quantity=14").body().contains("Thêm vào giỏ hàng thành công"));
        String cart = get("gdGioHang.jsp").body(); assertTrue(cart.contains("98.000"));
        String checkout = hidden(cart, "checkoutToken");
        int before = DbFixture.scalar(db, "SELECT COUNT(*) FROM tblInvoice3");
        String form = "csrf="+csrf+"&checkoutToken="+checkout;
        assertTrue(post("doLuuDonHang.jsp", form).body().contains("Đặt hàng thành công"));
        assertEquals(before+1, DbFixture.scalar(db, "SELECT COUNT(*) FROM tblInvoice3"));
        post("doLuuDonHang.jsp", form);
        assertEquals(before+1, DbFixture.scalar(db, "SELECT COUNT(*) FROM tblInvoice3"));
        String emptyCart = get("gdGioHang.jsp").body();
        assertTrue(Pattern.compile("<button[^>]*\\bdisabled\\b[^>]*>\\s*Đặt hàng\\s*</button>").matcher(emptyCart).find());
        assertEquals(200, get("loi.jsp").statusCode());
    }
    @Test void invalidQuantityAndLogout() throws Exception {
        login("khachhang", "Khach@123"); String csrf = hidden(get("gdChiTietSP.jsp?id=1").body(), "csrf");
        assertTrue(post("gdChiTietSP.jsp", "csrf="+csrf+"&id=1&quantity=0").body().contains("Số lượng mua phải là số nguyên"));
        assertTrue(post("doDangXuat.jsp", "csrf="+csrf).uri().getPath().endsWith("gdDangNhap.jsp"));
    }
}
