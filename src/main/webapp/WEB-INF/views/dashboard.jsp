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
    String currentDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
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

    <!-- Navigation Bar -->
    <nav class="navbar">
        <a href="dashboard" class="nav-brand">
            <div class="brand-icon">⚡</div>
            <span>VitalFit</span>
        </a>
        <button class="mobile-nav-toggle" onclick="document.querySelector('.nav-links').classList.toggle('active')">☰</button>
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
            <a href="logout" class="btn btn-danger" style="padding: 0.35rem 0.85rem; font-size: 0.8rem; min-height: auto;">Logout</a>
        </div>
    </nav>

    <div class="main-container">

        <!-- Hero Welcome Banner -->
        <div class="card-panel welcome-hero">
            <div>
                <h1 class="page-title">Welcome back, <%= currentUser.getName() %>!</h1>
                <p class="page-subtitle">Track live workout telemetry, active goals, and streak metrics.</p>
            </div>
            <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
                <span class="badge-streak">🔥 <%= currentStreak %> Day Streak</span>
                <span class="badge-date">📅 <%= currentDateStr %></span>
                <a href="workouts" class="btn btn-emerald">+ Log Workout</a>
            </div>
        </div>

        <!-- 4-Card Metric Grid -->
        <div class="grid-stats">
            <div class="card-panel stat-card">
                <span class="stat-label">Total Workouts Logged</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= request.getAttribute("totalWorkouts") != null ? request.getAttribute("totalWorkouts") : 0 %></span>
                    <span class="stat-unit">Sessions</span>
                </div>
            </div>
            <div class="card-panel stat-card">
                <span class="stat-label">Calories Burned</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= request.getAttribute("totalCalories") != null ? request.getAttribute("totalCalories") : 0 %></span>
                    <span class="stat-unit">kcal</span>
                </div>
            </div>
            <div class="card-panel stat-card">
                <span class="stat-label">Active Challenges & Goals</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= request.getAttribute("activeGoalsCount") != null ? request.getAttribute("activeGoalsCount") : 0 %></span>
                    <span class="stat-unit">Active</span>
                </div>
            </div>
            <div class="card-panel stat-card">
                <span class="stat-label">Current Streak</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= currentStreak %></span>
                    <span class="stat-unit">Days</span>
                </div>
            </div>
        </div>

        <!-- Dashboard Content Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem;">

            <!-- Recent Activity Table Preview -->
            <div class="card-panel" style="padding: 1.5rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
                    <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--text-main);">Recent Workout History</h3>
                    <a href="workouts" style="color: var(--primary-emerald-hover); font-size: 0.85rem; text-decoration: none; font-weight: 700;">View All &rarr;</a>
                </div>

                <div class="table-responsive">
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
                                        <td><strong style="color: var(--text-main);"><%= w.getActivityType() %></strong></td>
                                        <td><span class="badge-intensity <%= w.getIntensity() %>"><%= w.getIntensity() %></span></td>
                                        <td><%= w.getWorkoutDate() %></td>
                                        <td><%= w.getDurationMinutes() %> mins</td>
                                        <td><span style="color: var(--primary-emerald-hover); font-weight: 800;"><%= w.getCaloriesBurned() %> kcal</span></td>
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
            <div class="card-panel" style="padding: 1.5rem;">
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; color: var(--text-main);">Target Goals Progress</h3>

                <% if (goals != null && !goals.isEmpty()) {
                    for (Goal g : goals) { %>
                        <div style="margin-bottom: 1.25rem; padding-bottom: 1rem; border-bottom: 1px solid var(--border-color);">
                            <div style="display: flex; justify-content: space-between; margin-bottom: 0.35rem;">
                                <strong style="font-size: 0.95rem; color: var(--text-main);"><%= g.getTitle() %></strong>
                                <span style="font-size: 0.85rem; color: var(--primary-emerald-hover); font-weight: 800;"><%= g.getProgressPercentage() %>%</span>
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
                    <p style="color: var(--text-muted); font-size: 0.9rem; text-align: center; margin-top: 1.5rem;">No active goals set yet. Add a target goal in Workouts.</p>
                <% } %>
            </div>

        </div>
    </div>

</body>
</html>
