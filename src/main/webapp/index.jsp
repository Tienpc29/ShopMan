<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,dao.*,util.*,java.util.*,java.math.BigDecimal" %>

<%
Object member = session.getAttribute("member");
if (member instanceof Manager3) response.sendRedirect("gdChinhQL.jsp");
else if (member instanceof Customer3) response.sendRedirect("gdChinhKH.jsp");
else response.sendRedirect("gdDangNhap.jsp");
%>
