package com.vitalfit.controller;

import com.vitalfit.dao.WorkoutDAO;
import com.vitalfit.model.Goal;
import com.vitalfit.model.User;
import com.vitalfit.model.UserProfile;
import com.vitalfit.model.Workout;
import com.vitalfit.service.ActivityLogService;
import com.vitalfit.strategy.WorkoutCalculator;
import com.vitalfit.strategy.WorkoutCalculatorFactory;
import com.vitalfit.util.XssSanitizer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Workout tracking servlet supporting CRUD operations and CSV report export.
 *
 * @author Mukesh kumar (25SCSE1330101) - Team NOVA
 */
@WebServlet(urlPatterns = {"/workouts", "/user/workouts"})
public class WorkoutServlet extends HttpServlet {

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

        String action = req.getParameter("action");
        if ("exportCsv".equalsIgnoreCase(action)) {
            exportWorkoutCsv(user, resp);
            return;
        }

        try {
            List<Workout> workouts = workoutDAO.getWorkoutsByUserId(user.getId());
            List<Goal> goals = workoutDAO.getGoalsByUserId(user.getId());

            req.setAttribute("workouts", workouts);
            req.setAttribute("goals", goals);
            req.getRequestDispatcher("/WEB-INF/views/workouts.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error loading workouts: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/workouts.jsp").forward(req, resp);
        }
    }

    private void exportWorkoutCsv(User user, HttpServletResponse resp) throws IOException {
        try {
            List<Workout> workouts = workoutDAO.getWorkoutsByUserId(user.getId());
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"vitalfit_workouts_" + user.getId() + ".csv\"");

            try (PrintWriter writer = resp.getWriter()) {
                writer.println("Workout ID,Activity Type,Intensity,Duration (mins),Calories Burned (kcal),Distance (km),Date,Notes");
                for (Workout w : workouts) {
                    writer.printf("%d,\"%s\",\"%s\",%d,%d,%.2f,%s,\"%s\"%n",
                            w.getId(),
                            escapeCsv(w.getActivityType()),
                            escapeCsv(w.getIntensity()),
                            w.getDurationMinutes(),
                            w.getCaloriesBurned(),
                            w.getDistanceKm(),
                            w.getWorkoutDate() != null ? w.getWorkoutDate().toString() : "",
                            escapeCsv(w.getNotes()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to export workouts CSV");
        }
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\"");
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
            if ("delete".equalsIgnoreCase(action)) {
                int workoutId = Integer.parseInt(req.getParameter("id"));
                workoutDAO.deleteWorkout(workoutId, user.getId());
                ActivityLogService.logAsync(user.getEmail(), "DELETE_WORKOUT", "Deleted workout ID " + workoutId);
            } else if ("edit".equalsIgnoreCase(action)) {
                int workoutId = Integer.parseInt(req.getParameter("id"));
                String activityType = XssSanitizer.sanitize(req.getParameter("activityType"));
                String intensity = XssSanitizer.sanitize(req.getParameter("intensity"));
                if (intensity == null || intensity.isBlank()) {
                    intensity = "Medium";
                }
                int duration = Integer.parseInt(req.getParameter("duration"));
                int calories = Integer.parseInt(req.getParameter("calories"));
                String distStr = req.getParameter("distance");
                double distance = (distStr != null && !distStr.isBlank()) ? Double.parseDouble(distStr) : 0.0;
                String dateStr = req.getParameter("workoutDate");
                String notes = XssSanitizer.sanitize(req.getParameter("notes"));
                Date workoutDate = (dateStr != null && !dateStr.isBlank()) ? Date.valueOf(dateStr) : new Date(System.currentTimeMillis());

                Workout w = new Workout(workoutId, user.getId(), activityType, intensity, duration, calories, distance, workoutDate, notes);

                if (calories <= 0) {
                    UserProfile profile = new UserProfile();
                    profile.setUserId(user.getId());
                    WorkoutCalculator calc = WorkoutCalculatorFactory.getCalculator(activityType);
                    calories = (int) Math.round(calc.calculateCalories(w, profile));
                    w.setCaloriesBurned(calories);
                }

                workoutDAO.update(w);
                ActivityLogService.logAsync(user.getEmail(), "UPDATE_WORKOUT", "Updated workout ID " + workoutId);
            } else if ("addGoal".equalsIgnoreCase(action)) {
                String title = XssSanitizer.sanitize(req.getParameter("title"));
                double target = Double.parseDouble(req.getParameter("targetValue"));
                String unit = XssSanitizer.sanitize(req.getParameter("unit"));

                Goal goal = new Goal(0, user.getId(), title, target, 0.0, unit, "IN_PROGRESS");
                workoutDAO.addGoal(goal);
                ActivityLogService.logAsync(user.getEmail(), "ADD_GOAL", "Created goal: " + title);
            } else {
                String activityType = XssSanitizer.sanitize(req.getParameter("activityType"));
                String intensity = XssSanitizer.sanitize(req.getParameter("intensity"));
                if (intensity == null || intensity.isBlank()) {
                    intensity = "Medium";
                }
                int duration = Integer.parseInt(req.getParameter("duration"));
                String calStr = req.getParameter("calories");
                int calories = (calStr != null && !calStr.isBlank()) ? Integer.parseInt(calStr) : 0;
                String distStr = req.getParameter("distance");
                double distance = (distStr != null && !distStr.isBlank()) ? Double.parseDouble(distStr) : 0.0;
                String dateStr = req.getParameter("workoutDate");
                String notes = XssSanitizer.sanitize(req.getParameter("notes"));

                Date workoutDate = (dateStr != null && !dateStr.isBlank()) ? Date.valueOf(dateStr) : new Date(System.currentTimeMillis());

                Workout w = new Workout(0, user.getId(), activityType, intensity, duration, calories, distance, workoutDate, notes);

                if (calories <= 0) {
                    UserProfile profile = new UserProfile();
                    profile.setUserId(user.getId());
                    WorkoutCalculator calc = WorkoutCalculatorFactory.getCalculator(activityType);
                    calories = (int) Math.round(calc.calculateCalories(w, profile));
                    w.setCaloriesBurned(calories);
                }

                workoutDAO.addWorkout(w);
                ActivityLogService.logAsync(user.getEmail(), "LOG_WORKOUT", "Logged workout: " + activityType + " (" + intensity + ", " + calories + " kcal)");
            }

            resp.sendRedirect(req.getContextPath() + "/workouts");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Action failed: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
