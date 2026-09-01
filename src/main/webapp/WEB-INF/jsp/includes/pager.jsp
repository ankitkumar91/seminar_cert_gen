<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${totalPages > 1}">
  <nav class="pager-bar sans d-flex flex-wrap justify-content-between align-items-center gap-2 px-3 py-2" aria-label="List pages">
    <span class="small text-secondary">
      Showing ${(page - 1) * pageSize + 1}–${page * pageSize > totalRows ? totalRows : page * pageSize} of ${totalRows}
    </span>
    <div class="d-flex gap-2">
      <c:if test="${page > 1}">
        <a class="btn btn-sm btn-outline-secondary" href="${listPath}?page=${page - 1}${filterQuery}">Previous</a>
      </c:if>
      <c:if test="${page < totalPages}">
        <a class="btn btn-sm btn-navy" href="${listPath}?page=${page + 1}${filterQuery}">Next</a>
      </c:if>
    </div>
  </nav>
</c:if>
<c:if test="${totalPages == 1 and totalRows > 0}">
  <div class="px-3 py-2 small text-secondary sans">${totalRows} seminar<c:if test="${totalRows != 1}">s</c:if></div>
</c:if>
