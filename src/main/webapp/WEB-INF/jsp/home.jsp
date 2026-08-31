<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<section class="hero">
  <p class="brand-mark mb-3">For seminar organisers</p>
  <h1>Issue personalised participation certificates without redesigning the layout each time.</h1>
  <p class="mt-3 mb-0" style="max-width: 38rem; color:#f6f1e7">
    Admin staff create a seminar, upload the finished certificate artwork, and wait for the developer to lock field positions.
    Once approved, a time-bound link can be copied into a WhatsApp group. Attendees fill one fixed form and download a PDF.
  </p>
  <div class="mt-4 d-flex flex-wrap gap-2">
    <a class="btn btn-gold" href="${pageContext.request.contextPath}/login">Staff sign in</a>
    <a class="btn btn-outline-light" href="${pageContext.request.contextPath}/c/demo-nwcj-2026">Try the demo attendee form</a>
  </div>
</section>

<div class="row g-4 mt-1">
  <div class="col-md-4">
    <article class="card-quiet p-4 h-100">
      <h2 class="h5">1. Admin</h2>
      <p class="mb-0 text-secondary sans">Save seminar details, upload a <strong>1920 × 1358</strong> certificate image, and generate a link after approval.</p>
    </article>
  </div>
  <div class="col-md-4">
    <article class="card-quiet p-4 h-100">
      <h2 class="h5">2. Developer</h2>
      <p class="mb-0 text-secondary sans">Place the six fixed fields on that seminar’s artwork, then approve. Approval unlocks link generation.</p>
    </article>
  </div>
  <div class="col-md-4">
    <article class="card-quiet p-4 h-100">
      <h2 class="h5">3. Attendee</h2>
      <p class="mb-0 text-secondary sans">Open the shared link, submit the form, and receive a PDF with their details drawn onto the design.</p>
    </article>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
