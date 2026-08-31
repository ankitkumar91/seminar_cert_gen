<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Link not found — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<div class="card-quiet expired-panel">
  <p class="brand-mark">Unknown link</p>
  <h1 class="h3">This certificate page does not exist</h1>
  <p class="text-secondary sans">Check the URL you were sent, or ask the seminar organiser for a new link.</p>
  <a class="btn btn-navy sans" href="${pageContext.request.contextPath}/">Back to Certificate Desk</a>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
