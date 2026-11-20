<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>

<%
Product3 selectedProduct;
try {
    int id = Validation.integer(request.getParameter("id"), "ID mặt hàng", 1);
    selectedProduct = new Product3DAO().getProductById(id);
    if (selectedProduct == null) throw new IllegalArgumentException("Mặt hàng không tồn tại.");
} catch (IllegalArgumentException e) {
    session.setAttribute("message", e.getMessage()); response.sendRedirect("gdTimSP.jsp"); return;
}
String detailError = null;
if ("POST".equals(request.getMethod())) {
    try {
        int quantity = Validation.integer(request.getParameter("quantity"), "Số lượng mua", 1);
        synchronized (session) {
            List<InvoiceDetail3> cart = (List<InvoiceDetail3>) session.getAttribute("cart");
            if (cart == null) cart = new ArrayList<>();
            InvoiceDetail3 existing = null;
            for (InvoiceDetail3 line : cart) if (line.getProduct().getId() == selectedProduct.getId()) existing = line;
            long sum = (long) quantity + (existing == null ? 0 : existing.getQuantity());
            if (sum > selectedProduct.getStockQuantity()) throw new IllegalArgumentException("Tổng số lượng trong giỏ vượt quá tồn kho.");
            if (existing == null) {
                InvoiceDetail3 detail = new InvoiceDetail3();
                detail.setProduct(selectedProduct); detail.setQuantity(quantity); detail.setUnitPrice(selectedProduct.getSalePrice());
                cart.add(detail);
            } else { existing.setQuantity((int) sum); existing.setUnitPrice(selectedProduct.getSalePrice()); existing.setProduct(selectedProduct); }
            session.setAttribute("cart", cart); session.removeAttribute("checkoutToken");
        }
        session.setAttribute("message", "Thêm vào giỏ hàng thành công."); response.sendRedirect("gdDatHang.jsp"); return;
    } catch (IllegalArgumentException e) { detailError = e.getMessage(); }
}
%>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Chi tiết mặt hàng - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content">
<%@ include file="WEB-INF/fragments/message.jspf" %>

<a class="back-link" href="gdTimSP.jsp">← Tìm mặt hàng</a>
<h1>Chi tiết mặt hàng</h1>
<% if (detailError != null) { %><div class="notice" role="alert"><%= Web.h(detailError) %></div><% } %>
<div class="product-layout"><div class="product-media">
<% if (selectedProduct.getImage() != null) { %><img class="product-image" src="product-images/<%= Web.h(selectedProduct.getImage()) %>" alt="<%= Web.h(selectedProduct.getName()) %>"><% } else { %><span class="product-placeholder">Chưa có hình ảnh</span><% } %>
</div><div>
<dl><dt>Mã mặt hàng</dt><dd><%= Web.h(selectedProduct.getCode()) %></dd><dt>Tên mặt hàng</dt><dd><%= Web.h(selectedProduct.getName()) %></dd>
<dt>Giá bán</dt><dd><%= Web.money(selectedProduct.getSalePrice()) %> / <%= Web.h(selectedProduct.getUnit()) %></dd>
<dt>Còn lại trong kho</dt><dd><%= selectedProduct.getStockQuantity() %> <%= Web.h(selectedProduct.getUnit()) %></dd>
<dt>Mô tả</dt><dd><%= Web.h(selectedProduct.getDescription()) %></dd></dl>
<form class="form" action="gdChiTietSP.jsp" method="post">
<input type="hidden" name="csrf" value="<%= Web.csrf(session) %>"><input type="hidden" name="id" value="<%= selectedProduct.getId() %>">
<div class="field quantity-field"><label for="quantity">Số lượng mua</label><input id="quantity" name="quantity" type="number" min="1" max="<%= Math.max(1, selectedProduct.getStockQuantity()) %>" step="1" value="1" <%= selectedProduct.getStockQuantity() == 0 ? "disabled" : "" %> required></div>
<% if (selectedProduct.getStockQuantity() == 0) { %><p class="hint">Mặt hàng hiện đã hết hàng.</p><% } %>
<div class="actions"><button type="submit" <%= selectedProduct.getStockQuantity() == 0 ? "disabled" : "" %>>Thêm vào giỏ hàng</button><a href="gdTimSP.jsp">Quay lại tìm kiếm</a></div>
</form>
</div></div>

</main></body></html>
