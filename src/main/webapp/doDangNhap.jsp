<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>

<%
if (!"POST".equals(request.getMethod())) { response.sendRedirect("gdDangNhap.jsp"); return; }
try {
    Member3 member = new Member3DAO().checkLogin(request.getParameter("username"), request.getParameter("password"));
    if (member == null) { session.setAttribute("message", "Tên đăng nhập hoặc mật khẩu không đúng."); response.sendRedirect("gdDangNhap.jsp"); return; }
    session.invalidate(); session = request.getSession(true);
    session.setAttribute("member", member);
    response.sendRedirect(member instanceof Manager3 ? "gdChinhQL.jsp" : "gdChinhKH.jsp");
} catch (java.sql.SQLException e) {
    application.log("Đăng nhập: lỗi CSDL", e);
    session.setAttribute("message", "Chưa kết nối được CSDL. Kiểm tra cấu hình db.properties.");
    response.sendRedirect("gdDangNhap.jsp");
}
%>
