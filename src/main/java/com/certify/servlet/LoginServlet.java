package com.certify.servlet;

import com.certify.config.AppConfig;
import com.certify.dao.UserDao;
import com.certify.model.Role;
import com.certify.model.User;
import com.certify.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private final UserDao users = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = current(req);
        if (user != null) {
            resp.sendRedirect(req.getContextPath() + home(user));
            return;
        }
        WebUtil.ensureCsrf(req.getSession());
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!WebUtil.validCsrf(req)) {
            WebUtil.flash(req, "danger", "Your session expired. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String username = WebUtil.trim(req.getParameter("username"));
        String password = req.getParameter("password");
        User user = users.authenticate(username, password);
        if (user == null) {
            WebUtil.flash(req, "danger", "Incorrect username or password.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        HttpSession session = req.getSession(true);
        session.invalidate();
        session = req.getSession(true);
        session.setAttribute(AppConfig.SESSION_USER, user);
        WebUtil.ensureCsrf(session);
        resp.sendRedirect(req.getContextPath() + home(user));
    }

    private User current(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(AppConfig.SESSION_USER);
    }

    static String home(User user) {
        return user.getRole() == Role.ADMIN ? "/admin" : "/developer";
    }
}
