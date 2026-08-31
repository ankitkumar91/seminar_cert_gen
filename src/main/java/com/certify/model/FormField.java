package com.certify.model;

/**
 * Fixed attendee form fields, identical for every seminar.
 * The developer chooses a subset of these to print on the certificate.
 */
public enum FormField {
    FULL_NAME("fullName", "Full name", true),
    EMAIL("email", "Email", true),
    PHONE("phone", "Mobile number", false),
    INSTITUTE("institute", "Institute", true),
    SPECIALITY("speciality", "Speciality", true),
    DESIGNATION("designation", "Designation", true);

    private final String key;
    private final String label;
    private final boolean required;

    FormField(String key, String label, boolean required) {
        this.key = key;
        this.label = label;
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public boolean isRequired() {
        return required;
    }

    public String key() {
        return key;
    }

    public String label() {
        return label;
    }

    public boolean required() {
        return required;
    }

    public static FormField fromKey(String key) {
        if (key == null) {
            return null;
        }
        if ("college".equals(key)) {
            return INSTITUTE;
        }
        if ("enrollmentNo".equals(key) || "enrollment_no".equals(key)) {
            return SPECIALITY;
        }
        for (FormField f : values()) {
            if (f.key.equals(key)) {
                return f;
            }
        }
        return null;
    }
}
