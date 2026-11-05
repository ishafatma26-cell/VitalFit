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

        HttpSession session = req.getSession(true);
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTR);

        if (sessionToken == null) {
            byte[] randomBytes = new byte[32];
            SECURE_RANDOM.nextBytes(randomBytes);
            sessionToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
            session.setAttribute(CSRF_TOKEN_ATTR, sessionToken);
        }

        req.setAttribute(CSRF_TOKEN_ATTR, sessionToken);

        String method = req.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            String requestToken = req.getParameter("csrfToken");
            if (requestToken == null || requestToken.isBlank()) {
                requestToken = req.getHeader("X-CSRF-Token");
            }

            if (requestToken == null || !requestToken.equals(sessionToken)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
