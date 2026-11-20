<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>

<%
List<InvoiceDetail3> cart;
String checkoutToken;
synchronized (session) {
    cart = (List<InvoiceDetail3>) session.getAttribute("cart");
    if (cart == null) { cart = new ArrayList<>(); session.setAttribute("cart", cart); }
    if ("POST".equals(request.getMethod()) && "remove".equals(request.getParameter("action"))) {
        try { int id = Validation.integer(request.getParameter("id"), "ID mặt hàng", 1); cart.removeIf(line -> line.getProduct().getId() == id); }
        catch (IllegalArgumentException e) { session.setAttribute("message", e.getMessage()); }
        session.removeAttribute("checkoutToken"); response.sendRedirect("gdGioHang.jsp"); return;
    }
    checkoutToken = (String) session.getAttribute("checkoutToken");
    if (checkoutToken == null) { checkoutToken = UUID.randomUUID().toString(); session.setAttribute("checkoutToken", checkoutToken); }
    cart = new ArrayList<>(cart);
}
Customer3 cus = (Customer3) session.getAttribute("member");
BigDecimal total = BigDecimal.ZERO;
%>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Giỏ hàng - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content">
<%@ include file="WEB-INF/fragments/message.jspf" %>

<h1>Giỏ hàng của bạn</h1>
<div class="customer-info"><p>Khách hàng: <strong><%= Web.h(cus.getName()) %></strong></p><p class="hint">Số điện thoại: <%= Web.h(cus.getPhone()) %><br>Địa chỉ: <%= Web.h(cus.getAddress()) %></p></div>
<% if (!cart.isEmpty()) { %>
<div class="table-wrap"><table class="product-table"><thead><tr><th>Mã mặt hàng</th><th>Tên mặt hàng</th><th class="num">Số lượng</th><th class="num">Đơn giá</th><th class="num">Thành tiền</th><th>Thao tác</th></tr></thead><tbody>
<% for (InvoiceDetail3 d : cart) { BigDecimal amount = new BigDecimal(Float.toString(d.getUnitPrice())).multiply(BigDecimal.valueOf(d.getQuantity())); total = total.add(amount); %>
<tr><td><%= Web.h(d.getProduct().getCode()) %></td><td><div class="line-product">
<span class="product-thumb">
<% if (d.getProduct().getImage() != null && !d.getProduct().getImage().isBlank()) { %>
<img src="product-images/<%= Web.h(d.getProduct().getImage()) %>" alt="<%= Web.h(d.getProduct().getName()) %>" width="72" height="72" loading="lazy" decoding="async">
<span class="product-thumb-placeholder" hidden>Chưa có ảnh</span>
<% } else { %><span class="product-thumb-placeholder">Chưa có ảnh</span><% } %>
</span>
<span class="line-product-name"><%= Web.h(d.getProduct().getName()) %></span>
</div></td><td class="num"><%= d.getQuantity() %></td><td class="num"><%= Web.money(d.getUnitPrice()) %></td><td class="num"><%= Web.money(amount) %></td>
<td><form action="gdGioHang.jsp" method="post"><input type="hidden" name="csrf" value="<%= Web.csrf(session) %>"><input type="hidden" name="action" value="remove"><input type="hidden" name="id" value="<%= d.getProduct().getId() %>"><button type="submit">Xóa</button></form></td></tr>
<% } %>
</tbody></table></div>
<% } %>
<p class="total">Tổng tiền dự kiến: <%= Web.money(total) %></p>
<p class="hint">Giá và số lượng tồn kho được kiểm tra lại khi xác nhận đặt hàng. Đơn mới có trạng thái chờ duyệt.</p>
<form action="doLuuDonHang.jsp" method="post">
<input type="hidden" name="csrf" value="<%= Web.csrf(session) %>"><input type="hidden" name="checkoutToken" value="<%= Web.h(checkoutToken) %>">
<div class="actions"><button type="submit" <%= cart.isEmpty() ? "disabled" : "" %>>Đặt hàng</button><a href="gdDatHang.jsp">Tiếp tục mua hàng</a></div>
</form>

</main></body></html>
