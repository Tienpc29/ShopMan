package dao;

import model.*;
import org.junit.jupiter.api.*;
import support.DbFixture;
import java.sql.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** Kiểm thử tích hợp DAO trên DB thật trong bộ nhớ, không mock câu SQL. */
class ShopManDAOTest {
    String url;
    Product3DAO products;
    Invoice3DAO invoices;
    @BeforeEach void setup() throws Exception {
        url = DbFixture.create(); products = new Product3DAO(DbFixture.factory(url));
        invoices = new Invoice3DAO(DbFixture.factory(url));
    }
    Product3 sample(String code) {
        Product3 p = new Product3(); p.setCode(code); p.setName("Mặt hàng thử"); p.setUnit("Hộp");
        p.setCategory("Khác"); p.setImportPrice(5000); p.setSalePrice(7000); p.setStockQuantity(20);
        return p;
    }
    InvoiceDetail3 line(int id, int quantity) {
        Product3 p = new Product3(); p.setId(id);
        InvoiceDetail3 d = new InvoiceDetail3(); d.setProduct(p); d.setQuantity(quantity);
        d.setUnitPrice(1); // Giá giả trong giỏ phải bị bỏ qua.
        return d;
    }
    @Test void getAllAndLookup() throws Exception {
        assertEquals(3, products.getAllProduct().length);
        assertEquals("SP04", products.getProductById(1).getCode());
        assertNull(products.getProductById(99999));
    }
    @Test void addAndCheckCode() throws Exception {
        assertTrue(products.checkExists("SP04")); assertFalse(products.checkExists("NEW01"));
        Product3 p = sample("NEW01"); assertTrue(products.addProduct(p)); assertTrue(p.getId() > 0);
        assertTrue(products.checkExists("NEW01")); assertEquals(7000, products.getProductById(p.getId()).getSalePrice());
    }
    @Test void duplicateCodeRejectedByDatabase() throws Exception {
        assertThrows(SQLException.class, () -> products.addProduct(sample("SP04")));
        assertEquals(3, products.getAllProduct().length);
    }
    @Test void invalidProductRejected() {
        Product3 p = sample("BAD"); p.setSalePrice(-1);
        assertThrows(IllegalArgumentException.class, () -> products.addProduct(p));
        p.setSalePrice(Float.NaN); assertThrows(IllegalArgumentException.class, () -> products.addProduct(p));
    }
    @Test void searchUsesNameAndEscapesWildcards() throws Exception {
        assertEquals(1, products.searchProduct("Fami").length);
        assertEquals(0, products.searchProduct("' OR 1=1 --").length);
        assertEquals(0, products.searchProduct("%").length);
    }
    @Test void loginChecksHashAndCreatesCorrectEntity() throws Exception {
        // Riêng ca này dùng mật khẩu 12345678; không đổi tài khoản trong SQL mẫu.
        try (Connection con = DbFixture.factory(url).open();
             PreparedStatement ps = con.prepareStatement("UPDATE tblMember3 SET password=? WHERE username IN ('quanly','khachhang')")) {
            ps.setString(1, "pbkdf2$210000$Hc+8s3n7sXxTdXvgsBL6CQ==$fRQaWSR8EEK8GvGcLVMRxjov2FNpZaCVpkYbdtlm2GY=");
            assertEquals(2, ps.executeUpdate());
        }
        Member3DAO dao = new Member3DAO(DbFixture.factory(url));
        assertInstanceOf(Manager3.class, dao.checkLogin("quanly", "12345678"));
        assertInstanceOf(Customer3.class, dao.checkLogin("khachhang", "12345678"));
        assertNull(dao.checkLogin("quanly", "wrong"));
        assertNull(dao.checkLogin("' OR 1=1 --", "wrong"));
    }
    @Test void orderStoresInvoiceDetailsAndCurrentPrice() throws Exception {
        assertTrue(invoices.createInvoice(new InvoiceDetail3[]{line(1, 14), line(2, 2)}, 2));
        assertEquals(1, DbFixture.scalar(url, "SELECT COUNT(*) FROM tblInvoice3 WHERE invoiceStatus='CHO_DUYET' AND tblCustomer3tblMember3id=2 AND tblSalesStaff3id IS NULL AND tblDeliveryStaff3id IS NULL"));
        assertEquals(2, DbFixture.scalar(url, "SELECT COUNT(*) FROM tblInvoiceDetail3"));
        assertEquals(7000, DbFixture.scalar(url, "SELECT unitPrice FROM tblInvoiceDetail3 WHERE tblProduct3id=1"));
        assertEquals(486, products.getProductById(1).getStockQuantity());
    }
    @Test void duplicateCartLinesAreMerged() throws Exception {
        invoices.createInvoice(new InvoiceDetail3[]{line(1, 2), line(1, 3)}, 2);
        assertEquals(1, DbFixture.scalar(url, "SELECT COUNT(*) FROM tblInvoiceDetail3"));
        assertEquals(5, DbFixture.scalar(url, "SELECT quantity FROM tblInvoiceDetail3"));
    }
    @Test void emptyAndInvalidQuantityRejected() {
        assertThrows(IllegalArgumentException.class, () -> invoices.createInvoice(new InvoiceDetail3[0], 2));
        assertThrows(IllegalArgumentException.class, () -> invoices.createInvoice(new InvoiceDetail3[]{line(1, 0)}, 2));
    }
    @Test void insufficientStockLeavesNoPartialOrder() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> invoices.createInvoice(new InvoiceDetail3[]{line(1, 1), line(2, 101)}, 2));
        assertEquals(0, DbFixture.scalar(url, "SELECT COUNT(*) FROM tblInvoice3"));
        assertEquals(500, products.getProductById(1).getStockQuantity());
    }
    @Test void unknownCustomerOrProductRejected() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> invoices.createInvoice(new InvoiceDetail3[]{line(1, 1)}, 999));
        assertThrows(IllegalArgumentException.class, () -> invoices.createInvoice(new InvoiceDetail3[]{line(999, 1)}, 2));
        assertEquals(0, DbFixture.scalar(url, "SELECT COUNT(*) FROM tblInvoice3"));
    }
    @Test void detailInsertFailureRollsBackInvoiceAndStock() throws Exception {
        try (Connection con = DbFixture.factory(url).open(); Statement st = con.createStatement()) {
            st.execute("ALTER TABLE tblInvoiceDetail3 ADD CONSTRAINT force_failure CHECK(quantity<10)");
        }
        assertThrows(SQLException.class, () -> invoices.createInvoice(new InvoiceDetail3[]{line(1, 1), line(2, 14)}, 2));
        assertEquals(0, DbFixture.scalar(url, "SELECT COUNT(*) FROM tblInvoice3"));
        assertEquals(0, DbFixture.scalar(url, "SELECT COUNT(*) FROM tblInvoiceDetail3"));
        assertEquals(500, products.getProductById(1).getStockQuantity());
    }
    @Test void concurrentOrdersCannotOversell() throws Exception {
        try (Connection con = DbFixture.factory(url).open(); Statement st = con.createStatement()) { st.executeUpdate("UPDATE tblProduct3 SET stockQuantity=5 WHERE id=1"); }
        ExecutorService pool = Executors.newFixedThreadPool(2); CountDownLatch start = new CountDownLatch(1);
        Callable<Boolean> task = () -> { start.await(); try { return invoices.createInvoice(new InvoiceDetail3[]{line(1, 4)}, 2); } catch (IllegalArgumentException e) { return false; } };
        try {
            Future<Boolean> first = pool.submit(task), second = pool.submit(task); start.countDown();
            assertNotEquals(first.get(10, TimeUnit.SECONDS), second.get(10, TimeUnit.SECONDS));
            assertEquals(1, products.getProductById(1).getStockQuantity());
            assertEquals(1, DbFixture.scalar(url, "SELECT COUNT(*) FROM tblInvoice3"));
        } finally { pool.shutdownNow(); }
    }
}
