package ru.itis.timecapsule.repository.jdbc;

import ru.itis.timecapsule.config.Database;
import ru.itis.timecapsule.model.Capsule;
import ru.itis.timecapsule.repository.CapsuleRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcCapsuleRepository implements CapsuleRepository {
    private static final String SELECT_BASE = "SELECT c.id, c.owner_id, c.category_id, c.media_file_id, c.title, c.note, c.unlock_at, c.created_at, u.display_name AS owner_name, cat.name AS category_name " +
            "FROM capsules c JOIN users u ON u.id = c.owner_id JOIN categories cat ON cat.id = c.category_id ";
    private final Database database;

    public JdbcCapsuleRepository(Database database) { this.database = database; }

    @Override
    public List<Capsule> findAll() throws SQLException {
        List<Capsule> result = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BASE + "ORDER BY c.created_at DESC");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) result.add(map(rs));
        }
        return result;
    }

    @Override
    public Optional<Capsule> findById(long id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BASE + "WHERE c.id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    @Override
    public Optional<Capsule> findByMediaFileId(long mediaFileId) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BASE + "WHERE c.media_file_id = ?")) {
            statement.setLong(1, mediaFileId);
            try (ResultSet rs = statement.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    @Override
    public boolean existsByOwnerAndRequestKey(long ownerId, UUID requestKey) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT 1 FROM capsules WHERE owner_id = ? AND request_key = ?")) {
            statement.setLong(1, ownerId);
            statement.setObject(2, requestKey);
            try (ResultSet rs = statement.executeQuery()) { return rs.next(); }
        }
    }

    @Override
    public boolean save(Capsule capsule, List<Long> tagIds, UUID requestKey) throws SQLException {
        String sql = "INSERT INTO capsules(owner_id, category_id, media_file_id, request_key, title, note, unlock_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (owner_id, request_key) DO NOTHING RETURNING id";
        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setLong(1, capsule.getOwnerId());
                    statement.setLong(2, capsule.getCategoryId());
                    statement.setLong(3, capsule.getMediaFileId());
                    statement.setObject(4, requestKey);
                    statement.setString(5, capsule.getTitle());
                    statement.setString(6, capsule.getNote());
                    statement.setTimestamp(7, Timestamp.from(capsule.getUnlockAt()));
                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next()) {
                            connection.commit();
                            return false;
                        }
                        capsule.setId(rs.getLong(1));
                    }
                }
                replaceTags(connection, capsule.getId(), tagIds);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
        return true;
    }

    @Override
    public void update(Capsule capsule, List<Long> tagIds) throws SQLException {
        String sql = "UPDATE capsules SET category_id = ?, media_file_id = ?, title = ?, note = ?, unlock_at = ? WHERE id = ?";
        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setLong(1, capsule.getCategoryId());
                    statement.setLong(2, capsule.getMediaFileId());
                    statement.setString(3, capsule.getTitle());
                    statement.setString(4, capsule.getNote());
                    statement.setTimestamp(5, Timestamp.from(capsule.getUnlockAt()));
                    statement.setLong(6, capsule.getId());
                    statement.executeUpdate();
                }
                replaceTags(connection, capsule.getId(), tagIds);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Long> findTagIds(long capsuleId) throws SQLException {
        List<Long> ids = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT tag_id FROM capsule_tags WHERE capsule_id = ? ORDER BY tag_id")) {
            statement.setLong(1, capsuleId);
            try (ResultSet rs = statement.executeQuery()) { while (rs.next()) ids.add(rs.getLong(1)); }
        }
        return ids;
    }

    @Override
    public void delete(long id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM capsules WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private void replaceTags(Connection connection, long capsuleId, List<Long> tagIds) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM capsule_tags WHERE capsule_id = ?")) {
            delete.setLong(1, capsuleId);
            delete.executeUpdate();
        }
        try (PreparedStatement insert = connection.prepareStatement("INSERT INTO capsule_tags(capsule_id, tag_id) VALUES (?, ?)")) {
            for (Long tagId : tagIds) {
                insert.setLong(1, capsuleId);
                insert.setLong(2, tagId);
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private Capsule map(ResultSet rs) throws SQLException {
        Capsule capsule = new Capsule();
        capsule.setId(rs.getLong("id"));
        capsule.setOwnerId(rs.getLong("owner_id"));
        capsule.setCategoryId(rs.getLong("category_id"));
        capsule.setMediaFileId(rs.getLong("media_file_id"));
        capsule.setTitle(rs.getString("title"));
        capsule.setNote(rs.getString("note"));
        capsule.setUnlockAt(rs.getTimestamp("unlock_at").toInstant());
        capsule.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        capsule.setOwnerName(rs.getString("owner_name"));
        capsule.setCategoryName(rs.getString("category_name"));
        return capsule;
    }
}
