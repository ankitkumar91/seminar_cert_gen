package com.certify.util;

import com.certify.config.AppConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import java.io.IOException;
import java.util.UUID;

public final class WebUtil {
    private WebUtil() {}

    public static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static void flash(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession();
        session.setAttribute(AppConfig.FLASH, message);
        session.setAttribute(AppConfig.FLASH_TYPE, type);
    }

    public static void ensureCsrf(HttpSession session) {
        if (session.getAttribute(AppConfig.CSRF) == null) {
            session.setAttribute(AppConfig.CSRF, UUID.randomUUID().toString());
        }
    }

    public static boolean validCsrf(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        Object token = session.getAttribute(AppConfig.CSRF);
        String provided = req.getParameter("csrf");
        return token != null && token.toString().equals(provided);
    }

    public static String contextUrl(HttpServletRequest req) {
        String forwardedProto = req.getHeader("X-Forwarded-Proto");
        String scheme = forwardedProto != null ? forwardedProto : req.getScheme();
        String host = req.getHeader("X-Forwarded-Host");
        if (host == null) {
            host = req.getHeader("Host");
        }
        if (host == null) {
            int port = req.getServerPort();
            host = req.getServerName() + ((port == 80 || port == 443) ? "" : ":" + port);
        }
        String ctx = req.getContextPath();
        return scheme + "://" + host + ctx;
    }

    public static String publicLink(HttpServletRequest req, String token) {
        return contextUrl(req) + "/c/" + token;
    }

    public static String filenameOf(Part part) {
        String submitted = part.getSubmittedFileName();
        if (submitted == null) {
            return "";
        }
        int slash = Math.max(submitted.lastIndexOf('/'), submitted.lastIndexOf('\\'));
        return slash >= 0 ? submitted.substring(slash + 1) : submitted;
    }

    public static String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot).toLowerCase();
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    public static void consume(Part part) {
        if (part != null) {
            try {
                part.delete();
            } catch (IOException ignored) {
                // ignore
            }
        }
    }
}
