<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Certificate — ${seminar.title}" scope="request"/>
<c:set var="hideStaffSignIn" value="true" scope="request"/>
<c:set var="loadFormValidate" value="true" scope="request"/>
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
      <p class="sans">Fill in your details. After you submit, a PDF downloads automatically. This link stops working at ${expiresAt}.</p>

      <c:if test="${not empty error}">
        <div class="alert alert-danger sans" id="formError">${error}</div>
      </c:if>
      <div class="alert alert-danger sans d-none" id="clientError" role="alert"></div>

      <form method="post" class="sans cert-form" id="attendeeForm" novalidate>
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
        <div class="mb-3">
          <label class="form-label" for="fullName">Full name <span class="text-danger">*</span></label>
          <input class="form-control" id="fullName" name="fullName" required maxlength="120"
                 value="${submission.fullName}" placeholder="As it should appear on the certificate" autocomplete="name">
        </div>
        <div class="mb-3">
          <label class="form-label" for="email">Email <span class="text-danger">*</span></label>
          <input class="form-control" id="email" name="email" type="email" required maxlength="200"
                 value="${submission.email}" placeholder="name@institute.edu" autocomplete="email"
                 pattern="[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}">
          <div class="form-text">Use a working email with a domain, for example ananya@college.edu.</div>
        </div>
        <div class="mb-3">
          <label class="form-label" for="phone">Mobile number</label>
          <input class="form-control" id="phone" name="phone" type="tel" maxlength="20"
                 value="${submission.phone}" placeholder="10-digit number (optional)" autocomplete="tel"
                 inputmode="tel">
          <div class="form-text">Optional. 10-digit Indian mobile, with or without +91.</div>
        </div>
        <div class="mb-3">
          <label class="form-label" for="institute">Institute <span class="text-danger">*</span></label>
          <input class="form-control" id="institute" name="institute" required maxlength="255"
                 value="${submission.institute}" placeholder="College or hospital name">
        </div>
        <div class="mb-3">
          <label class="form-label" for="speciality">Speciality <span class="text-danger">*</span></label>
          <input class="form-control" id="speciality" name="speciality" required maxlength="120"
                 value="${submission.speciality}" placeholder="e.g. Cardiology, Computer Science">
        </div>
        <div class="mb-4">
          <label class="form-label" for="designation">Designation <span class="text-danger">*</span></label>
          <input class="form-control" id="designation" name="designation" required maxlength="80"
                 value="${submission.designation}" placeholder="e.g. Professor, Resident, Student" list="designationHints">
          <datalist id="designationHints">
            <option value="Professor">
            <option value="Associate Professor">
            <option value="Assistant Professor">
            <option value="Consultant">
            <option value="Resident">
            <option value="Student">
          </datalist>
        </div>
        <button class="btn btn-gold w-100" type="submit">Generate certificate PDF</button>
      </form>
    </div>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
