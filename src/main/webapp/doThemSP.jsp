<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>

<%
if (!"POST".equals(request.getMethod())) { response.sendRedirect("gdThemSP.jsp"); return; }
Product3 p = new Product3(); String savedImage = null; boolean persisted = false;
try {
    p.setCode(request.getParameter("code")); p.setName(request.getParameter("name"));
    p.setUnit(request.getParameter("unit")); p.setCategory(request.getParameter("category"));
    p.setDescription(request.getParameter("description"));
    p.setImportPrice(Validation.money(request.getParameter("importPrice"), "Giá nhập"));
    p.setSalePrice(Validation.money(request.getParameter("salePrice"), "Giá bán"));
    p.setStockQuantity(Validation.integer(request.getParameter("stockQuantity"), "Số lượng", 0));
    Validation.product(p);
    Product3DAO productDAO = new Product3DAO();
    if (productDAO.checkExists(p.getCode())) throw new IllegalArgumentException("Mã mặt hàng đã tồn tại. Hãy nhập mã khác.");
    savedImage = Images.save(request.getPart("image")); p.setImage(savedImage);
    if (!productDAO.addProduct(p)) throw new java.sql.SQLException("Không lưu được mặt hàng.");
    persisted = true;
} catch (IllegalArgumentException e) {
    Images.delete(savedImage); session.setAttribute("productForm", p); session.setAttribute("message", e.getMessage());
} catch (java.sql.SQLException e) {
    Images.delete(savedImage); application.log("Thêm mặt hàng", e); session.setAttribute("productForm", p);
    session.setAttribute("message", "23000".equals(e.getSQLState()) || "23505".equals(e.getSQLState()) ? "Mã mặt hàng đã tồn tại." : "Không lưu được mặt hàng. Kiểm tra kết nối CSDL và thử lại.");
} catch (Exception e) {
    Images.delete(savedImage); application.log("Tải ảnh mặt hàng", e); session.setAttribute("productForm", p);
    session.setAttribute("message", "Không đọc được ảnh. Hãy chọn ảnh hợp lệ và thử lại.");
}
if (!persisted) { response.sendRedirect("gdThemSP.jsp"); return; }
%>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Thêm thành công - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content"><div class="result"><h1>Thêm mặt hàng thành công</h1><p>Đã lưu mặt hàng <strong><%= Web.h(p.getCode()) %></strong>: <%= Web.h(p.getName()) %>.</p>
<div class="actions"><a class="button" href="gdQuanLySP.jsp">OK</a><a href="gdThemSP.jsp">Thêm mặt hàng khác</a></div></div></main></body></html>
