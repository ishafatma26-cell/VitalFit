<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.vitalfit.model.User" %>
<%@ page import="com.vitalfit.model.ActivityLog" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !currentUser.isAdmin()) {
        response.sendRedirect("dashboard");
        return;
    }
    List<User> userList = (List<User>) request.getAttribute("users");
    List<ActivityLog> logs = (List<ActivityLog>) request.getAttribute("logs");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Panel - VitalFit</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Fixed Header Navigation Bar -->
    <nav class="navbar">
        <a href="dashboard" class="nav-brand">
            <div class="brand-icon">⚡</div>
            <span>VitalFit Admin</span>
        </a>
        <div class="nav-links">
            <a href="dashboard" class="nav-link">Dashboard</a>
            <a href="workouts" class="nav-link">Workouts</a>
            <a href="challenges" class="nav-link">Challenges</a>
            <a href="leaderboard" class="nav-link">Leaderboard</a>
            <a href="admin" class="nav-link active" style="color: var(--warning);">Admin Panel</a>
            <div class="user-badge">
                <span style="font-size: 0.85rem; font-weight: 700;"><%= currentUser.getName() %></span>
                <span class="role-tag"><%= currentUser.getRole() %></span>
            </div>
            <a href="logout" class="btn btn-danger" style="padding: 0.35rem 0.85rem; font-size: 0.8rem;">Logout</a>
        </div>
    </nav>

    <div class="main-container">
        <div class="page-header">
            <div>
                <h1 class="page-title">Platform Administration & Security Panel</h1>
                <p class="page-subtitle">User role management and live non-blocking multithreaded asynchronous activity logs.</p>
            </div>
        </div>

        <!-- Section 1: User Management -->
        <div class="glass-panel" style="padding: 1.5rem; margin-bottom: 2rem;">
            <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem;">Registered User Management</h3>
            <div class="table-container">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Athlete Name</th>
                            <th>Email Address</th>
                            <th>Current Role</th>
                            <th>Action / Role Toggle</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (userList != null && !userList.isEmpty()) {
                            for (User u : userList) { %>
                                <tr>
                                    <td>#<%= u.getId() %></td>
                                    <td><strong style="color: #fff;"><%= u.getName() %></strong></td>
                                    <td><%= u.getEmail() %></td>
                                    <td>
                                        <span style="font-size: 0.75rem; padding: 0.2rem 0.5rem; border-radius: 10px; font-weight: 800; background: <%= u.isAdmin() ? "var(--accent-gold)" : "rgba(255, 255, 255, 0.1)" %>; color: #fff;">
                                            <%= u.getRole() %>
                                        </span>
                                    </td>
                                    <td>
                                        <form action="admin" method="post" style="display: flex; gap: 0.35rem;">
                                            <input type="hidden" name="action" value="updateRole">
                                            <input type="hidden" name="userId" value="<%= u.getId() %>">
                                            <% if (u.isAdmin()) { %>
                                                <input type="hidden" name="role" value="USER">
                                                <button type="submit" class="btn" style="padding: 0.25rem 0.6rem; font-size: 0.75rem; background: rgba(255, 255, 255, 0.1); color: #fff;">Demote to User</button>
                                            <% } else { %>
                                                <input type="hidden" name="role" value="ADMIN">
                                                <button type="submit" class="btn btn-primary" style="padding: 0.25rem 0.6rem; font-size: 0.75rem;">Promote to Admin</button>
                                            <% } %>
                                        </form>
                                    </td>
                                </tr>
                        <%  }
                           } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Section 2: Live System Activity Logs (Multithreaded Showcase) -->
        <div class="glass-panel" style="padding: 1.5rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
                <div>
                    <h3 style="font-size: 1.2rem; font-weight: 700;">Live System Activity Logs (Multithreaded)</h3>
                    <p style="font-size: 0.85rem; color: var(--text-muted);">Asymptotically logged via managed ExecutorService fixed thread pool (Executors.newFixedThreadPool(3)).</p>
                </div>
                <span style="font-size: 0.8rem; background: rgba(6, 182, 212, 0.15); border: 1px solid rgba(6, 182, 212, 0.3); color: var(--accent-cyan); padding: 0.3rem 0.75rem; border-radius: 20px; font-weight: 700;">
                    Pool Size: 3 Workers
                </span>
            </div>

            <div class="table-container">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Log ID</th>
                            <th>Timestamp</th>
                            <th>Worker Thread Name</th>
                            <th>Action Type</th>
                            <th>User Email</th>
                            <th>Action Details</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (logs != null && !logs.isEmpty()) {
                            for (ActivityLog log : logs) { %>
                                <tr>
                                    <td>#<%= log.getId() %></td>
                                    <td><%= log.getTimestamp() %></td>
                                    <td>
                                        <code style="color: var(--warning); font-size: 0.8rem; background: rgba(245, 158, 11, 0.1); padding: 0.15rem 0.4rem; border-radius: 4px; font-weight: 700;">
                                            <%= log.getThreadName() != null ? log.getThreadName() : "main" %>
                                        </code>
                                    </td>
                                    <td>
                                        <span style="color: var(--accent-cyan); font-weight: 800; font-size: 0.85rem;">
                                            <%= log.getAction() %>
                                        </span>
                                    </td>
                                    <td><strong style="color: #fff;"><%= log.getUserEmail() %></strong></td>
                                    <td><%= log.getDetails() %></td>
                                </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 3rem;">No activity log records found.</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div>

</body>
</html>
