package com.certify.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Seminar {
    private long id;
    private String title;
    private String description;
    private String venue;
    private LocalDate seminarDate;
    private String organizer;
    private SeminarStatus status;
    private String templateRelpath;
    private Integer templateWidth;
    private Integer templateHeight;
    private long createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Long approvedBy;
    private Instant approvedAt;
    private int linkCount;
    private int downloadCount;

    public boolean hasTemplate() {
        return templateRelpath != null && !templateRelpath.isBlank();
    }

    public boolean isApproved() {
        return status == SeminarStatus.APPROVED;
    }

    public String getSeminarDateLabel() {
        return seminarDate == null ? "Date not set" : seminarDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public String getStatusLabel() {
        return switch (status) {
            case DRAFT -> "Draft — upload design";
            case PENDING_APPROVAL -> "Pending developer approval";
            case APPROVED -> "Approved";
        };
    }

    public String getUpdatedAtLabel() {
        if (updatedAt == null) {
            return "";
        }
        return DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm").format(updatedAt.atZone(ZoneId.systemDefault()));
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public LocalDate getSeminarDate() { return seminarDate; }
    public void setSeminarDate(LocalDate seminarDate) { this.seminarDate = seminarDate; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public SeminarStatus getStatus() { return status; }
    public void setStatus(SeminarStatus status) { this.status = status; }
    public String getTemplateRelpath() { return templateRelpath; }
    public void setTemplateRelpath(String templateRelpath) { this.templateRelpath = templateRelpath; }
    public Integer getTemplateWidth() { return templateWidth; }
    public void setTemplateWidth(Integer templateWidth) { this.templateWidth = templateWidth; }
    public Integer getTemplateHeight() { return templateHeight; }
    public void setTemplateHeight(Integer templateHeight) { this.templateHeight = templateHeight; }
    public long getCreatedBy() { return createdBy; }
    public void setCreatedBy(long createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }
    public int getLinkCount() { return linkCount; }
    public void setLinkCount(int linkCount) { this.linkCount = linkCount; }
    public int getDownloadCount() { return downloadCount; }
    public void setDownloadCount(int downloadCount) { this.downloadCount = downloadCount; }
}
