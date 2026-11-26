package ru.itis.timecapsule.repository.jdbc;

import ru.itis.timecapsule.config.Database;
import ru.itis.timecapsule.model.Role;
import ru.itis.timecapsule.model.User;
import ru.itis.timecapsule.repository.UserRepository;

import java.sql.*;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {
    private final Database database;

    public JdbcUserRepository(Database database) { this.database = database; }

    @Override
    public Optional<User> findById(long id) throws SQLException {
        String sql = "SELECT id, email, display_name, password_hash, role FROM users WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT id, email, display_name, password_hash, role FROM users WHERE lower(email) = lower(?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO users(email, display_name, password_hash, role) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getDisplayName());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                user.setId(rs.getLong(1));
                return user;
            }
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setDisplayName(rs.getString("display_name"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.valueOf(rs.getString("role")));
        return user;
    }
}
