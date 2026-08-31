<%@ taglib prefix="c" uri="jakarta.tags.core" %>
</main>
<footer class="container page-wrap pb-5 footer-note sans">
  Certificate Desk overlays attendee details onto the design supplied for each seminar and issues a PDF. Links expire at the time the admin sets.
</footer>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
<c:if test="${loadAlign}">
  <script src="${pageContext.request.contextPath}/assets/js/align.js"></script>
</c:if>
</body>
</html>
