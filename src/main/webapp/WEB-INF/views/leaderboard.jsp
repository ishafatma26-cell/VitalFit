<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.vitalfit.model.User" %>
<%@ page import="com.vitalfit.service.LeaderboardService.LeaderboardEntry" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login");
        return;
    }
    List<LeaderboardEntry> leaderboard = (List<LeaderboardEntry>) request.getAttribute("leaderboard");
    List<LeaderboardEntry> podium = (List<LeaderboardEntry>) request.getAttribute("podium");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Leaderboard - VitalFit</title>
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
            <a href="workouts" class="nav-link">Workouts</a>
            <a href="challenges" class="nav-link">Challenges</a>
            <a href="leaderboard" class="nav-link active">Leaderboard</a>
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
                <h1 class="page-title">Community Leaderboard</h1>
                <p class="page-subtitle">In-memory telemetry aggregation & sorting processed by Java 8+ Stream API.</p>
            </div>
        </div>

        <!-- Podium Section for Top 3 -->
        <% if (podium != null && !podium.isEmpty()) { %>
            <div class="podium-grid">
                <% for (LeaderboardEntry p : podium) {
                    String podiumClass = "podium-" + p.getRank();
                    String rankClass = "rank-" + p.getRank();
                %>
                    <div class="glass-panel podium-card <%= podiumClass %>">
                        <div class="badge-rank <%= rankClass %>">#<%= p.getRank() %></div>
                        <h3 style="font-size: 1.3rem; font-weight: 800; color: #fff; margin-bottom: 0.25rem;"><%= p.getUserName() %></h3>
                        <div style="font-size: 2rem; font-weight: 800; color: var(--accent-cyan); margin: 0.5rem 0;">
                            <%= p.getTotalCalories() %> <span style="font-size: 0.9rem; color: var(--text-muted);">kcal</span>
                        </div>
                        <div style="display: flex; gap: 1rem; font-size: 0.8rem; color: var(--text-muted); margin-top: 0.5rem;">
                            <span>🏃 <%= p.getTotalDistance() %> km</span>
                            <span>⏱ <%= p.getTotalDuration() %> mins</span>
                            <span>🔥 <%= p.getTotalWorkouts() %> workouts</span>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>

        <!-- Full Leaderboard Table -->
        <div class="glass-panel" style="padding: 1.5rem;">
            <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem;">Overall Standings</h3>

            <div class="table-container">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Athlete</th>
                            <th>Calories Burned</th>
                            <th>Total Distance</th>
                            <th>Active Duration</th>
                            <th>Workouts Logged</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (leaderboard != null && !leaderboard.isEmpty()) {
                            for (LeaderboardEntry entry : leaderboard) {
                                boolean isMe = entry.getUserId() == currentUser.getId();
                        %>
                                <tr style="<%= isMe ? "background: rgba(6, 182, 212, 0.1);" : "" %>">
                                    <td>
                                        <strong style="font-size: 1.1rem; color: <%= entry.getRank() <= 3 ? "var(--accent-cyan)" : "var(--text-muted)" %>;">
                                            #<%= entry.getRank() %>
                                        </strong>
                                    </td>
                                    <td>
                                        <strong style="color: #fff;"><%= entry.getUserName() %></strong>
                                        <% if (isMe) { %>
                                            <span style="font-size: 0.7rem; padding: 0.15rem 0.4rem; background: var(--accent-gradient); border-radius: 10px; color: #fff; margin-left: 0.5rem;">YOU</span>
                                        <% } %>
                                    </td>
                                    <td><span style="color: var(--accent-cyan); font-weight: 800; font-size: 1.05rem;"><%= entry.getTotalCalories() %> kcal</span></td>
                                    <td><%= entry.getTotalDistance() %> km</td>
                                    <td><%= entry.getTotalDuration() %> mins</td>
                                    <td><%= entry.getTotalWorkouts() %> sessions</td>
                                </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 3rem;">No leaderboard records available yet. Log workouts to populate rankings!</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div>

</body>
</html>
