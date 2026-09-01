package com.certify.servlet.admin;

import com.certify.dao.SeminarListQuery;
import com.certify.model.SeminarStatus;
import javax.servlet.http.HttpServletRequest;

final class ListFilters {
    private ListFilters() {}

    static SeminarListQuery from(HttpServletRequest req) {
        String q = req.getParameter("q");
        SeminarStatus status = null;
        String raw = req.getParameter("status");
        if (raw != null && !raw.isBlank()) {
            try {
                status = SeminarStatus.valueOf(raw.trim());
            } catch (IllegalArgumentException ignored) {
                status = null;
            }
        }
        return new SeminarListQuery(q, status);
    }
}
