package com.certify.util;

import javax.servlet.http.HttpServletRequest;

public final class Paging {
    public static final int PAGE_SIZE = 25;

    private Paging() {}

    public static int page(HttpServletRequest req) {
        String raw = req.getParameter("page");
        if (raw == null || raw.isBlank()) {
            return 1;
        }
        try {
            return Math.max(1, Integer.parseInt(raw.trim()));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public static int totalPages(int totalRows) {
        if (totalRows <= 0) {
            return 1;
        }
        return (totalRows + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    public static int clampPage(int page, int totalRows) {
        int pages = totalPages(totalRows);
        return Math.min(page, pages);
    }

    public static int offset(int page) {
        return (page - 1) * PAGE_SIZE;
    }
}
