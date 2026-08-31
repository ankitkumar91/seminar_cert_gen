package com.certify.util;

import java.util.regex.Pattern;

public final class InputValidator {
    /**
     * Local-part @ domain with a TLD of at least two letters.
     * Rejects spaces, consecutive dots, and missing domain labels.
     */
    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,63}$");

    private InputValidator() {}

    public static boolean isValidEmail(String value) {
        if (value == null) {
            return false;
        }
        String email = value.trim();
        if (email.length() < 6 || email.length() > 200) {
            return false;
        }
        if (email.contains("..") || email.startsWith(".") || email.contains(".@")) {
            return false;
        }
        return EMAIL.matcher(email).matches();
    }

    /**
     * Phone is optional. When present: 10–15 digits, optional +91 / 91 prefix,
     * spaces hyphens and parentheses allowed.
     */
    public static boolean isValidPhone(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        String compact = value.trim().replaceAll("[\\s().-]", "");
        if (compact.startsWith("+")) {
            compact = compact.substring(1);
        }
        if (!compact.matches("\\d{10,15}")) {
            return false;
        }
        if (compact.length() == 12 && compact.startsWith("91")) {
            compact = compact.substring(2);
        } else if (compact.length() == 11 && compact.startsWith("0")) {
            compact = compact.substring(1);
        }
        if (compact.length() == 10) {
            char first = compact.charAt(0);
            return first >= '6' && first <= '9';
        }
        return compact.length() >= 10 && compact.length() <= 15;
    }

    public static String digitsOnlyPhone(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9+]", "").trim();
    }
}
