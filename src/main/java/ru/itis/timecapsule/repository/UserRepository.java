package ru.itis.timecapsule.repository;

import ru.itis.timecapsule.model.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long id) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    User save(User user) throws SQLException;
}
