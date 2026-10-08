<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>500 - System Exception</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="main-container" style="display: flex; justify-content: center; align-items: center; min-height: 80vh; text-align: center;">
        <div class="glass-panel" style="padding: 3rem; max-width: 500px;">
            <h1 style="font-size: 5rem; font-weight: 800; color: var(--danger); line-height: 1;">500</h1>
            <h2 style="margin: 1rem 0; font-weight: 700;">Internal Server Error</h2>
            <p style="color: var(--text-muted); margin-bottom: 2rem;">An unexpected server or database exception occurred while processing your request.</p>
            <a href="dashboard" class="btn btn-primary">Return to Dashboard</a>
        </div>
    </div>
</body>
</html>
