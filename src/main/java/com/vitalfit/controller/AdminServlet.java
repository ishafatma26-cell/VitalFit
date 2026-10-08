package com.vitalfit.controller;

import com.vitalfit.dao.UserDAO;
import com.vitalfit.model.ActivityLog;
import com.vitalfit.model.User;
import com.vitalfit.service.ActivityLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
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

            req.setAttribute("users", userList);
            req.setAttribute("logs", recentLogs);
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
        if ("updateRole".equalsIgnoreCase(action)) {
            try {
                int targetUserId = Integer.parseInt(req.getParameter("userId"));
                String newRole = req.getParameter("role");
                userDAO.updateUserRole(targetUserId, newRole);
                ActivityLogService.logAsync(user.getEmail(), "ADMIN_ROLE_UPDATE", "Changed user ID " + targetUserId + " role to " + newRole);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin");
    }
}
