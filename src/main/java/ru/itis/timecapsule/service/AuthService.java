package ru.itis.timecapsule.service;

import ru.itis.timecapsule.model.Role;
import ru.itis.timecapsule.model.User;
import ru.itis.timecapsule.repository.UserRepository;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuthService {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserRepository users;

    public AuthService(UserRepository users) { this.users = users; }

    public User register(String email, String displayName, String password) throws SQLException {
        email = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        displayName = displayName == null ? "" : displayName.trim();
        if (!EMAIL.matcher(email).matches() || email.length() > 255) throw new IllegalArgumentException("Укажи корректный email.");
        if (displayName.length() < 2 || displayName.length() > 80) throw new IllegalArgumentException("Имя должно быть от 2 до 80 символов.");
        if (password == null || password.length() < 8 || password.length() > 100) throw new IllegalArgumentException("Пароль должен содержать от 8 до 100 символов.");
        if (users.findByEmail(email).isPresent()) throw new IllegalArgumentException("Этот email уже зарегистрирован.");

        User user = new User();
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(Role.USER);
        return users.save(user);
    }

    public Optional<User> login(String email, String password) throws SQLException {
        if (email == null || password == null) return Optional.empty();
        return users.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .filter(user -> PasswordUtil.matches(password, user.getPasswordHash()));
    }

    public Optional<User> findById(long id) throws SQLException { return users.findById(id); }
}
