package com.certify.filter;

import com.certify.config.AppConfig;
import com.certify.model.Role;
import com.certify.model.User;
import com.certify.util.WebUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/admin", "/admin/*", "/developer", "/developer/*"})
public class AuthFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(AppConfig.SESSION_USER);
        if (user == null) {
            WebUtil.flash(req, "warning", "Please sign in to continue.");
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String path = req.getServletPath();
        if (path.startsWith("/admin") && user.getRole() != Role.ADMIN) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (path.startsWith("/developer") && user.getRole() != Role.DEVELOPER) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        WebUtil.ensureCsrf(session);
        req.setAttribute("currentUser", user);
        chain.doFilter(req, res);
    }
}
