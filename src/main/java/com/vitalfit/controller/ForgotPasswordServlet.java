package com.vitalfit.controller;

import com.vitalfit.dao.UserDAO;
import com.vitalfit.util.XssSanitizer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Controller for self-service password reset.
 *
 * @author ADITYA KUMAR SINGH (25SCSE1180275) - Team NOVA
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute("csrfToken", UUID.randomUUID().toString());
        }
        request.setAttribute("csrfToken", session.getAttribute("csrfToken"));
        request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = XssSanitizer.sanitize(request.getParameter("email"));
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (email == null || email.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            return;
        }

        email = email.trim().toLowerCase();

        if (newPassword.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters long.");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match. Please try again.");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            return;
        }

        try {
            if (!userDAO.existsByEmail(email)) {
                request.setAttribute("error", "No account found with this email address.");
                request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
                return;
            }

            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
            boolean updated = userDAO.updatePasswordByEmail(email, hashed);

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/login?msg=password_reset_success");
            } else {
                request.setAttribute("error", "Failed to reset password. Please try again later.");
                request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            log("Database error during password reset", e);
            request.setAttribute("error", "An internal error occurred. Please try again later.");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
        }
    }
}
