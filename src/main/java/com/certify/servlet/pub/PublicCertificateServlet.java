package com.certify.servlet.pub;

import com.certify.config.AppConfig;
import com.certify.dao.FieldPositionDao;
import com.certify.dao.SeminarDao;
import com.certify.dao.ShareLinkDao;
import com.certify.dao.SubmissionDao;
import com.certify.model.FormField;
import com.certify.model.Seminar;
import com.certify.model.ShareLink;
import com.certify.model.Submission;
import com.certify.util.PdfGenerator;
import com.certify.util.WebUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

@WebServlet("/c/*")
public class PublicCertificateServlet extends HttpServlet {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s]{8,20}$");

    private final ShareLinkDao links = new ShareLinkDao();
    private final SeminarDao seminars = new SeminarDao();
    private final FieldPositionDao positions = new FieldPositionDao();
    private final SubmissionDao submissions = new SubmissionDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Resolved resolved = resolve(req, resp);
        if (resolved == null) {
            return;
        }
        if (resolved.link.isExpired()) {
            req.setAttribute("seminar", resolved.seminar);
            req.getRequestDispatcher("/WEB-INF/jsp/public/expired.jsp").forward(req, resp);
            return;
        }
        WebUtil.ensureCsrf(req.getSession(true));
        req.setAttribute("seminar", resolved.seminar);
        req.setAttribute("token", resolved.link.getToken());
        req.setAttribute("expiresAt", resolved.link.getExpiresAtLabel());
        req.setAttribute("fields", FormField.values());
        req.getRequestDispatcher("/WEB-INF/jsp/public/form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Resolved resolved = resolve(req, resp);
        if (resolved == null) {
            return;
        }
        if (resolved.link.isExpired()) {
            req.setAttribute("seminar", resolved.seminar);
            req.getRequestDispatcher("/WEB-INF/jsp/public/expired.jsp").forward(req, resp);
            return;
        }
        if (!WebUtil.validCsrf(req)) {
            WebUtil.flash(req, "danger", "Your session expired. Please submit the form again.");
            resp.sendRedirect(req.getContextPath() + "/c/" + resolved.link.getToken());
            return;
        }

        Submission sub = new Submission();
        sub.setSeminarId(resolved.seminar.getId());
        sub.setShareLinkId(resolved.link.getId());
        sub.setFullName(WebUtil.trim(req.getParameter(FormField.FULL_NAME.key())));
        sub.setEmail(WebUtil.trim(req.getParameter(FormField.EMAIL.key())));
        sub.setPhone(WebUtil.trim(req.getParameter(FormField.PHONE.key())));
        sub.setCollege(WebUtil.trim(req.getParameter(FormField.COLLEGE.key())));
        sub.setEnrollmentNo(WebUtil.trim(req.getParameter(FormField.ENROLLMENT.key())));
        sub.setDesignation(WebUtil.trim(req.getParameter(FormField.DESIGNATION.key())));
        sub.setIpAddress(WebUtil.clientIp(req));

        String error = validate(sub);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("submission", sub);
            req.setAttribute("seminar", resolved.seminar);
            req.setAttribute("token", resolved.link.getToken());
            req.setAttribute("expiresAt", resolved.link.getExpiresAtLabel());
            req.setAttribute("fields", FormField.values());
            req.getRequestDispatcher("/WEB-INF/jsp/public/form.jsp").forward(req, resp);
            return;
        }

        submissions.insert(sub);
        Path fontDir = Path.of(getServletContext().getRealPath("/WEB-INF/fonts"));
        Path template = AppConfig.uploadsDir().resolve(resolved.seminar.getTemplateRelpath());
        byte[] pdf = new PdfGenerator(fontDir).generate(
                template, resolved.seminar, positions.findBySeminar(resolved.seminar.getId()), sub.asFieldMap());

        String filename = "certificate-" + slug(sub.getFullName()) + ".pdf";
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        resp.setContentLength(pdf.length);
        resp.getOutputStream().write(pdf);
    }

    private String validate(Submission sub) {
        if (WebUtil.isBlank(sub.getFullName()) || sub.getFullName().length() > 120) {
            return "Enter your full name as it should appear on the certificate.";
        }
        if (!EMAIL.matcher(sub.getEmail()).matches()) {
            return "Enter a valid email address.";
        }
        if (!PHONE.matcher(sub.getPhone()).matches()) {
            return "Enter a valid mobile number.";
        }
        if (WebUtil.isBlank(sub.getCollege())) {
            return "Enter your college or organisation name.";
        }
        if (WebUtil.isBlank(sub.getEnrollmentNo())) {
            return "Enter your enrollment or roll number.";
        }
        if (WebUtil.isBlank(sub.getDesignation())) {
            return "Select your role.";
        }
        return null;
    }

    private String slug(String name) {
        String s = name.toLowerCase().replaceAll("[^a-z0-9]+", "-");
        s = s.replaceAll("^-+|-+$", "");
        return s.isEmpty() ? "participant" : s;
    }

    private Resolved resolve(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || path.length() < 2) {
            resp.sendError(404);
            return null;
        }
        String token = path.substring(1);
        ShareLink link = links.findByToken(token);
        if (link == null) {
            req.getRequestDispatcher("/WEB-INF/jsp/public/not-found.jsp").forward(req, resp);
            return null;
        }
        Seminar seminar = seminars.find(link.getSeminarId());
        if (seminar == null || !seminar.hasTemplate()) {
            req.getRequestDispatcher("/WEB-INF/jsp/public/not-found.jsp").forward(req, resp);
            return null;
        }
        return new Resolved(link, seminar);
    }

    private record Resolved(ShareLink link, Seminar seminar) {}
}
