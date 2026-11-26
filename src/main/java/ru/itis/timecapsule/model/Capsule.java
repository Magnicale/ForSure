package ru.itis.timecapsule.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Capsule {
    private long id;
    private long ownerId;
    private long categoryId;
    private long mediaFileId;
    private String ownerName;
    private String categoryName;
    private String title;
    private String note;
    private Instant unlockAt;
    private Instant createdAt;
    private List<Tag> tags = new ArrayList<>();
    private boolean unlocked;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getOwnerId() { return ownerId; }
    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public long getMediaFileId() { return mediaFileId; }
    public void setMediaFileId(long mediaFileId) { this.mediaFileId = mediaFileId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Instant getUnlockAt() { return unlockAt; }
    public void setUnlockAt(Instant unlockAt) { this.unlockAt = unlockAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags == null ? new ArrayList<>() : tags; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    public long getUnlockAtMillis() { return unlockAt == null ? 0 : unlockAt.toEpochMilli(); }

    public String getUnlockAtLocal() {
        if (unlockAt == null) return "";
        return LocalDateTime.ofInstant(unlockAt, ZoneId.systemDefault()).toString();
    }
}
