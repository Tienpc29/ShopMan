<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Thêm mặt hàng - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content">
<%@ include file="WEB-INF/fragments/message.jspf" %>

<% Product3 old = (Product3) session.getAttribute("productForm"); session.removeAttribute("productForm"); if (old == null) old = new Product3(); %>
<a class="back-link" href="gdQuanLySP.jsp">← Danh sách mặt hàng</a>
<h1>Thêm thông tin mặt hàng</h1>
<p class="page-intro">Điền thông tin bên dưới. Hình ảnh và mô tả không bắt buộc.</p>
<form class="form form-grid" action="doThemSP.jsp" method="post" enctype="multipart/form-data">
<input type="hidden" name="csrf" value="<%= Web.csrf(session) %>">
<div class="field"><label for="code">Mã mặt hàng</label><input id="code" name="code" maxlength="25" pattern="[A-Z0-9_-]{1,25}" value="<%= Web.h(old.getCode()) %>" required><span class="hint">Ví dụ: SP04. Mã không được trùng.</span></div>
<div class="field"><label for="name">Tên mặt hàng</label><input id="name" name="name" maxlength="255" value="<%= Web.h(old.getName()) %>" required></div>
<div class="field"><label for="category">Loại hàng hóa</label><select id="category" name="category" required>
<% for (String category : new String[]{"Sữa và sản phẩm từ sữa", "Thực phẩm khô", "Đồ uống", "Đồ gia dụng", "Khác"}) { %><option <%= category.equals(old.getCategory()) ? "selected" : "" %>><%= Web.h(category) %></option><% } %>
</select></div>
<div class="field"><label for="unit">Đơn vị tính</label><select id="unit" name="unit" required>
<% for (String unit : new String[]{"Hộp", "Gói", "Chai", "Cái", "Kg"}) { %><option <%= unit.equals(old.getUnit()) ? "selected" : "" %>><%= Web.h(unit) %></option><% } %>
</select></div>
<div class="field"><label for="importPrice">Giá nhập (đồng)</label><input id="importPrice" name="importPrice" type="number" min="0" max="1000000000" step="1" value="<%= old.getImportPrice() %>" required></div>
<div class="field"><label for="stockQuantity">Số lượng nhập ban đầu</label><input id="stockQuantity" name="stockQuantity" type="number" min="0" max="2147483647" step="1" value="<%= old.getStockQuantity() %>" required></div>
<div class="field"><label for="salePrice">Giá bán (đồng)</label><input id="salePrice" name="salePrice" type="number" min="0" max="1000000000" step="1" value="<%= old.getSalePrice() %>" required></div>
<div class="field full"><label for="image">Hình ảnh</label><input id="image" name="image" type="file" accept="image/png,image/jpeg,image/gif" aria-describedby="image-hint"><span id="image-hint" class="hint">PNG, JPG, GIF; tối đa 2 MB. Khi nhập lại biểu mẫu, hãy chọn lại ảnh.</span></div>
<div class="field full"><label for="description">Mô tả</label><textarea id="description" name="description" maxlength="255"><%= Web.h(old.getDescription()) %></textarea></div>
<div class="actions"><button type="submit">Xác nhận thêm</button><a href="gdQuanLySP.jsp">Quay lại</a></div>
</form>

</main></body></html>
