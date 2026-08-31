<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Align ${seminar.title}" scope="request"/>
<c:set var="loadAlign" value="true" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<p class="brand-mark mb-1">Developer · alignment</p>
<h1 class="h4 mb-2">${seminar.title}</h1>
<p class="text-secondary sans">Drag each box onto the artwork. Resize from the right edge. Font size is in pixels at the native ${certWidth}×${certHeight} resolution.</p>

<form method="post" class="sans">
  <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
  <input type="hidden" name="id" value="${seminar.id}">

  <div class="align-stage mb-4" data-native-width="${certWidth}">
    <img src="${pageContext.request.contextPath}/media/template?seminarId=${seminar.id}" alt="Certificate">
    <c:forEach var="p" items="${positions}">
      <c:set var="label" value="${p.fieldKey}"/>
      <c:forEach var="f" items="${fields}">
        <c:if test="${f.key == p.fieldKey}"><c:set var="label" value="${f.label}"/></c:if>
      </c:forEach>
      <div class="field-box" data-key="${p.fieldKey}" data-x="${p.XPercent}" data-y="${p.YPercent}" data-w="${p.widthPercent}">
        ${label}
        <span class="handle"></span>
      </div>
    </c:forEach>
  </div>

  <div class="row g-3 align-controls">
    <c:forEach var="p" items="${positions}">
      <c:set var="label" value="${p.fieldKey}"/>
      <c:forEach var="f" items="${fields}">
        <c:if test="${f.key == p.fieldKey}"><c:set var="label" value="${f.label}"/></c:if>
      </c:forEach>
      <div class="col-md-6" data-field="${p.fieldKey}">
        <div class="card-quiet p-3 h-100">
          <strong>${label}</strong>
          <div class="row g-2 mt-1">
            <div class="col-4">
              <label class="form-label small mb-0">X %</label>
              <input class="form-control form-control-sm" name="${p.fieldKey}_x" value="${p.XPercent}">
            </div>
            <div class="col-4">
              <label class="form-label small mb-0">Y %</label>
              <input class="form-control form-control-sm" name="${p.fieldKey}_y" value="${p.YPercent}">
            </div>
            <div class="col-4">
              <label class="form-label small mb-0">Width %</label>
              <input class="form-control form-control-sm" name="${p.fieldKey}_w" value="${p.widthPercent}">
            </div>
            <div class="col-4">
              <label class="form-label small mb-0">Size</label>
              <input class="form-control form-control-sm" type="number" min="10" max="96" name="${p.fieldKey}_size" value="${p.fontSize}">
            </div>
            <div class="col-4">
              <label class="form-label small mb-0">Colour</label>
              <input class="form-control form-control-sm form-control-color" type="color" name="${p.fieldKey}_color" value="${p.fontColor}">
            </div>
            <div class="col-4">
              <label class="form-label small mb-0">Align</label>
              <select class="form-select form-select-sm" name="${p.fieldKey}_align">
                <option value="left" ${p.textAlign == 'left' ? 'selected' : ''}>Left</option>
                <option value="center" ${p.textAlign == 'center' ? 'selected' : ''}>Centre</option>
                <option value="right" ${p.textAlign == 'right' ? 'selected' : ''}>Right</option>
              </select>
            </div>
            <div class="col-12">
              <label class="small"><input type="checkbox" name="${p.fieldKey}_bold" ${p.fontBold ? 'checked' : ''}> Bold</label>
            </div>
          </div>
        </div>
      </div>
    </c:forEach>
  </div>

  <div class="d-flex flex-wrap gap-2 mt-4">
    <button class="btn btn-outline-secondary" type="submit" name="action" value="save">Save positions</button>
    <button class="btn btn-gold" type="submit" name="action" value="approve">Save and approve for linking</button>
    <a class="btn btn-link" href="${pageContext.request.contextPath}/developer">Back to queue</a>
  </div>
</form>
<jsp:include page="/WEB-INF/jsp/includes/footer.jsp"/>
