<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
</main>
<footer class="site-footer">
  <div class="container page-wrap py-4 d-flex flex-wrap align-items-center gap-3 footer-note sans">
    <img class="footer-logo" src="${pageContext.request.contextPath}/assets/img/elsevier-logo.png" width="125" height="130" alt="Elsevier">
    <p class="mb-0">Certificate Desk overlays attendee details onto the design supplied for each seminar and issues a PDF. Links expire at the time the admin sets.</p>
  </div>
</footer>
<script src="${pageContext.request.contextPath}/assets/js/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
<c:if test="${loadAlign}">
  <script src="${pageContext.request.contextPath}/assets/js/align.js"></script>
</c:if>
<c:if test="${loadFormValidate}">
  <script src="${pageContext.request.contextPath}/assets/js/form-validate.js"></script>
</c:if>
</body>
</html>
