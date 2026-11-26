package ru.itis.timecapsule.repository.jdbc;

import ru.itis.timecapsule.config.Database;
import ru.itis.timecapsule.model.Tag;
import ru.itis.timecapsule.repository.TagRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTagRepository implements TagRepository {
    private final Database database;

    public JdbcTagRepository(Database database) { this.database = database; }

    @Override
    public List<Tag> findAll() throws SQLException {
        List<Tag> result = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM tags ORDER BY name");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) result.add(map(rs));
        }
        return result;
    }

    @Override
    public Optional<Tag> findById(long id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM tags WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    private Tag map(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("name"));
        return tag;
    }
}
