<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Admin accounts — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<p class="brand-mark mb-1">Developer</p>
<h1 class="h3 mb-2">Admin accounts</h1>
<p class="text-secondary sans">Create a username and password for seminar admins, or revoke a login so it can no longer sign in. Passwords are stored hashed; you will not be able to look them up later.</p>

<div class="row g-4">
  <div class="col-lg-5">
    <section class="card-quiet p-4">
      <h2 class="h5">Create admin login</h2>
      <p class="small text-secondary sans">Use a revoked username here to restore that account with a new password.</p>
      <form method="post" action="${pageContext.request.contextPath}/developer/admins" class="sans cert-form">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
        <input type="hidden" name="action" value="create">
        <div class="mb-3">
          <label class="form-label" for="username">Username</label>
          <input class="form-control" id="username" name="username" required maxlength="32" autocomplete="off"
                 pattern="[A-Za-z][A-Za-z0-9._-]{2,31}" placeholder="e.g. seminar.admin">
        </div>
        <div class="mb-3">
          <label class="form-label" for="displayName">Display name</label>
          <input class="form-control" id="displayName" name="displayName" required maxlength="120" autocomplete="name"
                 placeholder="Name shown in the header">
        </div>
        <div class="mb-3">
          <label class="form-label" for="password">Password</label>
          <input class="form-control" id="password" name="password" type="password" required minlength="8" maxlength="72"
                 autocomplete="new-password">
          <div class="form-text">At least 8 characters, with a letter and a number.</div>
        </div>
        <div class="mb-3">
          <label class="form-label" for="confirmPassword">Confirm password</label>
          <input class="form-control" id="confirmPassword" name="confirmPassword" type="password" required minlength="8"
                 maxlength="72" autocomplete="new-password">
        </div>
        <button class="btn btn-gold w-100" type="submit">Create admin account</button>
      </form>
    </section>
  </div>
  <div class="col-lg-7">
    <section class="card-quiet p-0 overflow-hidden">
      <div class="px-4 pt-4 pb-2">
        <h2 class="h5 mb-0">Existing admins</h2>
      </div>
      <c:choose>
        <c:when test="${empty admins}">
          <p class="px-4 pb-4 text-secondary sans mb-0">No admin accounts yet.</p>
        </c:when>
        <c:otherwise>
          <div class="table-responsive">
            <table class="table mb-0 align-middle">
              <thead>
              <tr>
                <th>Username</th>
                <th>Name</th>
                <th>Status</th>
                <th></th>
              </tr>
              </thead>
              <tbody>
              <c:forEach var="u" items="${admins}">
                <tr>
                  <td><code>${u.username}</code></td>
                  <td>${u.displayName}</td>
                  <td>
                    <c:choose>
                      <c:when test="${u.active}">
                        <span class="text-success small">Active</span>
                      </c:when>
                      <c:otherwise>
                        <span class="text-secondary small">Revoked</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-end">
                    <c:if test="${u.active}">
                      <form method="post" action="${pageContext.request.contextPath}/developer/admins" class="m-0"
                            onsubmit="return confirm('Revoke ${u.username}? They will not be able to sign in.');">
                        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="action" value="revoke">
                        <input type="hidden" name="id" value="${u.id}">
                        <button class="btn btn-sm btn-outline-danger" type="submit">Revoke</button>
                      </form>
                    </c:if>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:otherwise>
      </c:choose>
    </section>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
