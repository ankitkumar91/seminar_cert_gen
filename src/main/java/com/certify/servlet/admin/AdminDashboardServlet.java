package com.certify.servlet.admin;

import com.certify.dao.SeminarDao;
import com.certify.dao.ShareLinkDao;
import com.certify.dao.SubmissionDao;
import com.certify.model.Seminar;
import com.certify.model.SeminarStatus;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardServlet extends HttpServlet {
    private final SeminarDao seminars = new SeminarDao();
    private final SubmissionDao submissions = new SubmissionDao();
    private final ShareLinkDao links = new ShareLinkDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Seminar> all = seminars.findAll();
        Map<Long, Integer> counts = new LinkedHashMap<>();
        Map<Long, Integer> linkCounts = new LinkedHashMap<>();
        int pending = 0;
        int approved = 0;
        for (Seminar s : all) {
            if (s.getStatus() == SeminarStatus.PENDING_APPROVAL) pending++;
            if (s.getStatus() == SeminarStatus.APPROVED) approved++;
            counts.put(s.getId(), submissions.countBySeminar(s.getId()));
            linkCounts.put(s.getId(), links.findBySeminar(s.getId()).size());
        }
        req.setAttribute("seminars", all);
        req.setAttribute("submissionCounts", counts);
        req.setAttribute("linkCounts", linkCounts);
        req.setAttribute("pendingCount", pending);
        req.setAttribute("approvedCount", approved);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(req, resp);
    }
}
