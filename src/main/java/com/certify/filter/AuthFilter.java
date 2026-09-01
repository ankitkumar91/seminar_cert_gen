package com.certify.filter;

import com.certify.config.AppConfig;
import com.certify.dao.UserDao;
import com.certify.model.Role;
import com.certify.model.User;
import com.certify.util.WebUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter extends HttpFilter {
    private final UserDao users = new UserDao();
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
        User live = users.find(user.getId());
        if (live == null || !live.isActive()) {
            session.invalidate();
            WebUtil.flash(req, "warning", "This login has been revoked. Sign in with an active account.");
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        session.setAttribute(AppConfig.SESSION_USER, live);
        user = live;
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
