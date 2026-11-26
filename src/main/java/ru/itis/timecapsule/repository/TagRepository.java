package ru.itis.timecapsule.repository;

import ru.itis.timecapsule.model.Tag;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TagRepository {
    List<Tag> findAll() throws SQLException;
    Optional<Tag> findById(long id) throws SQLException;
}
