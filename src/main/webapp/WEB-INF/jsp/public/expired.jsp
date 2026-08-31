<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Link expired — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<div class="card-quiet expired-panel">
  <p class="brand-mark">Link closed</p>
  <h1 class="h3">This certificate link has expired</h1>
  <p class="text-secondary sans mx-auto" style="max-width: 32rem">
    The organiser set an end time for certificate downloads
    <c:if test="${not empty seminar}"> for <strong>${seminar.title}</strong></c:if>.
    The form is no longer available. Ask the seminar admin if a new link is required.
  </p>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
