package ru.itis.timecapsule.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class WebUtil {
    private WebUtil() { }
    public static long userId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("userId");
        return value instanceof Long ? (Long) value : -1;
    }
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && "ADMIN".equals(session.getAttribute("role"));
    }
    public static long requiredId(String value) {
        try { long id = Long.parseLong(value); if (id > 0) return id; }
        catch (Exception ignored) { }
        throw new IllegalArgumentException("Некорректный идентификатор.");
    }
}
