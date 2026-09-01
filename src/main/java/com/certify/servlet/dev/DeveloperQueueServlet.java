package com.certify.servlet.dev;

import com.certify.dao.SeminarDao;
import com.certify.dao.SeminarListQuery;
import com.certify.model.SeminarStatus;
import com.certify.util.Paging;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DeveloperQueueServlet extends HttpServlet {
    private final SeminarDao seminars = new SeminarDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SeminarListQuery filter = from(req);
        int totalRows = seminars.countDeveloper(filter);
        int page = Paging.clampPage(Paging.page(req), totalRows);

        req.setAttribute("seminars", seminars.findDeveloperPage(filter, page));
        req.setAttribute("q", filter.getQ());
        req.setAttribute("statusFilter", filter.getStatus() == null ? "" : filter.getStatus().name());
        req.setAttribute("page", page);
        req.setAttribute("pageSize", Paging.PAGE_SIZE);
        req.setAttribute("totalRows", totalRows);
        req.setAttribute("totalPages", Paging.totalPages(totalRows));
        req.setAttribute("listPath", req.getContextPath() + "/developer");
        req.setAttribute("filterQuery", querySuffix(filter));
        req.getRequestDispatcher("/WEB-INF/jsp/developer/queue.jsp").forward(req, resp);
    }

    private static SeminarListQuery from(HttpServletRequest req) {
        String q = req.getParameter("q");
        SeminarStatus status = null;
        String raw = req.getParameter("status");
        if (raw != null && !raw.isBlank()) {
            try {
                status = SeminarStatus.valueOf(raw.trim());
            } catch (IllegalArgumentException ignored) {
                status = null;
            }
        }
        return new SeminarListQuery(q, status);
    }

    private static String querySuffix(SeminarListQuery filter) {
        StringBuilder sb = new StringBuilder();
        if (filter.hasText()) {
            sb.append("&q=").append(URLEncoder.encode(filter.getQ(), StandardCharsets.UTF_8));
        }
        if (filter.getStatus() != null) {
            sb.append("&status=").append(filter.getStatus().name());
        }
        return sb.toString();
    }
}
