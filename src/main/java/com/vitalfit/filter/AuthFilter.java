package com.vitalfit.filter;

import com.vitalfit.model.UserPrincipal;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Role-based Authentication & Authorization Filter.
 * Protects user routes (/dashboard, /user/dashboard, /workouts, /challenges, /leaderboard, /profile, /user/*)
 * and administrator routes (/admin, /admin/*).
 *
 * @author Isha Fatma (25SCSE1180892) - Team NOVA
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {
        "/dashboard", "/user/dashboard", "/workouts", "/challenges", "/leaderboard", "/profile",
        "/user/*", "/admin", "/admin/*"
})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String requestUri = req.getRequestURI();
        String requestPath = requestUri.substring(contextPath.length());

        // Explicit static asset bypass
        if (requestPath.startsWith("/css/") ||
            requestPath.startsWith("/js/") ||
            requestPath.startsWith("/assets/") ||
            requestPath.startsWith("/images/") ||
            requestPath.endsWith(".css") ||
            requestPath.endsWith(".js") ||
            requestPath.endsWith(".png") ||
            requestPath.endsWith(".jpg") ||
            requestPath.endsWith(".jpeg") ||
            requestPath.endsWith(".ico") ||
            requestPath.endsWith(".svg") ||
            requestPath.endsWith(".woff") ||
            requestPath.endsWith(".woff2") ||
            requestPath.endsWith(".ttf")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        UserPrincipal principal = (session != null) ? (UserPrincipal) session.getAttribute("userPrincipal") : null;

        if (principal == null && session != null) {
            Object userObj = session.getAttribute("user");
            if (userObj instanceof com.vitalfit.model.User) {
                com.vitalfit.model.User u = (com.vitalfit.model.User) userObj;
                principal = UserPrincipal.fromUser(u);
                session.setAttribute("userPrincipal", principal);
            }
        }

        if (principal == null) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        // Role-based access control for admin routes
        if (requestPath.startsWith("/admin")) {
            if (!principal.isAdmin()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator role required.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
