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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        List<FieldPosition> printed = positions.findBySeminar(seminar.getId());
        req.setAttribute("seminar", seminar);
        req.setAttribute("positions", printed);
        req.setAttribute("fields", FormField.values());
        req.setAttribute("availableFields", unusedFields(printed));
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
        if (action.startsWith("remove:")) {
            FormField field = FormField.fromKey(action.substring("remove:".length()));
            if (field != null) {
                positions.delete(seminar.getId(), field.key());
                WebUtil.flash(req, "success", field.label() + " will no longer print on this certificate.");
            }
            resp.sendRedirect(req.getContextPath() + "/developer/align?id=" + seminar.getId());
            return;
        }
        if ("add".equals(action)) {
            FormField field = FormField.fromKey(WebUtil.trim(req.getParameter("addField")));
            if (field == null) {
                WebUtil.flash(req, "danger", "Choose a form field to print on the certificate.");
            } else {
                positions.addField(seminar.getId(), field);
                WebUtil.flash(req, "success", field.label() + " added. Drag the box onto the artwork.");
            }
            resp.sendRedirect(req.getContextPath() + "/developer/align?id=" + seminar.getId());
            return;
        }

        for (FieldPosition existing : positions.findBySeminar(seminar.getId())) {
            String key = existing.getFieldKey();
            FieldPosition p = new FieldPosition();
            p.setSeminarId(seminar.getId());
            p.setFieldKey(key);
            p.setXPercent(dbl(req, key + "_x", existing.getXPercent()));
            p.setYPercent(dbl(req, key + "_y", existing.getYPercent()));
            p.setWidthPercent(dbl(req, key + "_w", existing.getWidthPercent()));
            p.setFontSize((int) dbl(req, key + "_size", existing.getFontSize()));
            p.setFontColor(color(req.getParameter(key + "_color")));
            p.setFontBold("on".equals(req.getParameter(key + "_bold")));
            String align = WebUtil.trim(req.getParameter(key + "_align"));
            p.setTextAlign(align.isEmpty() ? "center" : align);
            positions.upsert(p);
        }

        if ("approve".equals(action)) {
            if (positions.findBySeminar(seminar.getId()).isEmpty()) {
                WebUtil.flash(req, "danger", "Add at least one field to print before approving.");
                resp.sendRedirect(req.getContextPath() + "/developer/align?id=" + seminar.getId());
                return;
            }
            User user = (User) req.getSession().getAttribute(AppConfig.SESSION_USER);
            seminars.approve(seminar.getId(), user.getId());
            WebUtil.flash(req, "success", "Certificate approved. The admin can now generate a shareable link.");
            resp.sendRedirect(req.getContextPath() + "/developer");
            return;
        }
        WebUtil.flash(req, "success", "Printed fields and positions saved.");
        resp.sendRedirect(req.getContextPath() + "/developer/align?id=" + seminar.getId());
    }

    private List<FormField> unusedFields(List<FieldPosition> printed) {
        Set<String> used = new java.util.HashSet<>();
        for (FieldPosition p : printed) {
            used.add(p.getFieldKey());
        }
        List<FormField> unused = new ArrayList<>();
        for (FormField f : FormField.values()) {
            if (!used.contains(f.key())) {
                unused.add(f);
            }
        }
        return unused;
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
