package com.certify.servlet.dev;

import com.certify.config.AppConfig;
import com.certify.dao.FieldPositionDao;
import com.certify.dao.SeminarDao;
import com.certify.model.FieldPosition;
import com.certify.model.FormField;
import com.certify.model.Seminar;
import com.certify.model.User;
import com.certify.util.WebUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/developer/align")
public class AlignmentServlet extends HttpServlet {
    private final SeminarDao seminars = new SeminarDao();
    private final FieldPositionDao positions = new FieldPositionDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Seminar seminar = load(req, resp);
        if (seminar == null) {
            return;
        }
        if (!seminar.hasTemplate()) {
            WebUtil.flash(req, "warning", "This seminar has no certificate image yet.");
            resp.sendRedirect(req.getContextPath() + "/developer");
            return;
        }
        positions.ensureDefaults(seminar.getId());
        req.setAttribute("seminar", seminar);
        req.setAttribute("positions", positions.findBySeminar(seminar.getId()));
        req.setAttribute("fields", FormField.values());
        req.setAttribute("certWidth", AppConfig.CERT_WIDTH);
        req.setAttribute("certHeight", AppConfig.CERT_HEIGHT);
        req.getRequestDispatcher("/WEB-INF/jsp/developer/align.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!WebUtil.validCsrf(req)) {
            WebUtil.flash(req, "danger", "Your session expired. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/developer");
            return;
        }
        Seminar seminar = load(req, resp);
        if (seminar == null) {
            return;
        }
        String action = WebUtil.trim(req.getParameter("action"));
        for (FormField field : FormField.values()) {
            FieldPosition p = new FieldPosition();
            p.setSeminarId(seminar.getId());
            p.setFieldKey(field.key());
            p.setXPercent(dbl(req, field.key() + "_x", 15));
            p.setYPercent(dbl(req, field.key() + "_y", 50));
            p.setWidthPercent(dbl(req, field.key() + "_w", 70));
            p.setFontSize((int) dbl(req, field.key() + "_size", 24));
            p.setFontColor(color(req.getParameter(field.key() + "_color")));
            p.setFontBold("on".equals(req.getParameter(field.key() + "_bold")));
            String align = WebUtil.trim(req.getParameter(field.key() + "_align"));
            p.setTextAlign(align.isEmpty() ? "center" : align);
            positions.upsert(p);
        }
        if ("approve".equals(action)) {
            User user = (User) req.getSession().getAttribute(AppConfig.SESSION_USER);
            seminars.approve(seminar.getId(), user.getId());
            WebUtil.flash(req, "success", "Certificate approved. The admin can now generate a shareable link.");
            resp.sendRedirect(req.getContextPath() + "/developer");
            return;
        }
        WebUtil.flash(req, "success", "Field positions saved.");
        resp.sendRedirect(req.getContextPath() + "/developer/align?id=" + seminar.getId());
    }

    private double dbl(HttpServletRequest req, String name, double fallback) {
        try {
            return Double.parseDouble(req.getParameter(name));
        } catch (Exception e) {
            return fallback;
        }
    }

    private String color(String raw) {
        if (raw != null && raw.matches("#[0-9A-Fa-f]{6}")) {
            return raw;
        }
        return "#1a2744";
    }

    private Seminar load(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (WebUtil.isBlank(id)) {
            resp.sendRedirect(req.getContextPath() + "/developer");
            return null;
        }
        Seminar seminar = seminars.find(Long.parseLong(id));
        if (seminar == null) {
            resp.sendError(404);
        }
        return seminar;
    }
}
