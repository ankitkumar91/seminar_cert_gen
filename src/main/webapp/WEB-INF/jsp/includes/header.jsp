<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${empty pageTitle ? "Certificate Desk" : pageTitle}</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Source+Sans+3:wght@400;600;700&family=Source+Serif+4:opsz,wght@8..60,500;8..60,650&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet">
</head>
<body class="app-body">
<header class="site-header">
  <div class="container page-wrap py-3 d-flex justify-content-between align-items-center gap-3">
    <a href="${pageContext.request.contextPath}/" class="text-decoration-none brand-lockup">
      <img class="brand-logo" src="${pageContext.request.contextPath}/assets/img/elsevier-logo.png"
           width="125" height="130" alt="Elsevier">
      <span>
        <div class="brand-mark">Elsevier</div>
        <p class="brand-title mb-0">Certificate Desk</p>
      </span>
    </a>
    <nav class="d-flex align-items-center gap-3 sans">
      <c:choose>
        <c:when test="${not empty sessionScope.authUser}">
          <c:if test="${sessionScope.authUser.role == 'ADMIN'}">
            <a class="nav-link px-0" href="${pageContext.request.contextPath}/admin">Seminars</a>
          </c:if>
          <c:if test="${sessionScope.authUser.role == 'DEVELOPER'}">
            <a class="nav-link px-0" href="${pageContext.request.contextPath}/developer">Alignment queue</a>
            <a class="nav-link px-0" href="${pageContext.request.contextPath}/developer/admins">Admin accounts</a>
          </c:if>
          <span class="small user-chip">${sessionScope.authUser.displayName}</span>
          <form method="post" action="${pageContext.request.contextPath}/logout" class="m-0">
            <button class="btn btn-sm btn-outline-light">Sign out</button>
          </form>
        </c:when>
        <c:when test="${not hideStaffSignIn}">
          <a class="btn btn-gold btn-sm" href="${pageContext.request.contextPath}/login">Staff sign in</a>
        </c:when>
      </c:choose>
    </nav>
  </div>
</header>
<main class="container page-wrap py-4">
  <c:if test="${not empty sessionScope.flashMessage}">
    <div class="alert alert-${sessionScope.flashType} sans" role="alert">${sessionScope.flashMessage}</div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
  </c:if>
