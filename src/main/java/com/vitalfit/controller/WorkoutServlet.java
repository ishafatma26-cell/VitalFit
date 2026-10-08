package com.vitalfit.controller;

import com.vitalfit.dao.WorkoutDAO;
import com.vitalfit.model.Goal;
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
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/workouts")
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
            } else if ("addGoal".equalsIgnoreCase(action)) {
                String title = req.getParameter("title");
                double target = Double.parseDouble(req.getParameter("targetValue"));
                String unit = req.getParameter("unit");

                Goal goal = new Goal(0, user.getId(), title, target, 0.0, unit, "IN_PROGRESS");
                workoutDAO.addGoal(goal);
                ActivityLogService.logAsync(user.getEmail(), "ADD_GOAL", "Created goal: " + title);
            } else {
                // Add Workout
                String activityType = req.getParameter("activityType");
                int duration = Integer.parseInt(req.getParameter("duration"));
                int calories = Integer.parseInt(req.getParameter("calories"));
                double distance = Double.parseDouble(req.getParameter("distance"));
                String dateStr = req.getParameter("workoutDate");
                String notes = req.getParameter("notes");

                Date workoutDate = (dateStr != null && !dateStr.isBlank()) ? Date.valueOf(dateStr) : new Date(System.currentTimeMillis());

                Workout w = new Workout(0, user.getId(), activityType, duration, calories, distance, workoutDate, notes);
                workoutDAO.addWorkout(w);
                ActivityLogService.logAsync(user.getEmail(), "LOG_WORKOUT", "Logged workout: " + activityType + " (" + calories + " kcal)");
            }

            resp.sendRedirect(req.getContextPath() + "/workouts");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Action failed: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
