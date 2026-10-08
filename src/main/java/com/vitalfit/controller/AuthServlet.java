package com.vitalfit.controller;

import com.vitalfit.dao.UserDAO;
import com.vitalfit.model.User;
import com.vitalfit.service.ActivityLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    ActivityLogService.logAsync(user.getEmail(), "LOGOUT", "User logged out successfully");
                }
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=logged_out");
        } else {
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            req.setAttribute("errorMessage", "Email and password are required.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userDAO.authenticate(email.trim(), password);
            if (user != null) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                ActivityLogService.logAsync(user.getEmail(), "LOGIN", "User authenticated successfully");

                if (user.isAdmin()) {
                    resp.sendRedirect(req.getContextPath() + "/admin");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                }
            } else {
                ActivityLogService.logAsync(email, "LOGIN_FAILED", "Invalid credentials provided");
                req.setAttribute("errorMessage", "Invalid email or password.");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Database error during authentication: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (name == null || email == null || password == null || name.isBlank() || email.isBlank() || password.isBlank()) {
            req.setAttribute("errorMessage", "All registration fields are required.");
            req.getRequestDispatcher("/login.jsp?tab=register").forward(req, resp);
            return;
        }

        try {
            if (userDAO.findByEmail(email.trim()) != null) {
                req.setAttribute("errorMessage", "Account with this email already exists.");
                req.getRequestDispatcher("/login.jsp?tab=register").forward(req, resp);
                return;
            }

            User newUser = new User(name.trim(), email.trim(), null, "USER");
            boolean success = userDAO.registerUser(newUser, password);

            if (success) {
                ActivityLogService.logAsync(email, "REGISTER", "New user registered successfully");
                HttpSession session = req.getSession(true);
                session.setAttribute("user", newUser);
                resp.sendRedirect(req.getContextPath() + "/dashboard?msg=registered");
            } else {
                req.setAttribute("errorMessage", "Failed to register user. Please try again.");
                req.getRequestDispatcher("/login.jsp?tab=register").forward(req, resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Registration error: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp?tab=register").forward(req, resp);
        }
    }
}
