package com.certify.util;

import java.util.regex.Pattern;

public final class StaffCredentials {
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z][A-Za-z0-9._-]{2,31}$");

    private StaffCredentials() {}

    public static String normalizeUsername(String raw) {
        return raw == null ? "" : raw.trim();
    }

    public static String normalizeDisplayName(String raw) {
        return raw == null ? "" : raw.trim();
    }

    public static boolean isValidUsername(String username) {
        return USERNAME.matcher(username).matches();
    }

    public static boolean isValidDisplayName(String displayName) {
        return displayName.length() >= 2 && displayName.length() <= 120;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 72) {
            return false;
        }
        boolean letter = false;
        boolean digit = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLetter(c)) {
                letter = true;
            } else if (Character.isDigit(c)) {
                digit = true;
            }
        }
        return letter && digit;
    }
}
