<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Staff sign in — Certificate Desk" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<div class="row justify-content-center">
  <div class="col-lg-5">
    <div class="card-quiet p-4 p-md-5">
      <p class="brand-mark mb-2">Staff access</p>
      <h1 class="h3 mb-3">Sign in to Certificate Desk</h1>
      <p class="text-secondary sans">Admin accounts manage seminars and links. Developer accounts align text on each certificate image.</p>
      <form method="post" action="${pageContext.request.contextPath}/login" class="sans cert-form">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
        <div class="mb-3">
          <label class="form-label" for="username">Username</label>
          <input class="form-control" id="username" name="username" required autocomplete="username">
        </div>
        <div class="mb-4">
          <label class="form-label" for="password">Password</label>
          <input class="form-control" id="password" name="password" type="password" required autocomplete="current-password">
        </div>
        <button class="btn btn-navy w-100" type="submit">Sign in</button>
      </form>
      <p class="small text-secondary mt-4 mb-0 sans">
        Demo accounts — admin: <code>admin</code> / <code>Admin@123</code><br>
        developer: <code>developer</code> / <code>Dev@123</code>
      </p>
    </div>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
