package ru.itis.timecapsule.repository;

import ru.itis.timecapsule.model.Category;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll() throws SQLException;
    Optional<Category> findById(long id) throws SQLException;
    Category save(Category category) throws SQLException;
    void update(Category category) throws SQLException;
    void delete(long id) throws SQLException;
}
