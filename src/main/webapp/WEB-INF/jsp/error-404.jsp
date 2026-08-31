<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Page not found — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<div class="card-quiet expired-panel">
  <p class="brand-mark">Not found</p>
  <h1 class="h3">This page is not available</h1>
  <p class="text-secondary sans">The address may be wrong, or the application may not be deployed at this path. Try Staff sign in from the home page.</p>
  <div class="d-flex flex-wrap justify-content-center gap-2">
    <a class="btn btn-gold sans" href="${pageContext.request.contextPath}/login">Staff sign in</a>
    <a class="btn btn-navy sans" href="${pageContext.request.contextPath}/">Home</a>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
