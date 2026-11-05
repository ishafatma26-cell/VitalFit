package com.vitalfit.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cross-Site Request Forgery (CSRF) Protection Filter.
 * Generates cryptographic random tokens per session and validates state-changing requests.
 * Rejects missing or mismatched tokens with HTTP 403 Forbidden.
 *
 * @author Isha Fatma (25SCSE1180892) - Team NOVA
 */
@WebFilter(filterName = "CsrfFilter", urlPatterns = "/*")
public class CsrfFilter implements Filter {

    public static final String CSRF_TOKEN_ATTR = "csrfToken";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contextPath = req.getContextPath() != null ? req.getContextPath() : "";
        String requestUri = req.getRequestURI() != null ? req.getRequestURI() : "";
        String requestPath = (requestUri.startsWith(contextPath) && !contextPath.isEmpty())
                ? requestUri.substring(contextPath.length())
                : requestUri;

        // Static asset bypass check
        if (isStaticAsset(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        // Always ensure active session exists on incoming requests
        HttpSession session = req.getSession(true);
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTR);

        if (sessionToken == null || sessionToken.isBlank()) {
            sessionToken = generateToken();
            session.setAttribute(CSRF_TOKEN_ATTR, sessionToken);
        }

        // Expose token to request scope so JSPs can reliably access via ${csrfToken}
        req.setAttribute(CSRF_TOKEN_ATTR, sessionToken);

        String method = req.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            String requestToken = req.getParameter("csrfToken");
            if (requestToken == null || requestToken.trim().isEmpty()) {
                requestToken = req.getHeader("X-CSRF-TOKEN");
                if (requestToken == null || requestToken.trim().isEmpty()) {
                    requestToken = req.getHeader("X-CSRF-Token");
                }
            }

            if (requestToken == null || !sessionToken.equals(requestToken.trim())) {
                String servletPath = req.getServletPath();
                if ("/login".equals(servletPath) || "/register".equals(servletPath)) {
                    String freshToken = generateToken();
                    session.setAttribute(CSRF_TOKEN_ATTR, freshToken);
                    resp.sendRedirect(req.getContextPath() + "/login.jsp?error=session_expired");
                    return;
                }
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isStaticAsset(String path) {
        if (path == null) return false;
        String lower = path.toLowerCase();
        return lower.startsWith("/css/") || lower.startsWith("/js/") || lower.startsWith("/assets/")
                || lower.startsWith("/images/") || lower.endsWith(".css") || lower.endsWith(".js")
                || lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".gif") || lower.endsWith(".ico") || lower.endsWith(".svg")
                || lower.endsWith(".woff") || lower.endsWith(".woff2") || lower.endsWith(".ttf");
    }

    public static String generateToken() {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public void destroy() {}
}
