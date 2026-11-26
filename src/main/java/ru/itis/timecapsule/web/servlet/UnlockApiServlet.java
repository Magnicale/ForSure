package ru.itis.timecapsule.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.itis.timecapsule.model.Capsule;
import ru.itis.timecapsule.service.CapsuleService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/api/unlock")
public class UnlockApiServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private CapsuleService capsules;
    @Override public void init() { capsules = (CapsuleService) getServletContext().getAttribute("capsuleService"); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Capsule capsule = capsules.get(Long.parseLong(req.getParameter("id")));
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("id", capsule.getId());
            result.put("unlocked", capsule.isUnlocked());
            if (capsule.isUnlocked()) {
                result.put("note", capsule.getNote());
                result.put("mediaUrl", req.getContextPath() + "/media?id=" + capsule.getMediaFileId());
                result.put("tags", capsule.getTags().stream().map(tag -> tag.getName()).toList());
            }
            mapper.writeValue(resp.getOutputStream(), result);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getOutputStream(), Map.of("error", "Капсула не найдена."));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getOutputStream(), Map.of("error", "Ошибка базы данных."));
        }
    }
}
