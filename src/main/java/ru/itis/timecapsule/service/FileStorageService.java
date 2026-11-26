package ru.itis.timecapsule.service;

import ru.itis.timecapsule.model.MediaFile;
import ru.itis.timecapsule.dto.UploadedImage;
import ru.itis.timecapsule.repository.MediaFileRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class FileStorageService {
    private static final long MAX_BYTES = 5L * 1024L * 1024L;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/gif", ".gif");
    private final MediaFileRepository repository;
    private final Path directory;

    public FileStorageService(MediaFileRepository repository) {
        this.repository = repository;
        String configured = System.getProperty("timecapsule.uploadDir");
        this.directory = (configured == null || configured.isBlank()
                ? Path.of(System.getProperty("java.io.tmpdir"), "time-capsule-uploads")
                : Path.of(configured)).toAbsolutePath().normalize();
        try { Files.createDirectories(directory); }
        catch (IOException e) { throw new IllegalStateException("Не удалось создать папку для изображений.", e); }
    }

    public MediaFile save(UploadedImage image) throws IOException, SQLException {
        if (image == null || image.getSize() <= 0 || image.getSize() > MAX_BYTES)
            throw new IllegalArgumentException("Выбери изображение размером до 5 МБ.");
        String contentType = image.getContentType() == null ? "" : image.getContentType().toLowerCase(Locale.ROOT);
        String extension = EXTENSIONS.get(contentType);
        if (extension == null) throw new IllegalArgumentException("Разрешены JPG, PNG и GIF.");
        try (InputStream check = new ByteArrayInputStream(image.getContent())) {
            BufferedImage decoded = ImageIO.read(check);
            if (decoded == null) throw new IllegalArgumentException("Файл не является корректным изображением.");
        }

        String key = UUID.randomUUID() + extension;
        Path destination = directory.resolve(key).normalize();
        if (!destination.getParent().equals(directory)) throw new IllegalArgumentException("Недопустимый файл.");
        Files.write(destination, image.getContent(), StandardOpenOption.CREATE_NEW);

        MediaFile file = new MediaFile();
        file.setStorageKey(key);
        String original = image.getOriginalName();
        if (original == null || original.isBlank()) original = "image" + extension;
        String submitted = Path.of(original.replace('\\', '/')).getFileName().toString();
        file.setOriginalName(submitted.length() > 255 ? submitted.substring(submitted.length() - 255) : submitted);
        file.setContentType(contentType);
        file.setSizeBytes(image.getSize());
        try {
            return repository.save(file);
        } catch (SQLException e) {
            Files.deleteIfExists(destination);
            throw e;
        }
    }

    public MediaFile get(long id) throws SQLException {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Изображение не найдено."));
    }

    public InputStream open(MediaFile file) throws IOException {
        Path path = directory.resolve(file.getStorageKey()).normalize();
        if (!path.getParent().equals(directory)) throw new IllegalArgumentException("Недопустимый путь к изображению.");
        return Files.newInputStream(path);
    }

    public void delete(MediaFile file) throws SQLException, IOException {
        Path path = directory.resolve(file.getStorageKey()).normalize();
        Files.deleteIfExists(path);
        repository.delete(file.getId());
    }
}
