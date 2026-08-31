package com.certify.servlet.admin;

import com.certify.config.AppConfig;
import com.certify.dao.SeminarDao;
import com.certify.model.Seminar;
import com.certify.model.SeminarStatus;
import com.certify.model.User;
import com.certify.util.ImageValidator;
import com.certify.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@WebServlet("/admin/seminars")
@MultipartConfig(maxFileSize = AppConfig.MAX_UPLOAD_BYTES, fileSizeThreshold = 1024 * 1024)
public class SeminarDetailServlet extends HttpServlet {
    private final SeminarDao seminars = new SeminarDao();
    private final com.certify.dao.ShareLinkDao shareLinks = new com.certify.dao.ShareLinkDao();
    private final com.certify.dao.SubmissionDao submissions = new com.certify.dao.SubmissionDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Seminar seminar = load(req, resp);
        if (seminar == null) {
            return;
        }
        req.setAttribute("seminar", seminar);
        req.setAttribute("links", shareLinks.findBySeminar(seminar.getId()));
        req.setAttribute("submissionCount", submissions.countBySeminar(seminar.getId()));
        req.setAttribute("recentSubmissions", submissions.recentBySeminar(seminar.getId(), 8));
        req.setAttribute("certWidth", AppConfig.CERT_WIDTH);
        req.setAttribute("certHeight", AppConfig.CERT_HEIGHT);
        req.setAttribute("now", Instant.now());
        req.setAttribute("publicBase", WebUtil.contextUrl(req));
        req.getRequestDispatcher("/WEB-INF/jsp/admin/seminar-detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.validCsrf(req)) {
            WebUtil.flash(req, "danger", "Your session expired. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }
        String action = WebUtil.trim(req.getParameter("action"));
        if ("upload".equals(action)) {
            handleUpload(req, resp);
        } else if ("createLink".equals(action)) {
            handleLink(req, resp);
        } else {
            resp.sendError(400);
        }
    }

    private void handleUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Seminar seminar = load(req, resp);
        if (seminar == null) {
            return;
        }
        Part part = req.getPart("template");
        if (part == null || part.getSize() == 0) {
            WebUtil.flash(req, "danger", "Choose a certificate PNG or JPEG to upload.");
            resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + seminar.getId());
            return;
        }
        String filename = WebUtil.filenameOf(part);
        String ext = WebUtil.extensionOf(filename);
        if (!List.of(".png", ".jpg", ".jpeg").contains(ext)) {
            WebUtil.flash(req, "danger", "Upload a PNG or JPEG file.");
            resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + seminar.getId());
            return;
        }
        String storedExt = ext.equals(".png") ? ".png" : ".jpg";
        Path dir = AppConfig.uploadsDir().resolve("seminars/" + seminar.getId());
        Files.createDirectories(dir);
        Path dest = dir.resolve("template" + storedExt);
        part.write(dest.toAbsolutePath().toString());

        ImageValidator.Result result;
        try (InputStream in = Files.newInputStream(dest)) {
            result = ImageValidator.validate(in);
        }
        if (!result.ok()) {
            Files.deleteIfExists(dest);
            WebUtil.flash(req, "danger", result.message());
            resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + seminar.getId());
            return;
        }
        String rel = AppConfig.uploadsDir().relativize(dest).toString().replace('\\', '/');
        seminars.attachTemplate(seminar.getId(), rel, result.width(), result.height());
        WebUtil.flash(req, "success", "Design uploaded and sent to the developer panel for field alignment.");
        resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + seminar.getId());
    }

    private void handleLink(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Seminar seminar = load(req, resp);
        if (seminar == null) {
            return;
        }
        if (seminar.getStatus() != SeminarStatus.APPROVED) {
            WebUtil.flash(req, "danger", "A shareable link can be created only after the developer approves the design.");
            resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + seminar.getId());
            return;
        }
        String expiryRaw = WebUtil.trim(req.getParameter("expiresAt"));
        if (expiryRaw.isEmpty()) {
            WebUtil.flash(req, "danger", "Set an expiry date and time for the link.");
            resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + seminar.getId());
            return;
        }
        Instant expiresAt = LocalDateTime.parse(expiryRaw).atZone(ZoneId.systemDefault()).toInstant();
        if (!expiresAt.isAfter(Instant.now())) {
            WebUtil.flash(req, "danger", "Expiry must be in the future.");
            resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + seminar.getId());
            return;
        }
        User user = (User) req.getSession().getAttribute(AppConfig.SESSION_USER);
        String note = WebUtil.trim(req.getParameter("note"));
        shareLinks.create(seminar.getId(), expiresAt, user.getId(), note);
        WebUtil.flash(req, "success", "Link created. Copy it and share it on WhatsApp or email.");
        resp.sendRedirect(req.getContextPath() + "/admin/seminars?id=" + seminar.getId());
    }

    private Seminar load(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (WebUtil.isBlank(id)) {
            resp.sendRedirect(req.getContextPath() + "/admin");
            return null;
        }
        Seminar seminar = seminars.find(Long.parseLong(id));
        if (seminar == null) {
            resp.sendError(404);
        }
        return seminar;
    }
}
