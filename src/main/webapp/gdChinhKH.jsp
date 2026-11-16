<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Trang chủ khách hàng - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content">
<%@ include file="WEB-INF/fragments/message.jspf" %>

<h1>Trang chủ khách hàng</h1>
<p>Xin chào quý khách <%= Web.h(((Customer3) session.getAttribute("member")).getName()) %> đến với ShopMan.</p>
<p class="page-intro">Tìm mặt hàng, thêm vào giỏ và xác nhận đơn hàng.</p>
<div class="actions"><a class="button" href="gdDatHang.jsp">Đặt mua hàng trực tuyến</a></div>

</main></body></html>
