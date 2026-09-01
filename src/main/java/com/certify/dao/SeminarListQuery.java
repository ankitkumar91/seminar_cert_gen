package com.certify.dao;

import com.certify.model.SeminarStatus;

public final class SeminarListQuery {
    private final String q;
    private final SeminarStatus status;

    public SeminarListQuery(String q, SeminarStatus status) {
        this.q = q == null ? "" : q.trim();
        this.status = status;
    }

    public String getQ() {
        return q;
    }

    public SeminarStatus getStatus() {
        return status;
    }

    public boolean hasText() {
        return !q.isEmpty();
    }

    public String likePattern() {
        String escaped = q.toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }
}
