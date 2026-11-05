<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - VitalFit</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="bg-light">

    <nav class="navbar navbar-light bg-white border-bottom shadow-sm">
        <div class="container">
            <a href="${pageContext.request.contextPath}/login.jsp" class="navbar-brand d-flex align-items-center gap-2 fw-bold text-success">
                <span class="fs-4">⚡</span>
                <span>VitalFit</span>
            </a>
        </div>
    </nav>

    <div class="container d-flex justify-content-center align-items-center" style="min-height: 80vh;">
        <div class="card shadow-sm border-0 rounded-4" style="width: 100%; max-width: 440px;">
            <div class="card-body p-4 p-md-5">

                <div class="text-center mb-4">
                    <div class="d-inline-flex align-items-center justify-content-center bg-success bg-opacity-10 text-success rounded-circle mb-3" style="width: 56px; height: 56px;">
                        <i class="bi bi-shield-lock-fill fs-3"></i>
                    </div>
                    <h2 class="h4 fw-bold text-dark mb-1">Reset Password</h2>
                    <p class="text-muted small">Enter your registered email and your new password to restore account access.</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show small" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i> ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                    <input type="hidden" name="csrfToken" value="${csrfToken != null ? csrfToken : sessionScope.csrfToken}" />

                    <div class="mb-3">
                        <label class="form-label small fw-semibold text-secondary">Registered Email Address</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-envelope"></i></span>
                            <input type="email" name="email" class="form-control border-start-0 ps-0" placeholder="user@vitalfit.demo" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label small fw-semibold text-secondary">New Password</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-key"></i></span>
                            <input type="password" name="newPassword" class="form-control border-start-0 ps-0" placeholder="••••••••" minlength="6" required>
                        </div>
                        <div class="form-text small">Must be at least 6 characters long.</div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label small fw-semibold text-secondary">Confirm New Password</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-check2-circle"></i></span>
                            <input type="password" name="confirmPassword" class="form-control border-start-0 ps-0" placeholder="••••••••" minlength="6" required>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-success w-100 py-2 fw-semibold mb-3">
                        Reset Password
                    </button>

                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/login" class="text-decoration-none small text-muted">
                            <i class="bi bi-arrow-left me-1"></i> Back to Login
                        </a>
                    </div>
                </form>

            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
