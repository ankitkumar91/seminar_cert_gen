package com.certify.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ShareLink {
    private long id;
    private long seminarId;
    private String token;
    private Instant expiresAt;
    private Instant createdAt;
    private Long createdBy;
    private String note;

    private static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public String getExpiresAtLabel() {
        return expiresAt == null ? "" : DISPLAY.format(expiresAt.atZone(ZoneId.systemDefault()));
    }

    public String getCreatedAtLabel() {
        return createdAt == null ? "" : DISPLAY.format(createdAt.atZone(ZoneId.systemDefault()));
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getSeminarId() { return seminarId; }
    public void setSeminarId(long seminarId) { this.seminarId = seminarId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
