package ru.itis.timecapsule.web.servlet;

import ru.itis.timecapsule.model.Capsule;
import ru.itis.timecapsule.model.MediaFile;
import ru.itis.timecapsule.service.CapsuleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/media")
public class MediaServlet extends HttpServlet {
    private CapsuleService capsules;
    @Override public void init() { capsules = (CapsuleService) getServletContext().getAttribute("capsuleService"); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long mediaId = Long.parseLong(req.getParameter("id"));
            Optional<Capsule> capsule = capsules.findUnlockedByMediaId(mediaId);
            if (capsule.isEmpty()) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            MediaFile file = capsules.getMedia(mediaId);
            resp.setContentType(file.getContentType());
            resp.setContentLengthLong(file.getSizeBytes());
            String name = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8).replace("+", "%20");
            resp.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + name);
            try (InputStream input = ((ru.itis.timecapsule.service.FileStorageService)
                    getServletContext().getAttribute("fileStorageService")).open(file)) {
                input.transferTo(resp.getOutputStream());
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
