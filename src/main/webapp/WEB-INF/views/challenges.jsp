<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.vitalfit.model.User" %>
<%@ page import="com.vitalfit.model.Challenge" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.stream.Collectors" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login");
        return;
    }
    List<Challenge> challenges = (List<Challenge>) request.getAttribute("challenges");
    Set<Integer> enrolledChallengeIds = (Set<Integer>) request.getAttribute("enrolledChallengeIds");

    List<Challenge> enrolledChallenges = null;
    if (challenges != null && enrolledChallengeIds != null) {
        enrolledChallenges = challenges.stream()
                .filter(c -> enrolledChallengeIds.contains(c.getId()))
                .collect(Collectors.toList());
    }

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
    <title>Available Challenges - VitalFit</title>
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
            <a href="challenges" class="nav-link active">Challenges</a>
            <a href="leaderboard" class="nav-link">Leaderboard</a>
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
                <h1 class="page-title">Available Challenges</h1>
                <p class="page-subtitle">Transactional registration engine backed by atomic autoCommit(false) validation.</p>
            </div>
        </div>

        <% if (flashMessage != null) { %>
            <div class="alert alert-info"><%= flashMessage %></div>
        <% } %>

        <% if (currentUser.isAdmin()) { %>
            <!-- Admin Challenge Creator Card -->
            <div class="card-panel" style="padding: 1.5rem; margin-bottom: 2rem;">
                <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; color: var(--warning);">+ Admin: Launch Community Challenge</h3>
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
                        <button type="submit" class="btn btn-emerald" style="padding: 0.6rem 1.5rem;">Launch Challenge</button>
                    </div>
                </form>
            </div>
        <% } %>

        <!-- Active Challenges Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 1.5rem; margin-bottom: 3rem;">
            <% if (challenges != null && !challenges.isEmpty()) {
                for (Challenge c : challenges) {
                    boolean isEnrolled = enrolledChallengeIds != null && enrolledChallengeIds.contains(c.getId());
            %>
                    <div class="card-panel" style="padding: 1.5rem; display: flex; flex-direction: column; justify-content: space-between;">
                        <div>
                            <!-- Top Header: Title Left, Participant Count Right -->
                            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.75rem;">
                                <h3 class="challenge-card-title" style="font-size: 1.25rem; font-weight: 700; color: var(--text-main); margin: 0;"><%= c.getTitle() %></h3>
                                <span class="badge-blue-pill">
                                    👤 <%= c.getParticipantCount() %> Participants
                                </span>
                            </div>
                            <!-- Description -->
                            <p style="font-size: 0.9rem; color: var(--text-muted); line-height: 1.5; margin-bottom: 1rem;">
                                <%= c.getDescription() != null ? c.getDescription() : "No description provided." %>
                            </p>
                        </div>

                        <div>
                            <!-- Divider Line -->
                            <div class="card-divider"></div>

                            <!-- Bottom Row: Target Score Left, End Date Right -->
                            <div style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 1.25rem; color: var(--text-muted);">
                                <div><strong>Target:</strong> <span style="color: var(--text-main); font-weight: 700;"><%= c.getTargetGoal() %> <%= c.getTargetType() %></span></div>
                                <div><strong>Ends:</strong> <span style="color: var(--text-main); font-weight: 700;"><%= c.getEndDate() %></span></div>
                            </div>

                            <!-- Full-Width Button or Active Joined Badge -->
                            <form action="challenges" method="post">
                                <input type="hidden" name="action" value="join">
                                <input type="hidden" name="challengeId" value="<%= c.getId() %>">
                                <% if (isEnrolled || c.isJoined()) { %>
                                    <button type="button" class="btn" style="width: 100%; background: var(--primary-emerald-light); border: 1px solid rgba(16, 185, 129, 0.3); color: var(--primary-emerald); cursor: default;" disabled>
                                        ✓ Joined
                                    </button>
                                <% } else { %>
                                    <button type="submit" class="btn btn-emerald" style="width: 100%;">
                                        Join Challenge
                                    </button>
                                <% } %>
                            </form>
                        </div>
                    </div>
            <%  }
               } else { %>
                <div style="grid-column: 1 / -1; text-align: center; color: var(--text-muted); padding: 4rem;">
                    No challenges active currently. Check back soon!
                </div>
            <% } %>
        </div>

        <!-- Section: My Enrolled Challenges -->
        <div class="card-panel" style="padding: 1.5rem;">
            <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; color: var(--text-main);">My Enrolled Challenges</h3>
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Challenge Title</th>
                            <th>Target Metric</th>
                            <th>Goal Requirement</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (enrolledChallenges != null && !enrolledChallenges.isEmpty()) {
                            for (Challenge ec : enrolledChallenges) { %>
                                <tr>
                                    <td><strong style="color: var(--text-main);"><%= ec.getTitle() %></strong></td>
                                    <td><span style="color: var(--badge-blue); font-weight: 700;"><%= ec.getTargetType() %></span></td>
                                    <td><%= ec.getTargetGoal() %> <%= ec.getTargetType() %></td>
                                    <td><%= ec.getStartDate() %></td>
                                    <td><%= ec.getEndDate() %></td>
                                    <td><span style="color: var(--success); font-weight: 800;">ACTIVE</span></td>
                                </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 2.5rem;">You are not enrolled in any active challenges yet.</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div>

</body>
</html>
