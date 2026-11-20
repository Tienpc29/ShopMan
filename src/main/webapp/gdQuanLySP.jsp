<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Quản lý mặt hàng - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content">
<%@ include file="WEB-INF/fragments/message.jspf" %>

<% Product3[] products = new Product3DAO().getAllProduct(); %>
<h1>Quản lý thông tin mặt hàng</h1>
<p class="page-intro">Theo dõi giá, số lượng tồn kho và thêm mặt hàng mới.</p>
<div class="actions"><a class="button" href="gdThemSP.jsp">Thêm thông tin mặt hàng</a><a href="gdChinhQL.jsp">Quay lại</a></div>
<p>Danh sách mặt hàng hiện có: <%= products.length %> mặt hàng.</p>
<% if (products.length == 0) { %><p class="empty-state">Chưa có mặt hàng. Chọn “Thêm thông tin mặt hàng” để bắt đầu.</p><% } else { %>
<div class="table-wrap"><table><thead><tr><th>Mã mặt hàng</th><th>Tên mặt hàng</th><th>Loại hàng</th><th>Đơn vị</th><th class="num">Giá nhập</th><th class="num">Giá bán</th><th class="num">Tồn kho</th></tr></thead><tbody>
<% for (Product3 p : products) { %>
<tr><td><%= Web.h(p.getCode()) %></td><td><%= Web.h(p.getName()) %></td><td><%= Web.h(p.getCategory()) %></td><td><%= Web.h(p.getUnit()) %></td><td class="num"><%= Web.money(p.getImportPrice()) %></td><td class="num"><%= Web.money(p.getSalePrice()) %></td><td class="num"><%= p.getStockQuantity() %></td></tr>
<% } %>
</tbody></table></div>
<% } %>

</main></body></html>
