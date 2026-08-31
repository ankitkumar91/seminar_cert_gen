package com.certify.model;

/**
 * Fixed form fields identical for every seminar (scope 2.5 / section 5).
 * Pending client confirmation — this is the working set used in the first slice.
 */
public enum FormField {
    FULL_NAME("fullName", "Full name", true),
    EMAIL("email", "Email address", true),
    PHONE("phone", "Mobile number", true),
    COLLEGE("college", "College / organisation", true),
    ENROLLMENT("enrollmentNo", "Enrollment / roll number", true),
    DESIGNATION("designation", "Role", true);

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
        for (FormField f : values()) {
            if (f.key.equals(key)) {
                return f;
            }
        }
        return null;
    }
}
