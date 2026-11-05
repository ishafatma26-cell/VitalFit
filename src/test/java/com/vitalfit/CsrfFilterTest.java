package com.vitalfit;

import com.vitalfit.filter.CsrfFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CsrfFilter validation on HTTP methods, asset bypass, and token checks.
 *
 * @author Isha Fatma (25SCSE1180892) - Team NOVA
 */
@ExtendWith(MockitoExtension.class)
public class CsrfFilterTest {

    private CsrfFilter csrfFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private FilterChain chain;

    @BeforeEach
    public void setup() {
        csrfFilter = new CsrfFilter();
    }

    @Test
    @DisplayName("Static assets bypass CSRF checks entirely without requiring session or token")
    public void testStaticAssetBypassesCsrfFilter() throws Exception {
        when(request.getRequestURI()).thenReturn("/css/style.css");

        csrfFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(request, never()).getSession(true);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    @DisplayName("GET requests pass through without requiring CSRF token validation and populate request token")
    public void testGetRequestPassesThrough() throws Exception {
        when(request.getRequestURI()).thenReturn("/login");
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute(CsrfFilter.CSRF_TOKEN_ATTR)).thenReturn("valid-token-123");
        when(request.getMethod()).thenReturn("GET");

        csrfFilter.doFilter(request, response, chain);

        verify(request, times(1)).setAttribute(eq(CsrfFilter.CSRF_TOKEN_ATTR), eq("valid-token-123"));
        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
    }

    @Test
    @DisplayName("POST request without CSRF token on authenticated route is rejected with HTTP 403 Forbidden")
    public void testPostRequestWithoutTokenIsBlocked() throws Exception {
        when(request.getRequestURI()).thenReturn("/workouts");
        when(request.getServletPath()).thenReturn("/workouts");
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute(CsrfFilter.CSRF_TOKEN_ATTR)).thenReturn("valid-token-123");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("csrfToken")).thenReturn(null);
        when(request.getHeader("X-CSRF-TOKEN")).thenReturn(null);
        when(request.getHeader("X-CSRF-Token")).thenReturn(null);

        csrfFilter.doFilter(request, response, chain);

        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("POST request with expired/missing token on /login redirects to login with session_expired error")
    public void testPostRequestOnLoginRedirectsGracefully() throws Exception {
        when(request.getRequestURI()).thenReturn("/login");
        when(request.getServletPath()).thenReturn("/login");
        when(request.getContextPath()).thenReturn("");
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute(CsrfFilter.CSRF_TOKEN_ATTR)).thenReturn("old-token");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("csrfToken")).thenReturn("mismatched");

        csrfFilter.doFilter(request, response, chain);

        verify(response, times(1)).sendRedirect(eq("/login.jsp?error=session_expired"));
        verify(response, never()).sendError(anyInt(), anyString());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("POST request with mismatched/tampered CSRF token is blocked with HTTP 403")
    public void testPostRequestWithMismatchedTokenIsBlocked() throws Exception {
        when(request.getRequestURI()).thenReturn("/workouts");
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute(CsrfFilter.CSRF_TOKEN_ATTR)).thenReturn("session-secret-token");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("csrfToken")).thenReturn("attacker-tampered-token");

        csrfFilter.doFilter(request, response, chain);

        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("POST request with valid matching CSRF token is allowed to proceed")
    public void testPostRequestWithValidTokenProceeds() throws Exception {
        when(request.getRequestURI()).thenReturn("/workouts");
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute(CsrfFilter.CSRF_TOKEN_ATTR)).thenReturn("valid-match-xyz");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("csrfToken")).thenReturn("valid-match-xyz");

        csrfFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
    }

    @Test
    @DisplayName("POST request with valid header X-CSRF-TOKEN is allowed to proceed")
    public void testPostRequestWithValidHeaderTokenProceeds() throws Exception {
        when(request.getRequestURI()).thenReturn("/workouts");
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute(CsrfFilter.CSRF_TOKEN_ATTR)).thenReturn("valid-header-token");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("csrfToken")).thenReturn(null);
        when(request.getHeader("X-CSRF-TOKEN")).thenReturn("valid-header-token");

        csrfFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
    }
}
