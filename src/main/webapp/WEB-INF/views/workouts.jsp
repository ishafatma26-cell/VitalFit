<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.vitalfit.model.User" %>
<%@ page import="com.vitalfit.model.Workout" %>
<%@ page import="com.vitalfit.model.Goal" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login");
        return;
    }
    List<Workout> workouts = (List<Workout>) request.getAttribute("workouts");
    List<Goal> goals = (List<Goal>) request.getAttribute("goals");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workouts CRUD - VitalFit</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Light Frosted Navigation Bar -->
    <nav class="navbar">
        <a href="dashboard" class="nav-brand">
            <div class="brand-icon">⚡</div>
            <span>VitalFit</span>
        </a>
        <button class="mobile-nav-toggle" onclick="document.querySelector('.nav-links').classList.toggle('active')">☰</button>
        <div class="nav-links">
            <a href="dashboard" class="nav-link">Dashboard</a>
            <a href="workouts" class="nav-link active">Workouts</a>
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
        <div class="page-header">
            <div>
                <h1 class="page-title">Workout History & CRUD Engine</h1>
                <p class="page-subtitle">Log new workout sessions, set target goals, and manage your activity history.</p>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem;">

            <!-- Column 1: Forms -->
            <div style="display: flex; flex-direction: column; gap: 1.5rem;">

                <!-- Log Workout Form -->
                <div class="card-panel" style="padding: 1.5rem;">
                    <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; color: var(--text-main);">Log a New Workout</h3>
                    <form action="workouts" method="post">
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                            <div class="form-group">
                                <label class="form-label">Workout Date</label>
                                <input type="date" name="workoutDate" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Workout Type</label>
                                <select name="activityType" class="form-control" required>
                                    <option value="Running">Running</option>
                                    <option value="Cycling">Cycling</option>
                                    <option value="Strength">Strength</option>
                                    <option value="HIIT">HIIT</option>
                                    <option value="Yoga">Yoga</option>
                                    <option value="Swimming">Swimming</option>
                                </select>
                            </div>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                            <div class="form-group">
                                <label class="form-label">Intensity Level</label>
                                <select name="intensity" class="form-control" required>
                                    <option value="Low">Low</option>
                                    <option value="Medium" selected>Medium</option>
                                    <option value="High">High</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Duration (mins)</label>
                                <input type="number" name="duration" class="form-control" min="1" required placeholder="45">
                            </div>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                            <div class="form-group">
                                <label class="form-label">Calories Burned</label>
                                <input type="number" name="calories" class="form-control" min="0" required placeholder="350">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Distance (km)</label>
                                <input type="number" step="0.01" name="distance" class="form-control" placeholder="5.50" value="0.00">
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Notes / Telemetry</label>
                            <input type="text" name="notes" class="form-control" placeholder="e.g. Heart rate zone 4 sprint intervals">
                        </div>
                        <button type="submit" class="btn btn-primary" style="width: 100%;">Record Workout Session</button>
                    </form>
                </div>

                <!-- Set Target Goal Form -->
                <div class="card-panel" style="padding: 1.5rem;">
                    <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; color: var(--text-main);">Set Target Goal</h3>
                    <form action="workouts" method="post">
                        <input type="hidden" name="action" value="addGoal">
                        <div class="form-group">
                            <label class="form-label">Goal Title</label>
                            <input type="text" name="title" class="form-control" placeholder="e.g. Burn 10,000 Calories" required>
                        </div>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                            <div class="form-group">
                                <label class="form-label">Target Value</label>
                                <input type="number" step="0.1" name="targetValue" class="form-control" placeholder="10000" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Unit</label>
                                <select name="unit" class="form-control" required>
                                    <option value="kcal">kcal</option>
                                    <option value="km">km</option>
                                    <option value="workouts">workouts</option>
                                </select>
                            </div>
                        </div>
                        <button type="submit" class="btn" style="width: 100%; background: #ffffff; border: 1px solid var(--border-color); color: var(--text-main);">Set New Goal</button>
                    </form>
                </div>

            </div>

            <!-- Column 2: Workout History Table -->
            <div class="card-panel" style="padding: 1.5rem;">
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; color: var(--text-main);">Workout History Table</h3>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Activity</th>
                                <th>Intensity</th>
                                <th>Date</th>
                                <th>Duration</th>
                                <th>Calories</th>
                                <th>Distance</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (workouts != null && !workouts.isEmpty()) {
                                for (Workout w : workouts) { %>
                                    <tr>
                                        <td>
                                            <strong style="color: var(--text-main);"><%= w.getActivityType() %></strong>
                                            <% if (w.getNotes() != null && !w.getNotes().isBlank()) { %>
                                                <br><small style="color: var(--text-muted);"><%= w.getNotes() %></small>
                                            <% } %>
                                        </td>
                                        <td><span class="badge-intensity <%= w.getIntensity() %>"><%= w.getIntensity() %></span></td>
                                        <td><%= w.getWorkoutDate() %></td>
                                        <td><%= w.getDurationMinutes() %> mins</td>
                                        <td><span style="color: var(--primary); font-weight: 800;"><%= w.getCaloriesBurned() %> kcal</span></td>
                                        <td><%= w.getDistanceKm() %> km</td>
                                        <td>
                                            <form action="workouts" method="post" style="display: inline;" onsubmit="return confirm('Delete this workout record?');">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="<%= w.getId() %>">
                                                <button type="submit" class="btn btn-danger" style="padding: 0.25rem 0.6rem; font-size: 0.75rem; min-height: auto;">Delete</button>
                                            </form>
                                        </td>
                                    </tr>
                            <%  }
                               } else { %>
                                <tr>
                                    <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 3rem;">No workouts recorded yet. Use the form on the left to add your first session.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

            </div>

        </div>
    </div>

</body>
</html>
