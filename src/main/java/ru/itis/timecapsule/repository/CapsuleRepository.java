package ru.itis.timecapsule.repository;

import ru.itis.timecapsule.model.Capsule;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CapsuleRepository {
    List<Capsule> findAll() throws SQLException;
    Optional<Capsule> findById(long id) throws SQLException;
    Optional<Capsule> findByMediaFileId(long mediaFileId) throws SQLException;
    boolean existsByOwnerAndRequestKey(long ownerId, UUID requestKey) throws SQLException;
    boolean save(Capsule capsule, List<Long> tagIds, UUID requestKey) throws SQLException;
    void update(Capsule capsule, List<Long> tagIds) throws SQLException;
    List<Long> findTagIds(long capsuleId) throws SQLException;
    void delete(long id) throws SQLException;
}
