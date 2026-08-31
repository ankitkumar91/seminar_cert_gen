<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${empty seminar ? 'New seminar' : 'Edit seminar'} — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<p class="brand-mark mb-1">Admin</p>
<h1 class="h3 mb-4">${empty seminar ? "Create a seminar" : "Edit seminar details"}</h1>

<div class="card-quiet p-4 p-md-5">
  <form method="post" class="sans cert-form">
    <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
    <c:if test="${not empty seminar}">
      <input type="hidden" name="id" value="${seminar.id}">
    </c:if>
    <div class="mb-3">
      <label class="form-label" for="title">Seminar title</label>
      <input class="form-control" id="title" name="title" required maxlength="255" value="${seminar.title}">
    </div>
    <div class="row">
      <div class="col-md-6 mb-3">
        <label class="form-label" for="organizer">Organising department</label>
        <input class="form-control" id="organizer" name="organizer" maxlength="255" value="${seminar.organizer}">
      </div>
      <div class="col-md-6 mb-3">
        <label class="form-label" for="venue">Venue</label>
        <input class="form-control" id="venue" name="venue" maxlength="255" value="${seminar.venue}">
      </div>
    </div>
    <div class="mb-3">
      <label class="form-label" for="seminarDate">Seminar date</label>
      <input class="form-control" id="seminarDate" name="seminarDate" type="date" value="${seminar.seminarDate}">
    </div>
    <div class="mb-4">
      <label class="form-label" for="description">Internal notes</label>
      <textarea class="form-control" id="description" name="description" rows="4">${seminar.description}</textarea>
    </div>
    <div class="d-flex gap-2">
      <button class="btn btn-navy" type="submit">Save seminar</button>
      <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin">Cancel</a>
    </div>
  </form>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
