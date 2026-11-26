package ru.itis.timecapsule.config;

import ru.itis.timecapsule.repository.*;
import ru.itis.timecapsule.repository.jdbc.*;
import ru.itis.timecapsule.service.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        Database database = new Database(
                setting(context, "dbUrl", "timecapsule.db.url", "DB_URL", "jdbc:postgresql://localhost:5432/time_capsule"),
                setting(context, "dbUser", "timecapsule.db.user", "DB_USER", "postgres"),
                setting(context, "dbPassword", "timecapsule.db.password", "DB_PASSWORD", ""));

        UserRepository users = new JdbcUserRepository(database);
        CategoryRepository categories = new JdbcCategoryRepository(database);
        TagRepository tags = new JdbcTagRepository(database);
        MediaFileRepository mediaFiles = new JdbcMediaFileRepository(database);
        CapsuleRepository capsules = new JdbcCapsuleRepository(database);

        FileStorageService fileStorage = new FileStorageService(mediaFiles);
        AuthService authService = new AuthService(users);
        CapsuleService capsuleService = new CapsuleService(capsules, users, categories, tags, fileStorage);
        AdminService adminService = new AdminService(categories, tags);

        context.setAttribute("authService", authService);
        context.setAttribute("capsuleService", capsuleService);
        context.setAttribute("adminService", adminService);
        context.setAttribute("fileStorageService", fileStorage);
    }

    private String setting(ServletContext context, String parameter, String property, String environment, String fallback) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) value = System.getenv(environment);
        if (value == null || value.isBlank()) value = context.getInitParameter(parameter);
        return value == null || value.isBlank() ? fallback : value;
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // The application does not keep open JDBC connections between requests.
    }
}
