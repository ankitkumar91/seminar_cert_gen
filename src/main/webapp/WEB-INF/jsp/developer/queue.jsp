<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Developer queue — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<p class="brand-mark mb-1">Developer</p>
<h1 class="h3 mb-2">Certificate alignment queue</h1>
<p class="text-secondary sans">Pending work is listed first. Open a seminar to place fields on its artwork. Approving the design lets the admin generate a public link.</p>

<form class="card-quiet p-3 mb-3 sans" method="get" action="${pageContext.request.contextPath}/developer">
  <div class="row g-2 align-items-end">
    <div class="col-md-6">
      <label class="form-label small text-secondary mb-1" for="q">Search title or organiser</label>
      <input class="form-control" id="q" name="q" value="${q}" placeholder="e.g. Faculty Development" maxlength="120">
    </div>
    <div class="col-md-4">
      <label class="form-label small text-secondary mb-1" for="status">Status</label>
      <select class="form-select" id="status" name="status">
        <option value="" ${empty statusFilter ? 'selected' : ''}>All (pending first)</option>
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
            No seminars match this search.
          </c:when>
          <c:otherwise>
            No certificate images have been uploaded yet.
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
            <th>Status</th>
            <th>Updated</th>
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
              <td><span class="status-pill status-${s.status}">${s.statusLabel}</span></td>
              <td>${s.updatedAtLabel}</td>
              <td class="text-end">
                <a class="btn btn-sm btn-navy" href="${pageContext.request.contextPath}/developer/align?id=${s.id}">Align fields</a>
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
