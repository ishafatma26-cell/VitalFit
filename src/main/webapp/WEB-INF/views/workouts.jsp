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
    <title>Workouts & Goals - VitalFit</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <nav class="navbar">
        <a href="dashboard" class="nav-brand">
            <div class="brand-icon">V</div>
            <span>VitalFit</span>
        </a>
        <div class="nav-links">
            <a href="dashboard" class="nav-link">Dashboard</a>
            <a href="workouts" class="nav-link active">Workouts</a>
            <a href="challenges" class="nav-link">Challenges</a>
            <a href="leaderboard" class="nav-link">Leaderboard</a>
            <% if (currentUser.isAdmin()) { %>
                <a href="admin" class="nav-link" style="color: var(--warning);">Admin Portal</a>
            <% } %>
            <div class="user-badge">
                <span style="font-size: 0.85rem; font-weight: 600;"><%= currentUser.getName() %></span>
                <span class="role-tag"><%= currentUser.getRole() %></span>
            </div>
            <a href="logout" class="btn btn-danger" style="padding: 0.35rem 0.85rem; font-size: 0.8rem;">Logout</a>
        </div>
    </nav>

    <div class="main-container">
        <div class="page-header">
            <div>
                <h1 class="page-title">Activity Logging & Target Goals</h1>
                <p class="page-subtitle">Log new exercise sessions and define personalized health objectives.</p>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 1.5rem;">

            <!-- Log Form Column -->
            <div style="display: flex; flex-direction: column; gap: 1.5rem;">

                <!-- Log Workout Form -->
                <div class="glass-panel" style="padding: 1.5rem;">
                    <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; color: #fff;">Log Exercise Session</h3>
                    <form action="workouts" method="post">
                        <div class="form-group">
                            <label class="form-label">Activity Type</label>
                            <select name="activityType" class="form-control" required>
                                <option value="Running">Running</option>
                                <option value="Cycling">Cycling</option>
                                <option value="Swimming">Swimming</option>
                                <option value="HIIT Training">HIIT Training</option>
                                <option value="Strength Training">Strength Training</option>
                                <option value="Yoga / Mobility">Yoga / Mobility</option>
                            </select>
                        </div>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                            <div class="form-group">
                                <label class="form-label">Duration (mins)</label>
                                <input type="number" name="duration" class="form-control" min="1" required placeholder="45">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Calories (kcal)</label>
                                <input type="number" name="calories" class="form-control" min="0" required placeholder="350">
                            </div>
                        </div>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                            <div class="form-group">
                                <label class="form-label">Distance (km)</label>
                                <input type="number" step="0.01" name="distance" class="form-control" placeholder="5.50" value="0.00">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Date</label>
                                <input type="date" name="workoutDate" class="form-control" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Session Notes</label>
                            <input type="text" name="notes" class="form-control" placeholder="e.g. Heart rate zone 4 sprint intervals">
                        </div>
                        <button type="submit" class="btn btn-primary" style="width: 100%;">Record Workout</button>
                    </form>
                </div>

                <!-- Add Target Goal Form -->
                <div class="glass-panel" style="padding: 1.5rem;">
                    <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; color: #fff;">Create Target Goal</h3>
                    <form action="workouts" method="post">
                        <input type="hidden" name="action" value="addGoal">
                        <div class="form-group">
                            <label class="form-label">Goal Title</label>
                            <input type="text" name="title" class="form-control" placeholder="e.g. Burn 10000 Calories" required>
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
                        <button type="submit" class="btn" style="width: 100%; background: rgba(255, 255, 255, 0.1); border: 1px solid var(--border-color); color: #fff;">Set New Goal</button>
                    </form>
                </div>

            </div>

            <!-- Workout History Table Column -->
            <div class="glass-panel" style="padding: 1.5rem;">
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem;">Workout History</h3>

                <div class="table-container">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Activity</th>
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
                                            <strong style="color: #fff;"><%= w.getActivityType() %></strong>
                                            <% if (w.getNotes() != null && !w.getNotes().isBlank()) { %>
                                                <br><small style="color: var(--text-dim);"><%= w.getNotes() %></small>
                                            <% } %>
                                        </td>
                                        <td><%= w.getWorkoutDate() %></td>
                                        <td><%= w.getDurationMinutes() %> mins</td>
                                        <td><span style="color: var(--accent-cyan); font-weight: 700;"><%= w.getCaloriesBurned() %> kcal</span></td>
                                        <td><%= w.getDistanceKm() %> km</td>
                                        <td>
                                            <form action="workouts" method="post" style="display: inline;" onsubmit="return confirm('Delete this workout record?');">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="<%= w.getId() %>">
                                                <button type="submit" class="btn btn-danger" style="padding: 0.25rem 0.6rem; font-size: 0.75rem;">Delete</button>
                                            </form>
                                        </td>
                                    </tr>
                            <%  }
                               } else { %>
                                <tr>
                                    <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 3rem;">No workouts recorded yet. Use the form on the left to add your first session.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <!-- Goals Overview -->
                <div style="margin-top: 2rem; padding-top: 1.5rem; border-top: 1px solid var(--border-color);">
                    <h4 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem;">Target Progress</h4>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                        <% if (goals != null && !goals.isEmpty()) {
                            for (Goal g : goals) { %>
                                <div style="background: rgba(255, 255, 255, 0.03); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1rem;">
                                    <div style="display: flex; justify-content: space-between; font-size: 0.85rem; font-weight: 700;">
                                        <span><%= g.getTitle() %></span>
                                        <span style="color: var(--accent-cyan);"><%= g.getProgressPercentage() %>%</span>
                                    </div>
                                    <div class="progress-bar-bg">
                                        <div class="progress-bar-fill" style="width: <%= g.getProgressPercentage() %>%;"></div>
                                    </div>
                                    <div style="font-size: 0.75rem; color: var(--text-muted); margin-top: 0.5rem; display: flex; justify-content: space-between;">
                                        <span><%= g.getCurrentValue() %> / <%= g.getTargetValue() %> <%= g.getUnit() %></span>
                                        <span style="color: <%= "COMPLETED".equalsIgnoreCase(g.getStatus()) ? "var(--success)" : "var(--warning)" %>;"><%= g.getStatus() %></span>
                                    </div>
                                </div>
                        <%  }
                           } else { %>
                            <p style="color: var(--text-muted); font-size: 0.85rem;">No active goals.</p>
                        <% } %>
                    </div>
                </div>

            </div>

        </div>
    </div>

</body>
</html>
