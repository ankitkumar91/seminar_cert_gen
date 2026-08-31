package com.certify.servlet;

import com.certify.config.AppConfig;
import com.certify.dao.SeminarDao;
import com.certify.model.Role;
import com.certify.model.Seminar;
import com.certify.model.User;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemplateImageServlet extends HttpServlet {
    private final SeminarDao seminars = new SeminarDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(AppConfig.SESSION_USER);
        if (user == null || (user.getRole() != Role.ADMIN && user.getRole() != Role.DEVELOPER)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        long id = Long.parseLong(req.getParameter("seminarId"));
        Seminar seminar = seminars.find(id);
        if (seminar == null || !seminar.hasTemplate()) {
            resp.sendError(404);
            return;
        }
        Path file = AppConfig.uploadsDir().resolve(seminar.getTemplateRelpath()).normalize();
        if (!file.startsWith(AppConfig.uploadsDir()) || !Files.isRegularFile(file)) {
            resp.sendError(404);
            return;
        }
        String name = file.getFileName().toString().toLowerCase();
        resp.setContentType(name.endsWith(".png") ? "image/png" : "image/jpeg");
        resp.setHeader("Cache-Control", "private, max-age=60");
        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(file, out);
        }
    }
}
