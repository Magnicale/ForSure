package ru.itis.timecapsule.repository;

import ru.itis.timecapsule.model.MediaFile;

import java.sql.SQLException;
import java.util.Optional;

public interface MediaFileRepository {
    MediaFile save(MediaFile file) throws SQLException;
    Optional<MediaFile> findById(long id) throws SQLException;
    void delete(long id) throws SQLException;
}
