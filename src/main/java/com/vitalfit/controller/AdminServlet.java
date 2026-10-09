package com.vitalfit.controller;

import com.vitalfit.dao.ChallengeDAO;
import com.vitalfit.dao.UserDAO;
import com.vitalfit.model.ActivityLog;
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
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private UserDAO userDAO;
    private ChallengeDAO challengeDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        challengeDAO = new ChallengeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<User> userList = userDAO.getAllUsers();
            List<ActivityLog> recentLogs = ActivityLogService.getRecentLogs(50);
            List<Challenge> challenges = challengeDAO.getAllChallenges(user.getId());

            req.setAttribute("users", userList);
            req.setAttribute("logs", recentLogs);
            req.setAttribute("challenges", challenges);
            req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error loading admin dashboard: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("updateRole".equalsIgnoreCase(action)) {
                int targetUserId = Integer.parseInt(req.getParameter("userId"));
                String newRole = req.getParameter("role");
                userDAO.updateUserRole(targetUserId, newRole);
                ActivityLogService.logAsync(user.getEmail(), "ADMIN_ROLE_UPDATE", "Changed user ID " + targetUserId + " role to " + newRole);
            } else if ("createChallenge".equalsIgnoreCase(action)) {
                String title = req.getParameter("title");
                String description = req.getParameter("description");
                String targetType = req.getParameter("targetType");
                double targetGoal = Double.parseDouble(req.getParameter("targetGoal"));
                Date startDate = Date.valueOf(req.getParameter("startDate"));
                Date endDate = Date.valueOf(req.getParameter("endDate"));

                Challenge ch = new Challenge(0, title, description, targetType, targetGoal, startDate, endDate);
                challengeDAO.createChallenge(ch);
                ActivityLogService.logAsync(user.getEmail(), "CREATE_CHALLENGE", "Created challenge: " + title);
            } else if ("deleteChallenge".equalsIgnoreCase(action)) {
                int challengeId = Integer.parseInt(req.getParameter("challengeId"));
                challengeDAO.deleteChallenge(challengeId);
                ActivityLogService.logAsync(user.getEmail(), "DELETE_CHALLENGE", "Deleted challenge ID " + challengeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/admin");
    }
}
