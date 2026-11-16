<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>

<%
if (!"POST".equals(request.getMethod())) { response.sendError(405); return; }
session.invalidate(); response.sendRedirect("gdDangNhap.jsp");
%>
