package ru.itis.timecapsule.web.servlet;

import ru.itis.timecapsule.model.User;
import ru.itis.timecapsule.service.AuthService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AuthService auth;
    @Override public void init() { auth = (AuthService) getServletContext().getAttribute("authService"); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Optional<User> result = auth.login(req.getParameter("email"), req.getParameter("password"));
            if (result.isEmpty()) {
                req.setAttribute("error", "Неверный email или пароль.");
                doGet(req, resp);
                return;
            }
            User user = result.get();
            HttpSession session = req.getSession(true);
            req.changeSessionId();
            session.setAttribute("userId", user.getId());
            session.setAttribute("displayName", user.getDisplayName());
            session.setAttribute("role", user.getRole().name());
            resp.sendRedirect(req.getContextPath() + "/capsules");
        } catch (SQLException e) {
            req.setAttribute("error", "Не удалось подключиться к базе данных.");
            doGet(req, resp);
        }
    }
}
