package ru.itis.timecapsule.web.servlet;

import ru.itis.timecapsule.service.AuthService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private AuthService auth;
    @Override public void init() { auth = (AuthService) getServletContext().getAttribute("authService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            auth.register(req.getParameter("email"), req.getParameter("displayName"), req.getParameter("password"));
            resp.sendRedirect(req.getContextPath() + "/login?registered=1");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Не удалось зарегистрировать пользователя. Проверь email и подключение к БД.");
            doGet(req, resp);
        }
    }
}
