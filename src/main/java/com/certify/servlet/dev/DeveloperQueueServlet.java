package com.certify.servlet.dev;

import com.certify.dao.SeminarDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/developer")
public class DeveloperQueueServlet extends HttpServlet {
    private final SeminarDao seminars = new SeminarDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("seminars", seminars.findForDeveloper());
        req.getRequestDispatcher("/WEB-INF/jsp/developer/queue.jsp").forward(req, resp);
    }
}
