package ru.itis.timecapsule.web.servlet;

import ru.itis.timecapsule.model.Capsule;
import ru.itis.timecapsule.dto.UploadedImage;
import ru.itis.timecapsule.service.AdminService;
import ru.itis.timecapsule.service.CapsuleService;
import ru.itis.timecapsule.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/capsules/*")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class CapsulesServlet extends HttpServlet {
    private CapsuleService capsules;
    private AdminService admin;

    @Override public void init() {
        capsules = (CapsuleService) getServletContext().getAttribute("capsuleService");
        admin = (AdminService) getServletContext().getAttribute("adminService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        try {
            if ("/new".equals(path)) {
                prepareForm(req);
                req.setAttribute("requestKey", UUID.randomUUID().toString());
                req.getRequestDispatcher("/WEB-INF/views/capsule-form.jsp").forward(req, resp);
            } else if ("/edit".equals(path) || "/view".equals(path)) {
                Capsule capsule = capsules.get(WebUtil.requiredId(req.getParameter("id")));
                if ("/edit".equals(path)) {
                    checkOwner(capsule, req);
                    if (!capsule.isUnlocked()) throw new SecurityException("Запечатанную капсулу нельзя открыть для редактирования.");
                    prepareForm(req);
                    req.setAttribute("capsule", capsule);
                    req.setAttribute("selectedTagIds", tagIdsValue(capsule));
                    req.getRequestDispatcher("/WEB-INF/views/capsule-form.jsp").forward(req, resp);
                } else {
                    req.setAttribute("capsule", capsule);
                    req.getRequestDispatcher("/WEB-INF/views/capsule-view.jsp").forward(req, resp);
                }
            } else {
                req.setAttribute("capsules", capsules.getAll());
                req.setAttribute("currentUserId", WebUtil.userId(req));
                req.setAttribute("isAdmin", WebUtil.isAdmin(req));
                req.getRequestDispatcher("/WEB-INF/views/capsules.jsp").forward(req, resp);
            }
        } catch (SecurityException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось загрузить капсулы. Проверь базу данных.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        try {
            if ("/new".equals(path)) {
                boolean created = capsules.create(WebUtil.userId(req), requiredRequestKey(req.getParameter("requestKey")),
                        req.getParameter("title"), req.getParameter("note"),
                        req.getParameter("unlockAt"), req.getParameter("categoryId"),
                        req.getParameterValues("tagIds"), readImage(req.getPart("photo")));
                resp.sendRedirect(req.getContextPath() + (created ? "/capsules?created=1" : "/capsules?duplicate=1"));
            } else if ("/edit".equals(path)) {
                capsules.update(WebUtil.requiredId(req.getParameter("id")), WebUtil.userId(req), WebUtil.isAdmin(req),
                        req.getParameter("title"), req.getParameter("note"), req.getParameter("unlockAt"),
                        req.getParameter("categoryId"), req.getParameterValues("tagIds"), readImage(req.getPart("photo")));
                resp.sendRedirect(req.getContextPath() + "/capsules?updated=1");
            } else if ("/delete".equals(path)) {
                capsules.delete(WebUtil.requiredId(req.getParameter("id")), WebUtil.userId(req), WebUtil.isAdmin(req));
                resp.sendRedirect(req.getContextPath() + "/capsules?deleted=1");
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            try {
                prepareForm(req);
                if ("/new".equals(path)) req.setAttribute("requestKey", retryRequestKey(req.getParameter("requestKey")));
                if ("/edit".equals(path)) {
                    Capsule existing = capsules.get(WebUtil.requiredId(req.getParameter("id")));
                    req.setAttribute("capsule", existing);
                    req.setAttribute("selectedTagIds", tagIdsValue(existing));
                }
                req.getRequestDispatcher("/WEB-INF/views/capsule-form.jsp").forward(req, resp);
            } catch (SQLException lookupError) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось повторно загрузить форму.");
            }
        } catch (SecurityException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (SQLException | IOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось сохранить капсулу. Проверь базу и папку для файлов.");
        }
    }

    private void prepareForm(HttpServletRequest req) throws SQLException {
        req.setAttribute("categories", admin.getCategories());
        req.setAttribute("tags", admin.getTags());
    }

    private String tagIdsValue(Capsule capsule) {
        StringBuilder value = new StringBuilder(",");
        for (var tag : capsule.getTags()) value.append(tag.getId()).append(',');
        return value.toString();
    }

    private UUID requiredRequestKey(String value) {
        try { return UUID.fromString(value); }
        catch (Exception e) { throw new IllegalArgumentException("Обнови форму и отправь её ещё раз."); }
    }

    private String retryRequestKey(String value) {
        try { return UUID.fromString(value).toString(); }
        catch (Exception e) { return UUID.randomUUID().toString(); }
    }

    private UploadedImage readImage(Part part) throws IOException {
        if (part == null || part.getSize() == 0) return null;
        if (part.getSize() > 5L * 1024L * 1024L) throw new IllegalArgumentException("Выбери изображение размером до 5 МБ.");
        try (InputStream input = part.getInputStream()) {
            return new UploadedImage(input.readAllBytes(), part.getSubmittedFileName(), part.getContentType());
        }
    }

    private void checkOwner(Capsule capsule, HttpServletRequest req) {
        if (!WebUtil.isAdmin(req) && capsule.getOwnerId() != WebUtil.userId(req))
            throw new SecurityException("Можно управлять только своими капсулами.");
    }
}
