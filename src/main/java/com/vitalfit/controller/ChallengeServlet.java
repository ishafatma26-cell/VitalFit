package com.vitalfit.controller;

import com.vitalfit.dao.ChallengeDAO;
import com.vitalfit.model.Challenge;
import com.vitalfit.model.User;
import com.vitalfit.service.ActivityLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/challenges")
public class ChallengeServlet extends HttpServlet {

    private ChallengeDAO challengeDAO;

    @Override
    public void init() {
        challengeDAO = new ChallengeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<Challenge> challenges = challengeDAO.getAllChallenges(user.getId());
            req.setAttribute("challenges", challenges);
            req.getRequestDispatcher("/WEB-INF/views/challenges.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error loading challenges: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/challenges.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        try {
            if ("join".equalsIgnoreCase(action)) {
                int challengeId = Integer.parseInt(req.getParameter("challengeId"));
                // ACID Transaction executed inside challengeDAO.joinChallenge
                boolean joined = challengeDAO.joinChallenge(user.getId(), challengeId);
                if (joined) {
                    req.getSession().setAttribute("flashMessage", "Successfully joined the challenge!");
                } else {
                    req.getSession().setAttribute("flashMessage", "You have already joined or couldn't join this challenge.");
                }
            } else if ("create".equalsIgnoreCase(action) && user.isAdmin()) {
                String title = req.getParameter("title");
                String description = req.getParameter("description");
                String targetType = req.getParameter("targetType");
                double targetGoal = Double.parseDouble(req.getParameter("targetGoal"));
                Date startDate = Date.valueOf(req.getParameter("startDate"));
                Date endDate = Date.valueOf(req.getParameter("endDate"));

                Challenge ch = new Challenge(0, title, description, targetType, targetGoal, startDate, endDate);
                challengeDAO.createChallenge(ch);
                ActivityLogService.logAsync(user.getEmail(), "CREATE_CHALLENGE", "Created challenge: " + title);
                req.getSession().setAttribute("flashMessage", "New challenge created successfully!");
            }

            resp.sendRedirect(req.getContextPath() + "/challenges");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("flashMessage", "Error processing challenge request: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/challenges");
        }
    }
}
