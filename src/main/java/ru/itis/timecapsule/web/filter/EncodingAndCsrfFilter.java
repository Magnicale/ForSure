package ru.itis.timecapsule.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@WebFilter("/*")
public class EncodingAndCsrfFilter implements Filter {
    private static final SecureRandom RANDOM = new SecureRandom();
    @Override public void init(FilterConfig filterConfig) throws ServletException { }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "same-origin");
        HttpSession session = req.getSession(true);
        if (session.getAttribute("csrfToken") == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            session.setAttribute("csrfToken", Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
        }
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String expected = (String) session.getAttribute("csrfToken");
            if (expected == null || !expected.equals(req.getParameter("csrfToken"))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Проверка формы не пройдена.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() { }
}
