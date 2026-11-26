package ru.itis.timecapsule.repository.jdbc;

import ru.itis.timecapsule.config.Database;
import ru.itis.timecapsule.model.MediaFile;
import ru.itis.timecapsule.repository.MediaFileRepository;

import java.sql.*;
import java.util.Optional;

public class JdbcMediaFileRepository implements MediaFileRepository {
    private final Database database;

    public JdbcMediaFileRepository(Database database) { this.database = database; }

    @Override
    public MediaFile save(MediaFile file) throws SQLException {
        String sql = "INSERT INTO media_files(storage_key, original_name, content_type, size_bytes) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, file.getStorageKey());
            statement.setString(2, file.getOriginalName());
            statement.setString(3, file.getContentType());
            statement.setLong(4, file.getSizeBytes());
            try (ResultSet rs = statement.executeQuery()) { rs.next(); file.setId(rs.getLong(1)); }
        }
        return file;
    }

    @Override
    public Optional<MediaFile> findById(long id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM media_files WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM media_files WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private MediaFile map(ResultSet rs) throws SQLException {
        MediaFile file = new MediaFile();
        file.setId(rs.getLong("id"));
        file.setStorageKey(rs.getString("storage_key"));
        file.setOriginalName(rs.getString("original_name"));
        file.setContentType(rs.getString("content_type"));
        file.setSizeBytes(rs.getLong("size_bytes"));
        return file;
    }
}
