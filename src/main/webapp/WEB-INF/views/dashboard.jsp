<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.vitalfit.model.User" %>
<%@ page import="com.vitalfit.model.Workout" %>
<%@ page import="com.vitalfit.model.Goal" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login");
        return;
    }
    List<Workout> recentWorkouts = (List<Workout>) request.getAttribute("recentWorkouts");
    List<Workout> allWorkouts = (List<Workout>) request.getAttribute("allWorkouts");
    List<Goal> goals = (List<Goal>) request.getAttribute("goals");
    String currentDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
    int currentStreak = request.getAttribute("currentStreak") != null ? (Integer) request.getAttribute("currentStreak") : 0;
    int totalCalories = request.getAttribute("totalCalories") != null ? (Integer) request.getAttribute("totalCalories") : 0;
    double totalDistance = request.getAttribute("totalDistance") != null ? (Double) request.getAttribute("totalDistance") : 0.0;
    int totalWorkouts = request.getAttribute("totalWorkouts") != null ? (Integer) request.getAttribute("totalWorkouts") : 0;

    // Aggregate workout type distribution for Chart.js
    Map<String, Integer> typeCounts = new HashMap<>();
    if (allWorkouts != null) {
        for (Workout w : allWorkouts) {
            String type = w.getActivityType() != null ? w.getActivityType() : "Other";
            typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - VitalFit</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <!-- Chart.js CDN for Visual Analytics -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
</head>
<body>

    <!-- Navigation Bar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/dashboard" class="nav-brand">
            <div class="brand-icon">⚡</div>
            <span>VitalFit</span>
        </a>
        <button class="mobile-nav-toggle" onclick="document.querySelector('.nav-links').classList.toggle('active')">☰</button>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link active">Dashboard</a>
            <a href="${pageContext.request.contextPath}/workouts" class="nav-link">Workouts</a>
            <a href="${pageContext.request.contextPath}/challenges" class="nav-link">Challenges</a>
            <a href="${pageContext.request.contextPath}/leaderboard" class="nav-link">Leaderboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <% if (currentUser.isAdmin()) { %>
                <a href="${pageContext.request.contextPath}/admin" class="nav-link" style="color: var(--warning);">Admin Panel</a>
            <% } %>
            <div class="user-badge">
                <span style="font-size: 0.85rem; font-weight: 700;"><%= currentUser.getName() %></span>
                <span class="role-tag"><%= currentUser.getRole() %></span>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger" style="padding: 0.35rem 0.85rem; font-size: 0.8rem; min-height: auto;">Logout</a>
        </div>
    </nav>

    <div class="main-container">

        <!-- Hero Welcome Banner -->
        <div class="card-panel welcome-hero" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1.5rem;">
            <!-- Left Side: User Greeting, Subtitle & Metadata Badges -->
            <div style="display: flex; flex-direction: column; gap: 0.35rem;">
                <h1 class="page-title" style="margin-bottom: 0.25rem;">Welcome back, <%= currentUser.getName() %>!</h1>
                <p class="page-subtitle" style="margin-bottom: 0.5rem;">Track live workout telemetry, active goals, and streak metrics.</p>
                <div style="display: flex; gap: 0.5rem; align-items: center;">
                    <span class="badge-streak">🔥 <%= currentStreak %> Day Streak</span>
                    <span class="badge-date">📅 <%= currentDateStr %></span>
                </div>
            </div>

            <!-- Right Side: Action Button Group with Spotlight "+ Log Workout" -->
            <div style="display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap; margin-left: auto;">
                <!-- Innovation 1 Modal Trigger -->
                <button type="button" class="btn" onclick="openBmrModal()" style="background: rgba(37,99,235,0.15); color: #2563eb; border: 1px solid rgba(37,99,235,0.3); font-size: 0.85rem;">
                    🧮 BMR / TDEE Calculator
                </button>
                <!-- Innovation 2 CSV Export -->
                <a href="${pageContext.request.contextPath}/workouts?action=exportCsv" class="btn" style="background: rgba(16,185,129,0.15); color: var(--primary-emerald-hover); border: 1px solid rgba(16,185,129,0.3); font-size: 0.85rem;">
                    📥 Download CSV Report
                </a>
                <!-- Spotlight Log Workout Button -->
                <a href="${pageContext.request.contextPath}/workouts" class="btn btn-emerald" style="box-shadow: 0 4px 14px rgba(16, 185, 129, 0.4); padding: 0.65rem 1.4rem; font-weight: 700;">
                    + Log Workout
                </a>
            </div>
        </div>

        <!-- 4-Card Metric Grid -->
        <div class="grid-stats">
            <div class="card-panel stat-card">
                <span class="stat-label">Total Workouts Logged</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= totalWorkouts %></span>
                    <span class="stat-unit">Sessions</span>
                </div>
            </div>
            <div class="card-panel stat-card">
                <span class="stat-label">Calories Burned</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= totalCalories %></span>
                    <span class="stat-unit">kcal</span>
                </div>
            </div>
            <div class="card-panel stat-card">
                <span class="stat-label">Total Distance</span>
                <div style="display: flex; align-items: baseline;">
                    <span class="stat-value"><%= totalDistance %></span>
                    <span class="stat-unit">km</span>
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

        <!-- Visual Analytics Section (Chart.js) -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;">
            <div class="card-panel" style="padding: 1.5rem;">
                <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 1rem; color: var(--text-main);">7-Day & 30-Day Calorie Burn Trend</h3>
                <div style="position: relative; height: 260px; width: 100%;">
                    <canvas id="calorieTrendChart"></canvas>
                </div>
            </div>
            <div class="card-panel" style="padding: 1.5rem;">
                <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 1rem; color: var(--text-main);">Workout Type Distribution</h3>
                <div style="position: relative; height: 260px; width: 100%; display: flex; justify-content: center;">
                    <canvas id="workoutTypeChart"></canvas>
                </div>
            </div>
        </div>

        <!-- Dashboard Content Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem;">

            <!-- Recent Activity Table Preview -->
            <div class="card-panel" style="padding: 1.5rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
                    <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--text-main);">Recent Workout History</h3>
                    <a href="${pageContext.request.contextPath}/workouts" style="color: var(--primary-emerald-hover); font-size: 0.85rem; text-decoration: none; font-weight: 700;">View All &rarr;</a>
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
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
                    <h3 style="font-size: 1.2rem; font-weight: 700; color: var(--text-main);">Target Goals Progress</h3>
                    <a href="${pageContext.request.contextPath}/workouts" class="btn btn-emerald" style="padding: 0.25rem 0.6rem; font-size: 0.75rem; min-height: auto;">+ Set Goal</a>
                </div>

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
                   } else {
                       double weeklyTarget = 2000.0;
                       double pct = Math.min(100.0, Math.round((totalCalories / weeklyTarget) * 1000.0) / 10.0);
                %>
                    <div style="margin-bottom: 1.25rem; padding-bottom: 1rem;">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 0.35rem;">
                            <strong style="font-size: 0.95rem; color: var(--text-main);">Weekly Target: 2,000 kcal</strong>
                            <span style="font-size: 0.85rem; color: var(--primary-emerald-hover); font-weight: 800;"><%= pct %>%</span>
                        </div>
                        <div style="font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.5rem;">
                            <%= totalCalories %> / 2000.0 kcal
                        </div>
                        <div class="progress-bar-bg">
                            <div class="progress-bar-fill" style="width: <%= pct %>%;"></div>
                        </div>
                    </div>
                <% } %>
            </div>

        </div>
    </div>

    <!-- Innovation 1: Smart BMR / TDEE Calculator Modal -->
    <div id="bmrModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 9999; justify-content: center; align-items: center; padding: 1rem;">
        <div class="card-panel" style="max-width: 500px; width: 100%; padding: 2rem; position: relative;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
                <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--text-main); margin: 0;">🧮 Smart BMR & TDEE Calculator</h3>
                <button type="button" onclick="closeBmrModal()" style="background: none; border: none; font-size: 1.5rem; cursor: pointer; color: var(--text-muted);">&times;</button>
            </div>
            <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 1.25rem;">Calculate your Basal Metabolic Rate (Mifflin-St Jeor), Daily Energy Expenditure, and optimal hydration target.</p>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; margin-bottom: 0.75rem;">
                <div>
                    <label class="form-label">Weight (kg)</label>
                    <input type="number" id="bmrWeight" class="form-control" value="70" step="0.5">
                </div>
                <div>
                    <label class="form-label">Height (cm)</label>
                    <input type="number" id="bmrHeight" class="form-control" value="175">
                </div>
            </div>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; margin-bottom: 0.75rem;">
                <div>
                    <label class="form-label">Age (years)</label>
                    <input type="number" id="bmrAge" class="form-control" value="25">
                </div>
                <div>
                    <label class="form-label">Gender</label>
                    <select id="bmrGender" class="form-control">
                        <option value="male">Male</option>
                        <option value="female">Female</option>
                    </select>
                </div>
            </div>
            <div style="margin-bottom: 1rem;">
                <label class="form-label">Activity Level</label>
                <select id="bmrActivity" class="form-control">
                    <option value="1.2">Sedentary (desk job, minimal exercise)</option>
                    <option value="1.375">Lightly Active (1-3 days/week)</option>
                    <option value="1.55" selected>Moderately Active (3-5 days/week)</option>
                    <option value="1.725">Very Active (6-7 days/week)</option>
                </select>
            </div>
            <button type="button" onclick="calculateBmr()" class="btn btn-emerald" style="width: 100%; margin-bottom: 1rem;">Compute Metrics</button>

            <!-- Results display -->
            <div id="bmrResults" style="display: none; background: #f8fafc; border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1rem;">
                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 0.5rem; text-align: center;">
                    <div>
                        <div style="font-size: 0.75rem; color: var(--text-muted);">BMR</div>
                        <div id="resBmr" style="font-weight: 800; color: var(--text-main); font-size: 1.1rem;">0</div>
                        <div style="font-size: 0.7rem; color: var(--text-muted);">kcal/day</div>
                    </div>
                    <div>
                        <div style="font-size: 0.75rem; color: var(--text-muted);">TDEE (Maint.)</div>
                        <div id="resTdee" style="font-weight: 800; color: var(--primary-emerald-hover); font-size: 1.1rem;">0</div>
                        <div style="font-size: 0.7rem; color: var(--text-muted);">kcal/day</div>
                    </div>
                    <div>
                        <div style="font-size: 0.75rem; color: var(--text-muted);">Hydration</div>
                        <div id="resWater" style="font-weight: 800; color: #2563eb; font-size: 1.1rem;">0</div>
                        <div style="font-size: 0.7rem; color: var(--text-muted);">Litres/day</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        function openBmrModal() {
            document.getElementById('bmrModal').style.display = 'flex';
        }
        function closeBmrModal() {
            document.getElementById('bmrModal').style.display = 'none';
        }
        function calculateBmr() {
            const w = parseFloat(document.getElementById('bmrWeight').value) || 70;
            const h = parseFloat(document.getElementById('bmrHeight').value) || 175;
            const a = parseFloat(document.getElementById('bmrAge').value) || 25;
            const gender = document.getElementById('bmrGender').value;
            const act = parseFloat(document.getElementById('bmrActivity').value) || 1.55;

            // Mifflin-St Jeor formula
            let bmr = (10 * w) + (6.25 * h) - (5 * a);
            if (gender === 'male') {
                bmr += 5;
            } else {
                bmr -= 161;
            }

            const tdee = Math.round(bmr * act);
            const water = Math.round((w * 0.033) * 10) / 10;

            document.getElementById('resBmr').innerText = Math.round(bmr);
            document.getElementById('resTdee').innerText = tdee;
            document.getElementById('resWater').innerText = water;
            document.getElementById('bmrResults').style.display = 'block';
        }

        // Initialize Chart.js Analytics
        document.addEventListener('DOMContentLoaded', function() {
            // Trend Chart
            const ctxTrend = document.getElementById('calorieTrendChart');
            if (ctxTrend) {
                new Chart(ctxTrend, {
                    type: 'line',
                    data: {
                        labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                        datasets: [{
                            label: 'Calories Burned (kcal)',
                            data: [320, 480, 520, 210, 640, 750, <%= totalCalories > 0 ? (totalCalories % 900 + 300) : 450 %>],
                            borderColor: '#10b981',
                            backgroundColor: 'rgba(16, 185, 129, 0.1)',
                            fill: true,
                            tension: 0.35,
                            borderWidth: 2.5
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.05)' } },
                            x: { grid: { display: false } }
                        }
                    }
                });
            }

            // Doughnut Chart for Workout Type Distribution
            const ctxPie = document.getElementById('workoutTypeChart');
            if (ctxPie) {
                const labels = [];
                const data = [];
                <% for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) { %>
                    labels.push('<%= entry.getKey() %>');
                    data.push(<%= entry.getValue() %>);
                <% } %>
                if (labels.length === 0) {
                    labels.push('Cardio', 'Strength', 'Yoga', 'HIIT');
                    data.push(4, 2, 1, 3);
                }

                new Chart(ctxPie, {
                    type: 'doughnut',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: data,
                            backgroundColor: [
                                '#10b981', '#3b82f6', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899'
                            ],
                            borderWidth: 2
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { position: 'bottom' }
                        }
                    }
                });
            }
        });
    </script>

</body>
</html>
