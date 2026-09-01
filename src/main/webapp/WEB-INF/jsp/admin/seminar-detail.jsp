<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${seminar.title} — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-start gap-3 mb-4">
  <div>
    <p class="brand-mark mb-1">Admin · seminar</p>
    <h1 class="h3 mb-2">${seminar.title}</h1>
    <span class="status-pill status-${seminar.status}">${seminar.statusLabel}</span>
  </div>
  <a class="btn btn-outline-secondary sans" href="${pageContext.request.contextPath}/admin/seminars/edit?id=${seminar.id}">Edit details</a>
</div>

<div class="row g-4">
  <div class="col-lg-7">
    <section class="card-quiet p-4 mb-4">
      <h2 class="h5">Seminar details</h2>
      <dl class="row sans mb-0">
        <dt class="col-sm-4">Organiser</dt><dd class="col-sm-8">${empty seminar.organizer ? '—' : seminar.organizer}</dd>
        <dt class="col-sm-4">Venue</dt><dd class="col-sm-8">${empty seminar.venue ? '—' : seminar.venue}</dd>
        <dt class="col-sm-4">Date</dt><dd class="col-sm-8">${seminar.seminarDateLabel}</dd>
        <dt class="col-sm-4">Downloads</dt><dd class="col-sm-8">${submissionCount}</dd>
      </dl>
      <c:if test="${not empty seminar.description}">
        <p class="mt-3 mb-0 text-secondary">${seminar.description}</p>
      </c:if>
    </section>

    <section class="card-quiet p-4 mb-4">
      <h2 class="h5">Certificate design</h2>
      <p class="sans text-secondary">Upload a single flat image at <strong>${certWidth} × ${certHeight} px</strong> (PNG or JPEG). The artwork must already include college name, logo, and static copy. Re-uploading resets approval.</p>
      <form method="post" enctype="multipart/form-data" class="sans">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
        <input type="hidden" name="id" value="${seminar.id}">
        <input type="hidden" name="action" value="upload">
        <div class="mb-3">
          <input class="form-control" type="file" name="template" accept="image/png,image/jpeg" required>
        </div>
        <button class="btn btn-navy" type="submit">Upload and send for alignment</button>
      </form>
      <c:if test="${seminar.hasTemplate()}">
        <div class="mt-4">
          <img class="img-fluid rounded border" alt="Certificate template"
               src="${pageContext.request.contextPath}/media/template?seminarId=${seminar.id}">
        </div>
      </c:if>
    </section>
  </div>

  <div class="col-lg-5">
    <section class="card-quiet p-4 mb-4">
      <h2 class="h5">Shareable link</h2>
      <c:choose>
        <c:when test="${seminar.approved}">
          <p class="sans text-secondary">Set when the attendee form should stop accepting submissions. Copy the URL into WhatsApp yourself — the system does not send messages.</p>
          <form method="post" class="sans cert-form">
            <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
            <input type="hidden" name="id" value="${seminar.id}">
            <input type="hidden" name="action" value="createLink">
            <div class="mb-3">
              <label class="form-label" for="expiresAt">Expires</label>
              <input class="form-control" id="expiresAt" name="expiresAt" type="datetime-local" required>
            </div>
            <div class="mb-3">
              <label class="form-label" for="note">Internal note (optional)</label>
              <input class="form-control" id="note" name="note" maxlength="255" placeholder="WhatsApp group — CSE 2024">
            </div>
            <button class="btn btn-gold w-100" type="submit">Generate link</button>
          </form>
        </c:when>
        <c:otherwise>
          <p class="sans mb-0 text-secondary">Link generation unlocks after the developer aligns the fields and approves this design.</p>
        </c:otherwise>
      </c:choose>
    </section>

    <section class="card-quiet p-4">
      <h2 class="h5">Issued links</h2>
      <c:choose>
        <c:when test="${empty links}">
          <p class="sans text-secondary mb-0">No links yet.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="link" items="${links}">
            <div class="mb-3 pb-3 border-bottom">
              <div class="d-flex justify-content-between gap-2">
                <span class="small ${link.expired ? 'text-danger' : 'text-success'}">${link.expired ? 'Expired' : 'Active'} · ${link.expiresAtLabel}</span>
              </div>
              <div class="link-box mt-2">${publicBase}/c/${link.token}</div>
              <button class="btn btn-sm btn-outline-secondary mt-2 sans" type="button"
                      data-copy="${publicBase}/c/${link.token}">Copy link</button>
              <c:if test="${not empty link.note}"><div class="small text-secondary mt-1">${link.note}</div></c:if>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </section>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
