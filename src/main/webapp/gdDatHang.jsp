<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Đặt mua hàng - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content">
<%@ include file="WEB-INF/fragments/message.jspf" %>

<% List<InvoiceDetail3> cart = (List<InvoiceDetail3>) session.getAttribute("cart"); int lineCount = cart == null ? 0 : cart.size(); %>
<h1>Đặt mua hàng trực tuyến</h1>
<p class="page-intro">Tìm mặt hàng, chọn số lượng và thêm vào giỏ. Bạn có thể chọn nhiều mặt hàng trước khi xác nhận đặt hàng.</p>
<div class="actions"><a class="button" href="gdTimSP.jsp">Tìm hàng hóa</a><a class="button secondary" href="gdGioHang.jsp">Xem giỏ hàng (<%= lineCount %>)</a><a href="gdChinhKH.jsp">Quay lại</a></div>

</main></body></html>
