<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Something went wrong — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<div class="card-quiet expired-panel">
  <h1 class="h3">The page could not be opened</h1>
  <p class="text-secondary sans">Try again in a moment. If you were uploading a certificate, confirm the image is 1920 × 1358 pixels.</p>
  <a class="btn btn-navy sans" href="${pageContext.request.contextPath}/">Home</a>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
