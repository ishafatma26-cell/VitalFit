<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.vitalfit.model.User" %>
<%@ page import="com.vitalfit.model.Challenge" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login");
        return;
    }
    List<Challenge> challenges = (List<Challenge>) request.getAttribute("challenges");
    String flashMessage = (String) session.getAttribute("flashMessage");
    if (flashMessage != null) {
        session.removeAttribute("flashMessage");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Challenges - VitalFit</title>
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
            <a href="challenges" class="nav-link active">Challenges</a>
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
                <h1 class="page-title">Community Fitness Challenges</h1>
                <p class="page-subtitle">Join community competitions powered by transactional ACID backend verification.</p>
            </div>
        </div>

        <% if (flashMessage != null) { %>
            <div class="alert alert-info"><%= flashMessage %></div>
        <% } %>

        <% if (currentUser.isAdmin()) { %>
            <!-- Admin Challenge Creator -->
            <div class="glass-panel" style="padding: 1.5rem; margin-bottom: 2rem;">
                <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; color: var(--warning);">+ Admin: Launch New Challenge</h3>
                <form action="challenges" method="post" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; align-items: flex-end;">
                    <input type="hidden" name="action" value="create">
                    <div>
                        <label class="form-label">Challenge Title</label>
                        <input type="text" name="title" class="form-control" placeholder="100K Steps Challenge" required>
                    </div>
                    <div>
                        <label class="form-label">Target Type</label>
                        <select name="targetType" class="form-control" required>
                            <option value="Calories">Calories</option>
                            <option value="Distance">Distance</option>
                            <option value="Steps">Steps</option>
                        </select>
                    </div>
                    <div>
                        <label class="form-label">Target Goal</label>
                        <input type="number" step="0.1" name="targetGoal" class="form-control" placeholder="10000" required>
                    </div>
                    <div>
                        <label class="form-label">Start Date</label>
                        <input type="date" name="startDate" class="form-control" required>
                    </div>
                    <div>
                        <label class="form-label">End Date</label>
                        <input type="date" name="endDate" class="form-control" required>
                    </div>
                    <div style="grid-column: 1 / -1;">
                        <label class="form-label">Description</label>
                        <input type="text" name="description" class="form-control" placeholder="Short summary of challenge rules">
                    </div>
                    <div style="grid-column: 1 / -1;">
                        <button type="submit" class="btn btn-primary" style="padding: 0.6rem 1.5rem;">Launch Challenge</button>
                    </div>
                </form>
            </div>
        <% } %>

        <!-- Challenge Cards Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 1.5rem;">
            <% if (challenges != null && !challenges.isEmpty()) {
                for (Challenge c : challenges) { %>
                    <div class="glass-panel" style="padding: 1.5rem; display: flex; flex-direction: column; justify-content: space-between;">
                        <div>
                            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.75rem;">
                                <span style="font-size: 0.75rem; text-transform: uppercase; font-weight: 700; color: var(--accent-cyan); background: rgba(6, 182, 212, 0.1); padding: 0.25rem 0.6rem; border-radius: 20px;">
                                    <%= c.getTargetType() %>
                                </span>
                                <span style="font-size: 0.8rem; color: var(--text-muted);">
                                    👥 <%= c.getParticipantCount() %> Joined
                                </span>
                            </div>
                            <h3 style="font-size: 1.25rem; font-weight: 800; color: #fff; margin-bottom: 0.5rem;"><%= c.getTitle() %></h3>
                            <p style="font-size: 0.9rem; color: var(--text-muted); line-height: 1.5; margin-bottom: 1.25rem;">
                                <%= c.getDescription() != null ? c.getDescription() : "No description provided." %>
                            </p>
                        </div>

                        <div style="background: rgba(255, 255, 255, 0.03); padding: 0.85rem; border-radius: var(--radius-md); border: 1px solid var(--border-color); margin-bottom: 1.25rem;">
                            <div style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 0.35rem;">
                                <span style="color: var(--text-muted);">Goal Requirement:</span>
                                <strong style="color: #fff;"><%= c.getTargetGoal() %> <%= c.getTargetType() %></strong>
                            </div>
                            <div style="display: flex; justify-content: space-between; font-size: 0.85rem;">
                                <span style="color: var(--text-muted);">Duration:</span>
                                <span style="color: var(--text-dim);"><%= c.getStartDate() %> to <%= c.getEndDate() %></span>
                            </div>
                        </div>

                        <form action="challenges" method="post">
                            <input type="hidden" name="action" value="join">
                            <input type="hidden" name="challengeId" value="<%= c.getId() %>">
                            <% if (c.isJoined()) { %>
                                <button type="button" class="btn" style="width: 100%; background: rgba(16, 185, 129, 0.15); border: 1px solid rgba(16, 185, 129, 0.3); color: var(--success); cursor: default;" disabled>
                                    ✓ Joined Challenge
                                </button>
                            <% } else { %>
                                <button type="submit" class="btn btn-primary" style="width: 100%;">
                                    Join Challenge (ACID Tx)
                                </button>
                            <% } %>
                        </form>
                    </div>
            <%  }
               } else { %>
                <div style="grid-column: 1 / -1; text-align: center; color: var(--text-muted); padding: 4rem;">
                    No challenges active currently. Check back soon!
                </div>
            <% } %>
        </div>

    </div>

</body>
</html>
