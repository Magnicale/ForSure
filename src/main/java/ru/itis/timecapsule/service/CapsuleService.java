package ru.itis.timecapsule.service;

import ru.itis.timecapsule.model.*;
import ru.itis.timecapsule.repository.*;
import ru.itis.timecapsule.dto.UploadedImage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class CapsuleService {
    private final CapsuleRepository capsules;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final TagRepository tags;
    private final FileStorageService files;

    public CapsuleService(CapsuleRepository capsules, UserRepository users, CategoryRepository categories,
                          TagRepository tags, FileStorageService files) {
        this.capsules = capsules;
        this.users = users;
        this.categories = categories;
        this.tags = tags;
        this.files = files;
    }

    public List<Capsule> getAll() throws SQLException {
        List<Capsule> result = capsules.findAll();
        for (Capsule capsule : result) enrich(capsule);
        return result;
    }

    public Capsule get(long id) throws SQLException {
        Capsule capsule = capsules.findById(id).orElseThrow(() -> new IllegalArgumentException("Капсула не найдена."));
        enrich(capsule);
        return capsule;
    }

    public boolean create(long ownerId, UUID requestKey, String title, String note, String unlockAt, String categoryId,
                          String[] tagValues, UploadedImage photo) throws SQLException, IOException {
        User owner = users.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден."));
        if (requestKey == null) throw new IllegalArgumentException("Обнови форму и отправь её ещё раз.");
        if (capsules.existsByOwnerAndRequestKey(owner.getId(), requestKey)) return false;
        Capsule capsule = validated(title, note, unlockAt, categoryId, true);
        capsule.setOwnerId(owner.getId());
        MediaFile media = files.save(photo);
        capsule.setMediaFileId(media.getId());
        boolean saved;
        try {
            saved = capsules.save(capsule, parseTagIds(tagValues), requestKey);
        } catch (SQLException | RuntimeException e) {
            try { files.delete(media); } catch (Exception cleanup) { e.addSuppressed(cleanup); }
            throw e;
        }
        if (!saved) {
            files.delete(media);
            return false;
        }
        enrich(capsule);
        return true;
    }

    public void update(long capsuleId, long userId, boolean admin, String title, String note, String unlockAt,
                       String categoryId, String[] tagValues, UploadedImage photo) throws SQLException, IOException {
        Capsule capsule = get(capsuleId);
        requireOwner(capsule, userId, admin);
        if (!capsule.isUnlocked()) throw new SecurityException("Запечатанную капсулу нельзя открыть для редактирования.");
        Capsule updated = validated(title, note, unlockAt, categoryId, false);
        updated.setId(capsuleId);
        updated.setOwnerId(capsule.getOwnerId());
        MediaFile newFile = null;
        if (photo != null && photo.getSize() > 0) {
            newFile = files.save(photo);
            updated.setMediaFileId(newFile.getId());
        } else {
            updated.setMediaFileId(capsule.getMediaFileId());
        }
        try {
            capsules.update(updated, parseTagIds(tagValues));
        } catch (SQLException | RuntimeException e) {
            if (newFile != null) try { files.delete(newFile); } catch (Exception cleanup) { e.addSuppressed(cleanup); }
            throw e;
        }
        if (newFile != null) {
            try { files.delete(files.get(capsule.getMediaFileId())); } catch (Exception ignored) { }
        }
    }

    public void delete(long capsuleId, long userId, boolean admin) throws SQLException, IOException {
        Capsule capsule = get(capsuleId);
        requireOwner(capsule, userId, admin);
        MediaFile media = files.get(capsule.getMediaFileId());
        capsules.delete(capsuleId);
        files.delete(media);
    }

    public Optional<Capsule> findUnlockedByMediaId(long mediaId) throws SQLException {
        Optional<Capsule> found = capsules.findByMediaFileId(mediaId);
        if (found.isEmpty()) return Optional.empty();
        Capsule capsule = found.get();
        enrich(capsule);
        return capsule.isUnlocked() ? Optional.of(capsule) : Optional.empty();
    }

    public MediaFile getMedia(long id) throws SQLException { return files.get(id); }

    private Capsule validated(String title, String note, String unlockAt, String categoryId, boolean requireFuture) throws SQLException {
        String cleanTitle = title == null ? "" : title.trim();
        String cleanNote = note == null ? "" : note.trim();
        if (cleanTitle.isEmpty() || cleanTitle.length() > 120) throw new IllegalArgumentException("Заголовок должен быть от 1 до 120 символов.");
        if (cleanNote.isEmpty() || cleanNote.length() > 4000) throw new IllegalArgumentException("Заметка должна быть от 1 до 4000 символов.");
        Instant opening;
        try { opening = LocalDateTime.parse(unlockAt).atZone(ZoneId.systemDefault()).toInstant(); }
        catch (Exception e) { throw new IllegalArgumentException("Укажи корректную дату открытия."); }
        if (requireFuture && !opening.isAfter(Instant.now())) throw new IllegalArgumentException("Дата открытия должна быть в будущем.");
        long category;
        try { category = Long.parseLong(categoryId); }
        catch (Exception e) { throw new IllegalArgumentException("Выбери категорию."); }
        if (categories.findById(category).isEmpty()) throw new IllegalArgumentException("Категория не найдена.");
        Capsule result = new Capsule();
        result.setTitle(cleanTitle);
        result.setNote(cleanNote);
        result.setUnlockAt(opening);
        result.setCategoryId(category);
        return result;
    }

    private List<Long> parseTagIds(String[] values) throws SQLException {
        if (values == null || values.length == 0) return List.of();
        Set<Long> ids = new LinkedHashSet<>();
        for (String value : values) {
            try { ids.add(Long.parseLong(value)); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Некорректный тег."); }
        }
        Set<Long> valid = new HashSet<>();
        for (Tag tag : tags.findAll()) valid.add(tag.getId());
        if (!valid.containsAll(ids)) throw new IllegalArgumentException("Выбранный тег не найден.");
        return new ArrayList<>(ids);
    }

    private void enrich(Capsule capsule) throws SQLException {
        capsule.setUnlocked(!capsule.getUnlockAt().isAfter(Instant.now()));
        List<Tag> capsuleTags = new ArrayList<>();
        for (Long id : capsules.findTagIds(capsule.getId())) tags.findById(id).ifPresent(capsuleTags::add);
        capsule.setTags(capsuleTags);
    }

    private void requireOwner(Capsule capsule, long userId, boolean admin) {
        if (!admin && capsule.getOwnerId() != userId) throw new SecurityException("Нет доступа к этой капсуле.");
    }
}
