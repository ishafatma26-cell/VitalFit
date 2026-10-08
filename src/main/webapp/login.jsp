<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String msg = request.getParameter("msg");
    String tab = request.getParameter("tab");
    boolean isRegister = "register".equalsIgnoreCase(tab);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In - VitalFit</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <nav class="navbar">
        <a href="index.jsp" class="nav-brand">
            <div class="brand-icon">⚡</div>
            <span>VitalFit</span>
        </a>
    </nav>

    <div class="main-container" style="display: flex; justify-content: center; align-items: center; min-height: 80vh;">
        <div class="card-panel" style="width: 100%; max-width: 440px; padding: 2.5rem;">

            <div style="text-align: center; margin-bottom: 2rem;">
                <h2 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.5rem; color: var(--text-main);"><%= isRegister ? "Create Account" : "Welcome Back" %></h2>
                <p style="color: var(--text-muted); font-size: 0.9rem;"><%= isRegister ? "Join VitalFit to start tracking your health journey" : "Enter your credentials to access your dashboard" %></p>
            </div>

            <% if ("logged_out".equals(msg)) { %>
                <div class="alert alert-info">You have been signed out successfully.</div>
            <% } %>

            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("errorMessage") %></div>
            <% } %>

            <div style="display: flex; background: #f1f5f9; border-radius: var(--radius-md); padding: 0.25rem; margin-bottom: 1.5rem; border: 1px solid var(--border-color);">
                <a href="login.jsp" class="btn" style="flex: 1; padding: 0.5rem; font-size: 0.85rem; border-radius: var(--radius-sm); color: <%= !isRegister ? "var(--primary)" : "var(--text-muted)" %>; background: <%= !isRegister ? "#ffffff" : "transparent" %>; box-shadow: <%= !isRegister ? "var(--shadow-card)" : "none" %>;">Sign In</a>
                <a href="login.jsp?tab=register" class="btn" style="flex: 1; padding: 0.5rem; font-size: 0.85rem; border-radius: var(--radius-sm); color: <%= isRegister ? "var(--primary)" : "var(--text-muted)" %>; background: <%= isRegister ? "#ffffff" : "transparent" %>; box-shadow: <%= isRegister ? "var(--shadow-card)" : "none" %>;">Register</a>
            </div>

            <% if (!isRegister) { %>
                <form action="login" method="post">
                    <div class="form-group">
                        <label class="form-label">Email Address</label>
                        <input type="email" name="email" class="form-control" placeholder="user@vitalfit.demo" required value="user@vitalfit.demo">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Password</label>
                        <input type="password" name="password" class="form-control" placeholder="••••••••" required value="User@123">
                    </div>
                    <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 1rem;">Sign In</button>
                </form>

                <div style="margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid var(--border-color); font-size: 0.8rem; color: var(--text-muted);">
                    <strong>Demo Accounts:</strong><br>
                    • User: <code>user@vitalfit.demo</code> / <code>User@123</code><br>
                    • Admin: <code>admin@vitalfit.com</code> / <code>Admin@123</code>
                </div>
            <% } else { %>
                <form action="register" method="post">
                    <div class="form-group">
                        <label class="form-label">Full Name</label>
                        <input type="text" name="name" class="form-control" placeholder="John Doe" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Email Address</label>
                        <input type="email" name="email" class="form-control" placeholder="you@domain.com" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Password</label>
                        <input type="password" name="password" class="form-control" placeholder="••••••••" required>
                    </div>
                    <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 1rem;">Create Account</button>
                </form>
            <% } %>

        </div>
    </div>

</body>
</html>
