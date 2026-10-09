package com.vitalfit.controller;

import com.vitalfit.dao.WorkoutDAO;
import com.vitalfit.model.Goal;
import com.vitalfit.model.User;
import com.vitalfit.model.Workout;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private WorkoutDAO workoutDAO;

    @Override
    public void init() {
        workoutDAO = new WorkoutDAO();
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
            List<Workout> workouts = workoutDAO.getWorkoutsByUserId(user.getId());
            List<Goal> goals = workoutDAO.getGoalsByUserId(user.getId());

            // Compute summary metrics using Java Stream API
            int totalCalories = workouts.stream().mapToInt(Workout::getCaloriesBurned).sum();
            double totalDistance = workouts.stream().mapToDouble(Workout::getDistanceKm).sum();
            int totalWorkouts = workouts.size();
            long activeGoalsCount = goals.stream().filter(g -> "IN_PROGRESS".equalsIgnoreCase(g.getStatus())).count();

            int currentStreak = computeStreakDays(workouts);

            // Recent activity (latest 5 workouts)
            List<Workout> recentWorkouts = workouts.stream().limit(5).collect(Collectors.toList());

            req.setAttribute("totalCalories", totalCalories);
            req.setAttribute("totalDistance", Math.round(totalDistance * 100.0) / 100.0);
            req.setAttribute("totalWorkouts", totalWorkouts);
            req.setAttribute("activeGoalsCount", activeGoalsCount);
            req.setAttribute("currentStreak", currentStreak);
            req.setAttribute("recentWorkouts", recentWorkouts);
            req.setAttribute("goals", goals);

            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Unable to load dashboard data: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        }
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
