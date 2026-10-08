package com.vitalfit.controller;

import com.vitalfit.dao.ChallengeDAO;
import com.vitalfit.dao.UserDAO;
import com.vitalfit.dao.WorkoutDAO;
import com.vitalfit.model.User;
import com.vitalfit.model.Workout;
import com.vitalfit.service.ActivityLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO;
    private WorkoutDAO workoutDAO;
    private ChallengeDAO challengeDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        workoutDAO = new WorkoutDAO();
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
            // Refresh user details
            User refreshedUser = userDAO.findById(user.getId());
            if (refreshedUser != null) {
                session.setAttribute("user", refreshedUser);
                user = refreshedUser;
            }

            List<Workout> workouts = workoutDAO.getWorkoutsByUserId(user.getId());
            Set<Integer> enrolledChallengeIds = challengeDAO.getUserChallengeIds(user.getId());

            int totalWorkouts = workouts.size();
            int totalCalories = workouts.stream().mapToInt(Workout::getCaloriesBurned).sum();
            int totalDurationMinutes = workouts.stream().mapToInt(Workout::getDurationMinutes).sum();
            int currentStreak = computeStreakDays(workouts);

            int totalHours = totalDurationMinutes / 60;
            int remainingMins = totalDurationMinutes % 60;

            req.setAttribute("totalWorkouts", totalWorkouts);
            req.setAttribute("totalCalories", totalCalories);
            req.setAttribute("totalHours", totalHours);
            req.setAttribute("remainingMins", remainingMins);
            req.setAttribute("currentStreak", currentStreak);
            req.setAttribute("enrolledChallengesCount", enrolledChallengeIds.size());

            req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error loading profile details: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
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

        String newName = req.getParameter("name");

        if (newName != null && !newName.isBlank()) {
            try {
                boolean updated = userDAO.updateUserName(user.getId(), newName.trim());
                if (updated) {
                    user.setName(newName.trim());
                    session.setAttribute("user", user);
                    ActivityLogService.logAsync(user.getEmail(), "UPDATE_PROFILE", "Updated name to: " + newName.trim());
                    req.setAttribute("flashMessage", "Profile name updated successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                req.setAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            }
        }

        doGet(req, resp);
    }

    private int computeStreakDays(List<Workout> workouts) {
        if (workouts.isEmpty()) return 0;

        Set<LocalDate> dates = workouts.stream()
                .map(w -> w.getWorkoutDate().toLocalDate())
                .collect(Collectors.toCollection(TreeSet::new));

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (!dates.contains(today) && !dates.contains(yesterday)) {
            return 0;
        }

        LocalDate current = dates.contains(today) ? today : yesterday;
        int streak = 0;

        while (dates.contains(current)) {
            streak++;
            current = current.minusDays(1);
        }

        return streak;
    }
}
