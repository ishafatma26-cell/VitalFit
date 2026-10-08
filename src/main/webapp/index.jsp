<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VitalFit - Intelligent Fitness Analytics Platform</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <nav class="navbar">
        <a href="index.jsp" class="nav-brand">
            <div class="brand-icon">⚡</div>
            <span>VitalFit</span>
        </a>
        <div class="nav-links">
            <a href="login.jsp" class="nav-link">Sign In</a>
            <a href="login.jsp?tab=register" class="btn btn-primary" style="padding: 0.4rem 1rem;">Get Started</a>
        </div>
    </nav>

    <div class="main-container" style="display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center; min-height: 75vh;">
        <div style="max-width: 800px;">
            <div style="display: inline-block; padding: 0.35rem 1rem; background: var(--primary-light); border: 1px solid rgba(37, 99, 235, 0.3); border-radius: 30px; color: var(--primary); font-size: 0.85rem; font-weight: 700; margin-bottom: 1.5rem; text-transform: uppercase; letter-spacing: 1px;">
                Topic 8: Intelligent Fitness Tracking & Activity Analytics
            </div>
            <h1 style="font-size: 3.2rem; font-weight: 800; line-height: 1.1; margin-bottom: 1.5rem; color: var(--text-main);">
                Elevate Your Health with Intelligent Fitness Analytics
            </h1>
            <p style="font-size: 1.15rem; color: var(--text-muted); margin-bottom: 2.5rem; line-height: 1.6;">
                Track daily workouts, participate in competitive endurance challenges, analyze live streak metrics, and rise through the community leaderboard.
            </p>
            <div style="display: flex; gap: 1rem; justify-content: center;">
                <a href="login.jsp" class="btn btn-primary" style="padding: 0.85rem 2rem; font-size: 1.05rem;">Access Platform</a>
                <a href="login.jsp?tab=register" class="btn" style="background: #ffffff; border: 1px solid var(--border-color); color: var(--text-main); padding: 0.85rem 2rem; font-size: 1.05rem;">Create Account</a>
            </div>
        </div>

        <div class="grid-stats" style="margin-top: 4rem; width: 100%; max-width: 900px;">
            <div class="card-panel stat-card" style="text-align: left;">
                <span class="stat-label">Real-time Analytics</span>
                <span class="stat-value" style="font-size: 1.5rem; color: var(--primary);">Stream Engine</span>
                <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.5rem;">Powered by Java 8+ Stream API in-memory telemetry processing.</p>
            </div>
            <div class="card-panel stat-card" style="text-align: left;">
                <span class="stat-label">ACID Verification</span>
                <span class="stat-value" style="font-size: 1.5rem; color: var(--success);">Transactional</span>
                <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.5rem;">Database transactions with manual autoCommit control & rollback.</p>
            </div>
            <div class="card-panel stat-card" style="text-align: left;">
                <span class="stat-label">Non-blocking Audit</span>
                <span class="stat-value" style="font-size: 1.5rem; color: var(--warning);">Multithreaded</span>
                <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.5rem;">Managed ExecutorService thread pool asynchronous activity logging.</p>
            </div>
        </div>
    </div>

    <footer>
        VitalFit Platform &copy; 2026 - Jakarta EE 10 / Tomcat 10.1 & Neon PostgreSQL Engine.
    </footer>

</body>
</html>
