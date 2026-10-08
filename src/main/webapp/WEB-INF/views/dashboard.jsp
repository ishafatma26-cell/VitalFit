<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.vitalfit.model.User" %>
<%@ page import="com.vitalfit.model.Workout" %>
<%@ page import="com.vitalfit.model.Goal" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login");
        return;
    }
    List<Workout> recentWorkouts = (List<Workout>) request.getAttribute("recentWorkouts");
    List<Goal> goals = (List<Goal>) request.getAttribute("goals");
    String currentDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
    int currentStreak = request.getAttribute("currentStreak") != null ? (Integer) request.getAttribute("currentStreak") : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - VitalFit</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Fixed Header Navigation Bar -->
    <nav class="navbar">
        <a href="dashboard" class="nav-brand">
            <div class="brand-icon">⚡</div>
            <span>VitalFit</span>
        </a>
        <div class="nav-links">
            <a href="dashboard" class="nav-link active">Dashboard</a>
            <a href="workouts" class="nav-link">Workouts</a>
            <a href="challenges" class="nav-link">Challenges</a>
            <a href="leaderboard" class="nav-link">Leaderboard</a>
            <% if (currentUser.isAdmin()) { %>
                <a href="admin" class="nav-link" style="color: var(--warning);">Admin Panel</a>
            <% } %>
            <div class="user-badge">
                <span style="font-size: 0.85rem; font-weight: 700;"><%= currentUser.getName() %></span>
                <span class="role-tag"><%= currentUser.getRole() %></span>
            </div>
            <a href="logout" class="btn btn-danger" style="padding: 0.35rem 0.85rem; font-size: 0.8rem;">Logout</a>
        </div>
    </nav>

    <div class="main-container">

        <!-- Hero Welcome Banner -->
        <div class="glass-panel welcome-hero">
            <div>
                <h1 class="page-title">Welcome back, <%= currentUser.getName() %>!</h1>
                <p class="page-subtitle">Today is <%= currentDateStr %>. Keep push limits and tracking live fitness metrics.</p>
            </div>
            <div style="display: flex; gap: 1rem; align-items: center;">
                <div style="background: rgba(6, 182, 212, 0.15); border: 1px solid rgba(6, 182, 212, 0.3); padding: 0.5rem 1.25rem; border-radius: 30px; display: flex; align-items: center; gap: 0.5rem;">
                    <span style="font-size: 1.2rem;">🔥</span>
                    <div>
                        <div style="font-size: 0.7rem; color: var(--text-muted); font-weight: 700; text-transform: uppercase;">Active Streak</div>
                        <div style="font-size: 1.1rem; font-weight: 800; color: var(--accent-cyan);"><%= currentStreak %> Days Streak</div>
                    </div>
                </div>
                <a href="workouts" class="btn btn-primary">+ Log Workout</a>
            </div>
        </div>

        <!-- 4-Card Responsive Metric Grid -->
        <div class="grid-stats">
            <div class="glass-panel stat-card">
                <span class="stat-label">Total Workouts Logged</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= request.getAttribute("totalWorkouts") != null ? request.getAttribute("totalWorkouts") : 0 %></span>
                    <span class="stat-unit">Sessions</span>
                </div>
            </div>
            <div class="glass-panel stat-card">
                <span class="stat-label">Cumulative Calories Burned</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= request.getAttribute("totalCalories") != null ? request.getAttribute("totalCalories") : 0 %></span>
                    <span class="stat-unit">kcal</span>
                </div>
            </div>
            <div class="glass-panel stat-card">
                <span class="stat-label">Active Goals & Challenges</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= request.getAttribute("activeGoalsCount") != null ? request.getAttribute("activeGoalsCount") : 0 %></span>
                    <span class="stat-unit">Active</span>
                </div>
            </div>
            <div class="glass-panel stat-card">
                <span class="stat-label">Daily Streak Count</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= currentStreak %></span>
                    <span class="stat-unit">Days</span>
                </div>
            </div>
        </div>

        <!-- Main Dashboard Content Grid -->
        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1.5rem;">

            <!-- Recent Activity List -->
            <div class="glass-panel" style="padding: 1.5rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
                    <h3 style="font-size: 1.2rem; font-weight: 700;">Recent Workout Preview</h3>
                    <a href="workouts" style="color: var(--accent-cyan); font-size: 0.85rem; text-decoration: none; font-weight: 700;">View All Workouts &rarr;</a>
                </div>

                <div class="table-container">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Activity</th>
                                <th>Intensity</th>
                                <th>Date</th>
                                <th>Duration</th>
                                <th>Calories</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (recentWorkouts != null && !recentWorkouts.isEmpty()) {
                                for (Workout w : recentWorkouts) { %>
                                    <tr>
                                        <td><strong style="color: #fff;"><%= w.getActivityType() %></strong></td>
                                        <td><span class="badge-intensity <%= w.getIntensity() %>"><%= w.getIntensity() %></span></td>
                                        <td><%= w.getWorkoutDate() %></td>
                                        <td><%= w.getDurationMinutes() %> mins</td>
                                        <td><span style="color: var(--accent-cyan); font-weight: 800;"><%= w.getCaloriesBurned() %> kcal</span></td>
                                    </tr>
                            <%  }
                               } else { %>
                                <tr>
                                    <td colspan="5" style="text-align: center; color: var(--text-muted); padding: 2.5rem;">No workouts recorded yet. Click "+ Log Workout" to start!</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Target Goals Progress -->
            <div class="glass-panel" style="padding: 1.5rem;">
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem;">Target Goals Progress</h3>

                <% if (goals != null && !goals.isEmpty()) {
                    for (Goal g : goals) { %>
                        <div style="margin-bottom: 1.25rem; padding-bottom: 1rem; border-bottom: 1px solid rgba(255, 255, 255, 0.05);">
                            <div style="display: flex; justify-content: space-between; margin-bottom: 0.35rem;">
                                <strong style="font-size: 0.95rem; color: #fff;"><%= g.getTitle() %></strong>
                                <span style="font-size: 0.85rem; color: var(--accent-cyan); font-weight: 800;"><%= g.getProgressPercentage() %>%</span>
                            </div>
                            <div style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.5rem;">
                                <%= g.getCurrentValue() %> / <%= g.getTargetValue() %> <%= g.getUnit() %>
                            </div>
                            <div class="progress-bar-bg">
                                <div class="progress-bar-fill" style="width: <%= g.getProgressPercentage() %>%;"></div>
                            </div>
                        </div>
                <%  }
                   } else { %>
                    <p style="color: var(--text-muted); font-size: 0.9rem; text-align: center; margin-top: 1.5rem;">No goals set yet. Add a target goal under the Workouts tab.</p>
                <% } %>
            </div>

        </div>
    </div>

</body>
</html>
