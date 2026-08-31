<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Certificate — ${seminar.title}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<div class="row justify-content-center">
  <div class="col-lg-7">
    <div class="card-quiet p-4 p-md-5">
      <p class="brand-mark mb-2">Attendee form</p>
      <h1 class="h3">${seminar.title}</h1>
      <p class="text-secondary sans">
        ${seminar.organizer}
        <c:if test="${not empty seminar.venue}"> · ${seminar.venue}</c:if>
        · ${seminar.seminarDateLabel}
      </p>
      <p class="sans">Fill the same details that will be printed on your certificate. After you submit, a PDF downloads automatically. This link stops working at ${expiresAt}.</p>

      <c:if test="${not empty error}">
        <div class="alert alert-danger sans">${error}</div>
      </c:if>

      <form method="post" class="sans cert-form">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
        <div class="mb-3">
          <label class="form-label" for="fullName">Full name</label>
          <input class="form-control" id="fullName" name="fullName" required maxlength="120" value="${submission.fullName}" placeholder="As it should appear on the certificate">
        </div>
        <div class="mb-3">
          <label class="form-label" for="email">Email address</label>
          <input class="form-control" id="email" name="email" type="email" required value="${submission.email}">
        </div>
        <div class="mb-3">
          <label class="form-label" for="phone">Mobile number</label>
          <input class="form-control" id="phone" name="phone" required value="${submission.phone}">
        </div>
        <div class="mb-3">
          <label class="form-label" for="college">College / organisation</label>
          <input class="form-control" id="college" name="college" required maxlength="255" value="${submission.college}">
        </div>
        <div class="mb-3">
          <label class="form-label" for="enrollmentNo">Enrollment / roll number</label>
          <input class="form-control" id="enrollmentNo" name="enrollmentNo" required maxlength="80" value="${submission.enrollmentNo}">
        </div>
        <div class="mb-4">
          <label class="form-label" for="designation">Role</label>
          <select class="form-select" id="designation" name="designation" required>
            <option value="">Select</option>
            <option value="Student" ${submission.designation == 'Student' ? 'selected' : ''}>Student</option>
            <option value="Faculty" ${submission.designation == 'Faculty' ? 'selected' : ''}>Faculty</option>
            <option value="Research scholar" ${submission.designation == 'Research scholar' ? 'selected' : ''}>Research scholar</option>
            <option value="Participant" ${submission.designation == 'Participant' ? 'selected' : ''}>Participant</option>
          </select>
        </div>
        <button class="btn btn-gold w-100" type="submit">Generate certificate PDF</button>
      </form>
    </div>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
