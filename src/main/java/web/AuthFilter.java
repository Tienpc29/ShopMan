package web;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Set;
import model.*;

public class AuthFilter implements Filter {
    private static final Set<String> MANAGER = Set.of("/gdChinhQL.jsp", "/gdQuanLySP.jsp", "/gdThemSP.jsp", "/doThemSP.jsp");
    private static final Set<String> CUSTOMER = Set.of("/gdChinhKH.jsp", "/gdDatHang.jsp", "/gdTimSP.jsp", "/gdChiTietSP.jsp", "/gdGioHang.jsp", "/doLuuDonHang.jsp");
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");
        if (!req.getServletPath().startsWith("/product-images")) res.setCharacterEncoding("UTF-8");
        res.setHeader("X-Content-Type-Options", "nosniff"); res.setHeader("X-Frame-Options", "DENY");
        String path = req.getServletPath();
        if (path.startsWith("/assets/")) { chain.doFilter(req, res); return; }
        res.setHeader("Cache-Control", "no-store");
        HttpSession session = req.getSession();
        Object member = session.getAttribute("member");
        boolean publicPage = path.equals("/gdDangNhap.jsp") || path.equals("/doDangNhap.jsp") || path.equals("/loi.jsp");
        if (!publicPage && !(member instanceof Member3)) { res.sendRedirect(req.getContextPath() + "/gdDangNhap.jsp"); return; }
        if (MANAGER.contains(path) && !(member instanceof Manager3) || CUSTOMER.contains(path) && !(member instanceof Customer3)) {
            res.sendError(403, "Bạn không có quyền sử dụng chức năng này."); return;
        }
        if ("POST".equals(req.getMethod())) {
            String token;
            try { token = req.getParameter("csrf"); }
            catch (IllegalStateException e) { res.sendError(413, "Tệp tải lên quá lớn."); return; }
            if (token == null || !token.equals(session.getAttribute("csrf"))) { res.sendError(403, "Phiên biểu mẫu không hợp lệ. Hãy tải lại trang."); return; }
        }
        chain.doFilter(req, res);
    }
}
