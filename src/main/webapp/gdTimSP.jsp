<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Tìm mặt hàng - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content">
<%@ include file="WEB-INF/fragments/message.jspf" %>

<h1>Tìm kiếm mặt hàng</h1>
<form action="gdTimSP.jsp" method="get"><label for="name">Nhập tên mặt hàng cần tìm</label><div class="search"><input id="name" name="name" type="search" maxlength="255" placeholder="Ví dụ: sữa, bánh…" value="<%= Web.h(request.getParameter("name")) %>" required><button type="submit">Tìm kiếm</button></div></form>
<%
String name = request.getParameter("name");
if (name != null && !name.isBlank()) {
    // Bước 8–12: tìm theo tên, DAO trả về Product3[].
    Product3[] found = new Product3DAO().searchProduct(name);
%>
<h2>Kết quả tìm kiếm</h2>
<p class="hint">Tìm thấy <%= found.length %> mặt hàng.</p>
<% if (found.length == 0) { %><p class="empty-state">Không tìm thấy mặt hàng phù hợp. Hãy thử tên khác.</p><% } else { %>
<div class="table-wrap"><table class="product-table"><thead><tr><th>Mã mặt hàng</th><th>Tên mặt hàng</th><th>Đơn vị</th><th class="num">Giá bán</th><th class="num">Tồn kho</th><th>Chọn</th></tr></thead><tbody>
<% for (Product3 p : found) { %>
<tr><td><%= Web.h(p.getCode()) %></td><td><div class="line-product">
<span class="product-thumb">
<% if (p.getImage() != null && !p.getImage().isBlank()) { %>
<img src="product-images/<%= Web.h(p.getImage()) %>" alt="<%= Web.h(p.getName()) %>" width="72" height="72" loading="lazy" decoding="async">
<span class="product-thumb-placeholder" hidden>Chưa có ảnh</span>
<% } else { %><span class="product-thumb-placeholder">Chưa có ảnh</span><% } %>
</span>
<span class="line-product-name"><%= Web.h(p.getName()) %></span>
</div></td><td><%= Web.h(p.getUnit()) %></td><td class="num"><%= Web.money(p.getSalePrice()) %></td><td class="num"><%= p.getStockQuantity() %></td><td><a href="gdChiTietSP.jsp?id=<%= p.getId() %>">Xem chi tiết</a></td></tr>
<% } %>
</tbody></table></div>
<% } %>
<% } else { %><p class="hint">Nhập tên và bấm Tìm kiếm để xem các mặt hàng phù hợp.</p><% } %>
<div class="actions"><a href="gdDatHang.jsp">Quay lại</a></div>

</main></body></html>
