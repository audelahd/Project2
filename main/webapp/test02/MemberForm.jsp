<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath }"/>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 가입창</title>
</head>
<body>
	<form method="post" action="${contextPath }/member/addMember.do">
	<h1 style="text-align:center">회원 가입창</h1>
	<table align="center">
		<tr>
		<td width="200">
		<p align="right">아이디</p></td>
	</table>
	</form>

</body>
</html>