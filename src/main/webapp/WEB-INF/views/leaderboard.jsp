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

    <!-- Navigation Bar -->
    <nav class="navbar">
        <a href="dashboard" class="nav-brand">
            <div class="brand-icon">⚡</div>
            <span>VitalFit</span>
        </a>
        <button class="mobile-nav-toggle" onclick="document.querySelector('.nav-links').classList.toggle('active')">☰</button>
        <div class="nav-links">
            <a href="dashboard" class="nav-link">Dashboard</a>
            <a href="workouts" class="nav-link">Workouts</a>
            <a href="challenges" class="nav-link">Challenges</a>
            <a href="leaderboard" class="nav-link active">Leaderboard</a>
            <a href="profile" class="nav-link">Profile</a>
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
                <h1 class="page-title">Community Leaderboard</h1>
                <p class="page-subtitle">Real-time workout volume and calorie aggregation using Java 8+ Stream API (Collectors.groupingBy & summingInt).</p>
            </div>
        </div>

        <!-- 3-Tier Visual Podium Showcase -->
        <% if (podium != null && !podium.isEmpty()) { %>
            <div class="podium-grid">
                <% for (LeaderboardEntry p : podium) {
                    String podiumClass = "podium-" + p.getRank();
                    String rankClass = "rank-" + p.getRank();
                %>
                    <div class="card-panel podium-card <%= podiumClass %>">
                        <div class="badge-rank <%= rankClass %>">#<%= p.getRank() %></div>
                        <h3 style="font-size: 1.3rem; font-weight: 800; color: var(--text-main); margin-bottom: 0.25rem;"><%= p.getUserName() %></h3>
                        <div style="font-size: 2.1rem; font-weight: 800; color: var(--primary-emerald); margin: 0.5rem 0; line-height: 1;">
                            <%= p.getTotalCalories() %> <span style="font-size: 0.9rem; color: var(--text-muted); font-weight: 600;">kcal</span>
                        </div>
                        <div style="display: flex; gap: 0.85rem; font-size: 0.8rem; color: var(--text-muted); margin-top: 0.75rem;">
                            <span>🏃 <%= p.getTotalDistance() %> km</span>
                            <span>⏱ <%= p.getTotalDuration() %> mins</span>
                            <span>🔥 <%= p.getTotalWorkouts() %> workouts</span>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>

        <!-- Full Tabular Rankings Below Podium -->
        <div class="card-panel" style="padding: 1.5rem;">
            <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; color: var(--text-main);">Overall Athlete Standings</h3>

            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Athlete Name</th>
                            <th>Total Calories Burned</th>
                            <th>Total Distance</th>
                            <th>Active Duration</th>
                            <th>Completed Workouts</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (leaderboard != null && !leaderboard.isEmpty()) {
                            for (LeaderboardEntry entry : leaderboard) {
                                boolean isMe = entry.getUserId() == currentUser.getId();
                        %>
                                <tr style="<%= isMe ? "background: var(--primary-emerald-light);" : "" %>">
                                    <td>
                                        <strong style="font-size: 1.1rem; color: <%= entry.getRank() <= 3 ? "var(--primary-emerald)" : "var(--text-muted)" %>;">
                                            #<%= entry.getRank() %>
                                        </strong>
                                    </td>
                                    <td>
                                        <strong style="color: var(--text-main);"><%= entry.getUserName() %></strong>
                                        <% if (isMe) { %>
                                            <span style="font-size: 0.7rem; padding: 0.15rem 0.4rem; background: var(--primary-emerald); border-radius: 10px; color: #fff; margin-left: 0.5rem; font-weight: 800;">YOU</span>
                                        <% } %>
                                    </td>
                                    <td><span style="color: var(--primary-emerald-hover); font-weight: 800; font-size: 1.05rem;"><%= entry.getTotalCalories() %> kcal</span></td>
                                    <td><%= entry.getTotalDistance() %> km</td>
                                    <td><%= entry.getTotalDuration() %> mins</td>
                                    <td><%= entry.getTotalWorkouts() %> sessions</td>
                                </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 3rem;">No leaderboard records available. Log workouts to populate rankings!</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div>

</body>
</html>
