package com.certify.model;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class Submission {
    private long id;
    private long seminarId;
    private Long shareLinkId;
    private String fullName;
    private String email;
    private String phone;
    private String institute;
    private String speciality;
    private String designation;
    private Instant createdAt;
    private String ipAddress;

    public Map<String, String> asFieldMap() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(FormField.FULL_NAME.key(), fullName);
        m.put(FormField.EMAIL.key(), email);
        m.put(FormField.PHONE.key(), phone);
        m.put(FormField.INSTITUTE.key(), institute);
        m.put(FormField.SPECIALITY.key(), speciality);
        m.put(FormField.DESIGNATION.key(), designation);
        return m;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getSeminarId() { return seminarId; }
    public void setSeminarId(long seminarId) { this.seminarId = seminarId; }
    public Long getShareLinkId() { return shareLinkId; }
    public void setShareLinkId(Long shareLinkId) { this.shareLinkId = shareLinkId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getInstitute() { return institute; }
    public void setInstitute(String institute) { this.institute = institute; }
    public String getSpeciality() { return speciality; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
