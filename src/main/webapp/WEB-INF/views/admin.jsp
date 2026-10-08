<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.vitalfit.model.User" %>
<%@ page import="com.vitalfit.model.ActivityLog" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !currentUser.isAdmin()) {
        response.sendRedirect("login");
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
    <title>Admin Portal - VitalFit</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <nav class="navbar">
        <a href="dashboard" class="nav-brand">
            <div class="brand-icon">V</div>
            <span>VitalFit Admin</span>
        </a>
        <div class="nav-links">
            <a href="dashboard" class="nav-link">Dashboard</a>
            <a href="workouts" class="nav-link">Workouts</a>
            <a href="challenges" class="nav-link">Challenges</a>
            <a href="leaderboard" class="nav-link">Leaderboard</a>
            <a href="admin" class="nav-link active" style="color: var(--warning);">Admin Portal</a>
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
                <h1 class="page-title">Platform Administration & Audit System</h1>
                <p class="page-subtitle">User role management and non-blocking multithreaded asynchronous audit activity logs.</p>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 2rem;">

            <!-- User Management -->
            <div class="glass-panel" style="padding: 1.5rem;">
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem;">User Management</h3>
                <div class="table-container">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name / Email</th>
                                <th>Role</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (userList != null && !userList.isEmpty()) {
                                for (User u : userList) { %>
                                    <tr>
                                        <td>#<%= u.getId() %></td>
                                        <td>
                                            <strong style="color: #fff;"><%= u.getName() %></strong><br>
                                            <small style="color: var(--text-dim);"><%= u.getEmail() %></small>
                                        </td>
                                        <td>
                                            <span style="font-size: 0.75rem; padding: 0.2rem 0.5rem; border-radius: 10px; font-weight: 700; background: <%= u.isAdmin() ? "var(--accent-gold)" : "rgba(255, 255, 255, 0.1)" %>; color: #fff;">
                                                <%= u.getRole() %>
                                            </span>
                                        </td>
                                        <td>
                                            <form action="admin" method="post" style="display: flex; gap: 0.35rem;">
                                                <input type="hidden" name="action" value="updateRole">
                                                <input type="hidden" name="userId" value="<%= u.getId() %>">
                                                <% if (u.isAdmin()) { %>
                                                    <input type="hidden" name="role" value="USER">
                                                    <button type="submit" class="btn" style="padding: 0.25rem 0.5rem; font-size: 0.75rem; background: rgba(255, 255, 255, 0.1); color: #fff;">Demote</button>
                                                <% } else { %>
                                                    <input type="hidden" name="role" value="ADMIN">
                                                    <button type="submit" class="btn btn-primary" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;">Promote</button>
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

            <!-- Thread Pool Info -->
            <div class="glass-panel" style="padding: 1.5rem; display: flex; flex-direction: column; justify-content: space-between;">
                <div>
                    <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1rem; color: var(--accent-cyan);">Multithreaded Logging Status</h3>
                    <p style="color: var(--text-muted); font-size: 0.9rem; line-height: 1.6; margin-bottom: 1.5rem;">
                        All system actions (logins, workout additions, challenge registrations, role changes) trigger asynchronous background tasks sent to a fixed-size thread pool (<code style="color: var(--accent-cyan);">Executors.newFixedThreadPool(3)</code>).
                    </p>
                    <div style="background: rgba(255, 255, 255, 0.03); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.25rem;">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                            <span style="color: var(--text-muted);">Pool Architecture:</span>
                            <strong style="color: #fff;">3 Worker Threads</strong>
                        </div>
                        <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                            <span style="color: var(--text-muted);">Execution Engine:</span>
                            <strong style="color: var(--success);">Non-blocking Async</strong>
                        </div>
                        <div style="display: flex; justify-content: space-between;">
                            <span style="color: var(--text-muted);">Database Table:</span>
                            <code style="color: var(--warning);">activity_log</code>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <!-- System Audit Log Table -->
        <div class="glass-panel" style="padding: 1.5rem;">
            <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem;">Live Asynchronous Audit Logs</h3>

            <div class="table-container">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Timestamp</th>
                            <th>User Email</th>
                            <th>Action Type</th>
                            <th>Details</th>
                            <th>Worker Thread</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (logs != null && !logs.isEmpty()) {
                            for (ActivityLog log : logs) { %>
                                <tr>
                                    <td>#<%= log.getId() %></td>
                                    <td><%= log.getTimestamp() %></td>
                                    <td><strong style="color: #fff;"><%= log.getUserEmail() %></strong></td>
                                    <td>
                                        <span style="color: var(--accent-cyan); font-weight: 700; font-size: 0.85rem;">
                                            <%= log.getAction() %>
                                        </span>
                                    </td>
                                    <td><%= log.getDetails() %></td>
                                    <td>
                                        <code style="color: var(--warning); font-size: 0.8rem; background: rgba(245, 158, 11, 0.1); padding: 0.15rem 0.4rem; border-radius: 4px;">
                                            <%= log.getThreadName() != null ? log.getThreadName() : "main" %>
                                        </code>
                                    </td>
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
