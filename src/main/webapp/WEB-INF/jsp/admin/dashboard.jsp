<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Seminars — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
  <div>
    <p class="brand-mark mb-1">Admin</p>
    <h1 class="h3 mb-0">Seminars</h1>
  </div>
  <a class="btn btn-gold sans" href="${pageContext.request.contextPath}/admin/seminars/new">Create seminar</a>
</div>

<div class="row g-3 mb-4 sans">
  <div class="col-md-4">
    <div class="card-quiet p-3">
      <div class="text-secondary small">Total seminars</div>
      <div class="fs-3">${totalCount}</div>
    </div>
  </div>
  <div class="col-md-4">
    <div class="card-quiet p-3">
      <div class="text-secondary small">Waiting on developer</div>
      <div class="fs-3">${pendingCount}</div>
    </div>
  </div>
  <div class="col-md-4">
    <div class="card-quiet p-3">
      <div class="text-secondary small">Approved for linking</div>
      <div class="fs-3">${approvedCount}</div>
    </div>
  </div>
</div>

<form class="card-quiet p-3 mb-3 sans" method="get" action="${pageContext.request.contextPath}/admin">
  <div class="row g-2 align-items-end">
    <div class="col-md-6">
      <label class="form-label small text-secondary mb-1" for="q">Search title or organiser</label>
      <input class="form-control" id="q" name="q" value="${q}" placeholder="e.g. Cloud-Native" maxlength="120">
    </div>
    <div class="col-md-4">
      <label class="form-label small text-secondary mb-1" for="status">Status</label>
      <select class="form-select" id="status" name="status">
        <option value="" ${empty statusFilter ? 'selected' : ''}>All statuses</option>
        <option value="DRAFT" ${statusFilter == 'DRAFT' ? 'selected' : ''}>Draft — upload design</option>
        <option value="PENDING_APPROVAL" ${statusFilter == 'PENDING_APPROVAL' ? 'selected' : ''}>Pending developer approval</option>
        <option value="APPROVED" ${statusFilter == 'APPROVED' ? 'selected' : ''}>Approved</option>
      </select>
    </div>
    <div class="col-md-2">
      <button class="btn btn-navy w-100" type="submit">Filter</button>
    </div>
  </div>
</form>

<div class="card-quiet p-0 overflow-hidden">
  <c:choose>
    <c:when test="${empty seminars}">
      <div class="p-5 text-center text-secondary">
        <c:choose>
          <c:when test="${not empty q or not empty statusFilter}">
            No seminars match this search. Clear the filters to see the full list.
          </c:when>
          <c:otherwise>
            No seminars yet. Create one and upload the certificate artwork from the design team.
          </c:otherwise>
        </c:choose>
      </div>
    </c:when>
    <c:otherwise>
      <div class="table-responsive">
        <table class="table mb-0 align-middle">
          <thead>
          <tr>
            <th>Seminar</th>
            <th>Date</th>
            <th>Certificate</th>
            <th>Links</th>
            <th>Downloads</th>
            <th></th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="s" items="${seminars}">
            <tr>
              <td>
                <strong>${s.title}</strong>
                <div class="small text-secondary">${s.organizer}</div>
              </td>
              <td>${s.seminarDateLabel}</td>
              <td><span class="status-pill status-${s.status}">${s.statusLabel}</span></td>
              <td>${s.linkCount}</td>
              <td>${s.downloadCount}</td>
              <td class="text-end">
                <a class="btn btn-sm btn-navy" href="${pageContext.request.contextPath}/admin/seminars?id=${s.id}">Open</a>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
      <jsp:include page="/WEB-INF/jsp/includes/pager.jsp"/>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
