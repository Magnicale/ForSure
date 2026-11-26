package ru.itis.timecapsule.dto;

public class UploadedImage {
    private final byte[] content;
    private final String originalName;
    private final String contentType;

    public UploadedImage(byte[] content, String originalName, String contentType) {
        this.content = content;
        this.originalName = originalName;
        this.contentType = contentType;
    }

    public byte[] getContent() { return content; }
    public String getOriginalName() { return originalName; }
    public String getContentType() { return contentType; }
    public long getSize() { return content == null ? 0 : content.length; }
}
