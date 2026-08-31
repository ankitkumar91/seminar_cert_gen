<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Developer queue — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<p class="brand-mark mb-1">Developer</p>
<h1 class="h3 mb-4">Certificate alignment queue</h1>
<p class="text-secondary sans">Open a seminar to place the six standard fields on its artwork. Approving the design lets the admin generate a public link.</p>

<div class="card-quiet p-0 overflow-hidden">
  <c:choose>
    <c:when test="${empty seminars}">
      <div class="p-5 text-center text-secondary">No certificate images have been uploaded yet.</div>
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
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
