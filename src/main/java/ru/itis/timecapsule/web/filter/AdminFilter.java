package ru.itis.timecapsule.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) throws ServletException { }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Только для администратора.");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() { }
}
