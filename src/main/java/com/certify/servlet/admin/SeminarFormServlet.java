package com.certify.servlet.admin;

import com.certify.config.AppConfig;
import com.certify.dao.SeminarDao;
import com.certify.model.Seminar;
import com.certify.model.SeminarStatus;
import com.certify.model.User;
import com.certify.util.WebUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;

public class SeminarFormServlet extends HttpServlet {
    private final SeminarDao seminars = new SeminarDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            Seminar seminar = seminars.find(Long.parseLong(idParam));
            if (seminar == null) {
                resp.sendError(404);
                return;
            }
            req.setAttribute("seminar", seminar);
        }
        req.getRequestDispatcher("/WEB-INF/jsp/admin/seminar-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!WebUtil.validCsrf(req)) {
            WebUtil.flash(req, "danger", "Your session expired. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }
        String title = WebUtil.trim(req.getParameter("title"));
        if (title.isEmpty()) {
            WebUtil.flash(req, "danger", "Seminar title is required.");
            resp.sendRedirect(req.getContextPath() + "/admin/seminars/new");
            return;
        }
        User user = (User) req.getSession().getAttribute(AppConfig.SESSION_USER);
        String idParam = WebUtil.trim(req.getParameter("id"));
        Seminar s = idParam.isEmpty() ? new Seminar() : seminars.find(Long.parseLong(idParam));
        if (s == null) {
            resp.sendError(404);
            return;
        }
        s.setTitle(title);
        s.setDescription(WebUtil.trim(req.getParameter("description")));
        s.setVenue(WebUtil.trim(req.getParameter("venue")));
        s.setOrganizer(WebUtil.trim(req.getParameter("organizer")));
        String date = WebUtil.trim(req.getParameter("seminarDate"));
        s.setSeminarDate(date.isEmpty() ? null : LocalDate.parse(date));
        if (idParam.isEmpty()) {
            s.setStatus(SeminarStatus.DRAFT);
            s.setCreatedBy(user.getId());
            s.setCreatedAt(Instant.now());
            s.setUpdatedAt(Instant.now());
            long id = seminars.insert(s);
            WebUtil.flash(req, "success", "Seminar saved. Upload the certificate design to send it for alignment.");
            resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + id);
        } else {
            seminars.updateDetails(s);
            WebUtil.flash(req, "success", "Seminar details updated.");
            resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + s.getId());
        }
    }
}
