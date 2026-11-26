package ru.itis.timecapsule.model;

public class MediaFile {
    private long id;
    private String storageKey;
    private String originalName;
    private String contentType;
    private long sizeBytes;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
}
