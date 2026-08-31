<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Align ${seminar.title}" scope="request"/>
<c:set var="loadAlign" value="true" scope="request"/>
<jsp:include page="/WEB-INF/jsp/includes/header.jsp"/>

<p class="brand-mark mb-1">Developer · alignment</p>
<h1 class="h4 mb-2">${seminar.title}</h1>
<p class="text-secondary sans">
  Attendees always fill the same form (full name, email, mobile, institute, speciality, designation).
  Add only the fields that should be drawn on this seminar’s artwork. Remove a field to stop printing it.
</p>

<form method="post" class="sans mb-4">
  <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
  <input type="hidden" name="id" value="${seminar.id}">
  <div class="card-quiet p-3 d-flex flex-wrap align-items-end gap-2">
    <div class="flex-grow-1">
      <label class="form-label small mb-1" for="addField">Add a field to the certificate</label>
      <select class="form-select" id="addField" name="addField" ${empty availableFields ? 'disabled' : ''}>
        <c:if test="${empty availableFields}">
          <option>All form fields are already on this certificate</option>
        </c:if>
        <c:forEach var="f" items="${availableFields}">
          <option value="${f.key}">${f.label}<c:if test="${!f.required}"> (optional on form)</c:if></option>
        </c:forEach>
      </select>
    </div>
    <button class="btn btn-navy" type="submit" name="action" value="add" ${empty availableFields ? 'disabled' : ''}>Add to certificate</button>
  </div>
</form>

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

  <c:if test="${empty positions}">
    <p class="text-secondary">No fields are printed yet. Add at least full name from the list above.</p>
  </c:if>

  <div class="row g-3 align-controls">
    <c:forEach var="p" items="${positions}">
      <c:set var="label" value="${p.fieldKey}"/>
      <c:forEach var="f" items="${fields}">
        <c:if test="${f.key == p.fieldKey}"><c:set var="label" value="${f.label}"/></c:if>
      </c:forEach>
      <div class="col-md-6" data-field="${p.fieldKey}">
        <div class="card-quiet p-3 h-100">
          <div class="d-flex justify-content-between align-items-start gap-2">
            <strong>${label}</strong>
            <button class="btn btn-sm btn-outline-danger" type="submit" name="action" value="remove:${p.fieldKey}">Remove</button>
          </div>
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
