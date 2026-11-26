package ru.itis.timecapsule.web.servlet;

import ru.itis.timecapsule.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private AdminService admin;
    @Override public void init() { admin = (AdminService) getServletContext().getAttribute("adminService"); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("categories", admin.getCategories());
            req.getRequestDispatcher("/WEB-INF/views/admin-categories.jsp").forward(req, resp);
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось загрузить категории.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) admin.createCategory(req.getParameter("name"));
            else if ("update".equals(action)) admin.updateCategory(Long.parseLong(req.getParameter("id")), req.getParameter("name"));
            else if ("delete".equals(action)) admin.deleteCategory(Long.parseLong(req.getParameter("id")));
            else { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
            resp.sendRedirect(req.getContextPath() + "/admin/categories?updated=1");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Не удалось изменить категорию. Возможно, она используется капсулами.");
            doGet(req, resp);
        }
    }
}
