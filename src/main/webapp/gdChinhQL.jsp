<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Trang chủ quản lý - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content">
<%@ include file="WEB-INF/fragments/message.jspf" %>

<h1>Trang chủ quản lý</h1>
<p>Xin chào quản lý: <%= Web.h(((Manager3) session.getAttribute("member")).getName()) %></p>
<p class="page-intro">Xem danh sách và thêm thông tin mặt hàng của cửa hàng.</p>
<div class="actions"><a class="button" href="gdQuanLySP.jsp">Quản lý thông tin mặt hàng</a></div>

</main></body></html>
