package ru.itis.timecapsule.repository.jdbc;

import ru.itis.timecapsule.config.Database;
import ru.itis.timecapsule.model.Category;
import ru.itis.timecapsule.repository.CategoryRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCategoryRepository implements CategoryRepository {
    private final Database database;

    public JdbcCategoryRepository(Database database) { this.database = database; }

    @Override
    public List<Category> findAll() throws SQLException {
        List<Category> result = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM categories ORDER BY name");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) result.add(map(rs));
        }
        return result;
    }

    @Override
    public Optional<Category> findById(long id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM categories WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Category save(Category category) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO categories(name) VALUES (?) RETURNING id")) {
            statement.setString(1, category.getName());
            try (ResultSet rs = statement.executeQuery()) { rs.next(); category.setId(rs.getLong(1)); }
        }
        return category;
    }

    @Override
    public void update(Category category) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE categories SET name = ? WHERE id = ?")) {
            statement.setString(1, category.getName());
            statement.setLong(2, category.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM categories WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private Category map(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        return category;
    }
}
