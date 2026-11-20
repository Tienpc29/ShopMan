<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>

<%
if (!"POST".equals(request.getMethod())) { response.sendRedirect("gdGioHang.jsp"); return; }
synchronized (session) {
    String expected = (String) session.getAttribute("checkoutToken");
    if (expected == null || !expected.equals(request.getParameter("checkoutToken"))) {
        session.setAttribute("message", "Giỏ hàng đã thay đổi hoặc đơn đã được gửi. Hãy kiểm tra lại.");
        response.sendRedirect("gdGioHang.jsp"); return;
    }
    List<InvoiceDetail3> cart = (List<InvoiceDetail3>) session.getAttribute("cart");
    Customer3 cus = (Customer3) session.getAttribute("member");
    try {
        boolean result = new Invoice3DAO().createInvoice(cart == null ? null : cart.toArray(new InvoiceDetail3[0]), cus.getId());
        if (!result) throw new java.sql.SQLException("Không lưu được đơn hàng.");
        session.removeAttribute("cart"); session.removeAttribute("checkoutToken");
        session.setAttribute("message", "Đặt hàng thành công. Đơn hàng đang chờ được duyệt.");
        response.sendRedirect("gdDatHang.jsp");
    } catch (IllegalArgumentException e) {
        session.setAttribute("message", e.getMessage()); response.sendRedirect("gdGioHang.jsp");
    } catch (java.sql.SQLException e) {
        application.log("Lưu đơn hàng", e);
        session.setAttribute("message", "Chưa lưu được đơn hàng. Giỏ hàng được giữ lại để bạn thử lại."); response.sendRedirect("gdGioHang.jsp");
    }
}
%>
