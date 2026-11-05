<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String msg = request.getParameter("msg");
    String err = request.getParameter("error");
    String tab = request.getParameter("tab");
    boolean isRegister = "register".equalsIgnoreCase(tab);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In - VitalFit</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/login.jsp" class="nav-brand">
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
            <% } else if ("password_reset_success".equals(msg)) { %>
                <div class="alert alert-info" style="background: rgba(16, 185, 129, 0.15); border-color: rgba(16, 185, 129, 0.3); color: #047857;">
                    Your password has been successfully reset. Please log in with your new password.
                </div>
            <% } %>

            <% if ("session_expired".equals(err)) { %>
                <div class="alert alert-error">Session expired or invalid token. Please try logging in again.</div>
            <% } else if ("invalid_credentials".equals(err)) { %>
                <div class="alert alert-error">Invalid email or password. Please try again.</div>
            <% } %>

            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("errorMessage") %></div>
            <% } %>

            <div style="display: flex; background: #f1f5f9; border-radius: var(--radius-md); padding: 0.25rem; margin-bottom: 1.5rem; border: 1px solid var(--border-color);">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn" style="flex: 1; padding: 0.5rem; font-size: 0.85rem; border-radius: var(--radius-sm); color: <%= !isRegister ? "var(--primary-emerald-hover)" : "var(--text-muted)" %>; background: <%= !isRegister ? "#ffffff" : "transparent" %>; box-shadow: <%= !isRegister ? "var(--shadow-card)" : "none" %>;">Sign In</a>
                <a href="${pageContext.request.contextPath}/login.jsp?tab=register" class="btn" style="flex: 1; padding: 0.5rem; font-size: 0.85rem; border-radius: var(--radius-sm); color: <%= isRegister ? "var(--primary-emerald-hover)" : "var(--text-muted)" %>; background: <%= isRegister ? "#ffffff" : "transparent" %>; box-shadow: <%= isRegister ? "var(--shadow-card)" : "none" %>;">Register</a>
            </div>

            <% if (!isRegister) { %>
                <form action="${pageContext.request.contextPath}/login" method="post">
                    <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.csrfToken}" />
                    <div class="form-group">
                        <label class="form-label">Email Address</label>
                        <input type="email" name="email" class="form-control" placeholder="user@vitalfit.demo" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Password</label>
                        <input type="password" name="password" class="form-control" placeholder="••••••••" required>
                    </div>
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <label style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; color: var(--text-muted); cursor: pointer;">
                            <input type="checkbox" id="rememberMe" name="rememberMe"> Remember me
                        </label>
                        <a href="${pageContext.request.contextPath}/forgot-password" style="font-size: 0.85rem; color: var(--primary-emerald); text-decoration: none; font-weight: 500;">
                            Forgot password?
                        </a>
                    </div>
                    <button type="submit" class="btn btn-emerald" style="width: 100%; margin-top: 0.5rem;">Sign In</button>
                </form>
            <% } else { %>
                <form action="${pageContext.request.contextPath}/register" method="post">
                    <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.csrfToken}" />
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
                    <button type="submit" class="btn btn-emerald" style="width: 100%; margin-top: 1rem;">Create Account</button>
                </form>
            <% } %>

        </div>
    </div>

</body>
</html>
