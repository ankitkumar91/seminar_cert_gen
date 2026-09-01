package com.certify.servlet.dev;

import com.certify.dao.UserDao;
import com.certify.model.Role;
import com.certify.model.User;
import com.certify.util.StaffCredentials;
import com.certify.util.WebUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AdminAccountsServlet extends HttpServlet {
    private final UserDao users = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("admins", users.findAdmins());
        req.getRequestDispatcher("/WEB-INF/jsp/developer/admin-accounts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!WebUtil.validCsrf(req)) {
            WebUtil.flash(req, "danger", "Your session expired. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }
        String action = WebUtil.trim(req.getParameter("action"));
        if ("revoke".equals(action)) {
            revoke(req, resp);
            return;
        }
        create(req, resp);
    }

    private void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = StaffCredentials.normalizeUsername(req.getParameter("username"));
        String displayName = StaffCredentials.normalizeDisplayName(req.getParameter("displayName"));
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");

        if (!StaffCredentials.isValidUsername(username)) {
            WebUtil.flash(req, "danger",
                    "Username must start with a letter and be 3–32 characters (letters, digits, dot, underscore, hyphen).");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }
        if (!StaffCredentials.isValidDisplayName(displayName)) {
            WebUtil.flash(req, "danger", "Display name must be between 2 and 120 characters.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }
        if (!StaffCredentials.isValidPassword(password)) {
            WebUtil.flash(req, "danger", "Password must be 8–72 characters and include a letter and a number.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }
        if (password == null || !password.equals(confirm)) {
            WebUtil.flash(req, "danger", "Password and confirmation do not match.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }

        User existing = users.findByUsername(username);
        if (existing != null) {
            if (existing.getRole() != Role.ADMIN) {
                WebUtil.flash(req, "danger", "That username is already used by a developer account.");
                resp.sendRedirect(req.getContextPath() + "/developer/admins");
                return;
            }
            if (existing.isActive()) {
                WebUtil.flash(req, "danger", "An active admin already uses that username.");
                resp.sendRedirect(req.getContextPath() + "/developer/admins");
                return;
            }
            users.restoreAdmin(existing.getId(), displayName, password);
            WebUtil.flash(req, "success",
                    "Restored admin “" + username + "” with a new password. They can sign in immediately.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }

        users.insertAdmin(username, displayName, password);
        WebUtil.flash(req, "success", "Created admin account “" + username + "”. Share the password out of band.");
        resp.sendRedirect(req.getContextPath() + "/developer/admins");
    }

    private void revoke(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id;
        try {
            id = Long.parseLong(WebUtil.trim(req.getParameter("id")));
        } catch (NumberFormatException e) {
            WebUtil.flash(req, "danger", "That admin account could not be found.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }
        User target = users.find(id);
        if (target == null || target.getRole() != Role.ADMIN) {
            WebUtil.flash(req, "danger", "That admin account could not be found.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }
        if (!target.isActive()) {
            WebUtil.flash(req, "warning", "That admin login is already revoked.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }
        if (users.countActiveAdmins() <= 1) {
            WebUtil.flash(req, "danger", "Keep at least one active admin so seminars can still be managed.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }
        if (!users.revokeAdmin(id)) {
            WebUtil.flash(req, "danger", "Could not revoke that admin login.");
            resp.sendRedirect(req.getContextPath() + "/developer/admins");
            return;
        }
        WebUtil.flash(req, "success",
                "Revoked admin “" + target.getUsername() + "”. That username and password no longer sign in.");
        resp.sendRedirect(req.getContextPath() + "/developer/admins");
    }
}
