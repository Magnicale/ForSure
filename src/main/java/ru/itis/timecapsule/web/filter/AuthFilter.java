package ru.itis.timecapsule.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/capsules", "/capsules/*", "/media", "/api/*", "/logout", "/admin/*"})
public class AuthFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) throws ServletException { }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Cache-Control", "no-store");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() { }
}
