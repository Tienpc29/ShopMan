<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><title>Đăng nhập - ShopMan</title><link rel="stylesheet" href="assets/style.css"></head><body>
<%@ include file="WEB-INF/fragments/header.jspf" %>
<main id="main-content">
<%@ include file="WEB-INF/fragments/message.jspf" %>

<div class="login"><h1>Đăng nhập</h1>
<p>Đăng nhập để sử dụng ShopMan.</p>
<form action="doDangNhap.jsp" method="post">
<input type="hidden" name="csrf" value="<%= Web.csrf(session) %>">
<div class="field"><label for="username">Tên đăng nhập</label><input id="username" name="username" maxlength="255" autocomplete="username" autocapitalize="none" spellcheck="false" required></div>
<div class="field"><label for="password">Mật khẩu</label><input id="password" name="password" type="password" autocomplete="current-password" required></div>
<button type="submit">Đăng nhập</button>
</form></div>

</main></body></html>
