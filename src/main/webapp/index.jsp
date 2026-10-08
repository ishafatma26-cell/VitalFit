<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VitalFit - Intelligent Fitness Tracking</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <nav class="navbar">
        <a href="index.jsp" class="nav-brand">
            <div class="brand-icon">V</div>
            <span>VitalFit</span>
        </a>
        <div class="nav-links">
            <a href="login.jsp" class="nav-link">Sign In</a>
            <a href="login.jsp?tab=register" class="btn btn-primary" style="padding: 0.4rem 1rem;">Get Started</a>
        </div>
    </nav>

    <div class="main-container" style="display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center; min-height: 75vh;">
        <div style="max-width: 800px;">
            <div style="display: inline-block; padding: 0.35rem 1rem; background: rgba(6, 182, 212, 0.15); border: 1px solid rgba(6, 182, 212, 0.3); border-radius: 30px; color: var(--accent-cyan); font-size: 0.85rem; font-weight: 700; margin-bottom: 1.5rem; text-transform: uppercase; letter-spacing: 1px;">
                Topic 8: Intelligent Fitness Tracking & Activity Analytics
            </div>
            <h1 style="font-size: 3.5rem; font-weight: 800; line-height: 1.1; margin-bottom: 1.5rem; background: linear-gradient(135deg, #ffffff 0%, #94a3b8 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">
                Elevate Your Health with Intelligent Analytics
            </h1>
            <p style="font-size: 1.2rem; color: var(--text-muted); margin-bottom: 2.5rem; line-height: 1.6;">
                Track workouts, join competitive endurance challenges, analyze live streak metrics, and rise through the community leaderboard.
            </p>
            <div style="display: flex; gap: 1rem; justify-content: center;">
                <a href="login.jsp" class="btn btn-primary" style="padding: 0.85rem 2rem; font-size: 1.05rem;">Access Portal</a>
                <a href="login.jsp?tab=register" class="btn" style="background: rgba(255, 255, 255, 0.08); border: 1px solid var(--border-color); color: #fff; padding: 0.85rem 2rem; font-size: 1.05rem;">Create Free Account</a>
            </div>
        </div>

        <div class="grid-stats" style="margin-top: 4rem; width: 100%; max-width: 900px;">
            <div class="glass-panel stat-card" style="text-align: left;">
                <span class="stat-label">Real-time Analytics</span>
                <span class="stat-value" style="font-size: 1.5rem; color: var(--accent-cyan);">Stream Aggregation</span>
                <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.5rem;">Powered by Java 8+ Stream API in-memory pipeline.</p>
            </div>
            <div class="glass-panel stat-card" style="text-align: left;">
                <span class="stat-label">ACID Security</span>
                <span class="stat-value" style="font-size: 1.5rem; color: var(--success);">Transactional</span>
                <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.5rem;">Database transactions with manual autoCommit control & rollback.</p>
            </div>
            <div class="glass-panel stat-card" style="text-align: left;">
                <span class="stat-label">Non-blocking Audit</span>
                <span class="stat-value" style="font-size: 1.5rem; color: var(--warning);">Multithreaded</span>
                <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.5rem;">Managed ExecutorService fixed thread pool async logging.</p>
            </div>
        </div>
    </div>

    <footer>
        VitalFit Platform &copy; 2025 - Jakarta EE 10 / Tomcat 10.1 & Neon PostgreSQL Engine.
    </footer>

</body>
</html>
