<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.vitalfit.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login");
        return;
    }
    String flashMessage = (String) request.getAttribute("flashMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");

    String name = currentUser.getName() != null ? currentUser.getName() : "Athlete";
    String initials = name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.toUpperCase();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Profile - VitalFit</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Header Navigation Bar -->
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
            <a href="leaderboard" class="nav-link">Leaderboard</a>
            <a href="profile" class="nav-link active">Profile</a>
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
                <h1 class="page-title">Athlete Profile & Statistics</h1>
                <p class="page-subtitle">Manage your account details and view lifetime performance telemetry.</p>
            </div>
        </div>

        <% if (flashMessage != null) { %>
            <div class="alert alert-info"><%= flashMessage %></div>
        <% } %>
        <% if (errorMessage != null) { %>
            <div class="alert alert-error"><%= errorMessage %></div>
        <% } %>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;">

            <!-- Athlete Info Card -->
            <div class="card-panel" style="padding: 2rem; display: flex; flex-direction: column; align-items: center; text-align: center;">
                <div style="width: 80px; height: 80px; border-radius: 50%; background: var(--primary-emerald); color: #fff; font-size: 2rem; font-weight: 800; display: flex; align-items: center; justify-content: center; margin-bottom: 1rem; box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);">
                    <%= initials %>
                </div>
                <h2 style="font-size: 1.5rem; font-weight: 800; color: var(--text-main); margin-bottom: 0.25rem;"><%= currentUser.getName() %></h2>
                <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1rem;"><%= currentUser.getEmail() %></p>
                <div style="display: flex; gap: 0.5rem; margin-bottom: 1.5rem;">
                    <span class="role-tag" style="font-size: 0.75rem; padding: 0.25rem 0.75rem;"><%= currentUser.getRole() %></span>
                </div>
                <div style="font-size: 0.8rem; color: var(--text-muted); border-top: 1px solid var(--border-color); width: 100%; padding-top: 1rem;">
                    Member Since: <%= currentUser.getCreatedAt() != null ? currentUser.getCreatedAt().toString().substring(0, 10) : "2026" %>
                </div>
            </div>

            <!-- Update Profile Form -->
            <div class="card-panel" style="padding: 2rem;">
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; color: var(--text-main);">Update Display Name</h3>
                <form action="profile" method="post">
                    <div class="form-group">
                        <label class="form-label">Full Name</label>
                        <input type="text" name="name" class="form-control" value="<%= currentUser.getName() %>" required placeholder="Display Name">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Email Address (Read Only)</label>
                        <input type="email" class="form-control" value="<%= currentUser.getEmail() %>" disabled style="background: #f1f5f9; cursor: not-allowed;">
                    </div>
                    <button type="submit" class="btn btn-emerald" style="width: 100%; margin-top: 0.5rem;">Save Changes</button>
                </form>
            </div>

        </div>

        <!-- Lifetime Stats Grid -->
        <h2 style="font-size: 1.35rem; font-weight: 800; margin-bottom: 1.25rem; color: var(--text-main);">Lifetime Performance Telemetry</h2>
        <div class="grid-stats">
            <div class="card-panel stat-card">
                <span class="stat-label">Workouts Completed</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= request.getAttribute("totalWorkouts") != null ? request.getAttribute("totalWorkouts") : 0 %></span>
                    <span class="stat-unit">Sessions</span>
                </div>
            </div>
            <div class="card-panel stat-card">
                <span class="stat-label">Cumulative Duration</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= request.getAttribute("totalHours") != null ? request.getAttribute("totalHours") : 0 %></span>
                    <span class="stat-unit">hrs <%= request.getAttribute("remainingMins") != null ? request.getAttribute("remainingMins") : 0 %>m</span>
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
                <span class="stat-label">Active Challenges</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= request.getAttribute("enrolledChallengesCount") != null ? request.getAttribute("enrolledChallengesCount") : 0 %></span>
                    <span class="stat-unit">Joined</span>
                </div>
            </div>
        </div>

    </div>

</body>
</html>
