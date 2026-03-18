package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Value("${upload.avatars.path}")
    private String avatarsPath;

    @Value("${upload.ads.path}")
    private String adsPath;

    public String saveImage(MultipartFile file, String type, Integer entityId) {
        validateImage(file);

        String basePath = getBasePath(type);
        String fileName = generateFileName(entityId, file.getOriginalFilename());

        try {
            Path uploadDir = Paths.get(basePath);
            createDirectoryIfNotExists(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath);

            log.info("Изображение сохранено: {} для сущности типа {} с id {}",
                    filePath.toAbsolutePath(), type, entityId);

            return fileName;

        } catch (IOException e) {
            log.error("Ошибка при сохранении изображения для {} с id {}", type, entityId, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при сохранении изображения", e);
        }
    }

    public byte[] getImage(String fileName, String type) {
        if (fileName == null || fileName.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Изображение не найдено");
        }

        String basePath = getBasePath(type);

        try {
            Path filePath = Paths.get(basePath, fileName);

            if (!Files.exists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл изображения не найден");
            }

            return Files.readAllBytes(filePath);

        } catch (IOException e) {
            log.error("Ошибка при чтении изображения: {}", fileName, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при чтении изображения", e);
        }
    }

    public void deleteImage(String fileName, String type) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        String basePath = getBasePath(type);

        try {
            Path filePath = Paths.get(basePath, fileName);
            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                log.info("Изображение удалено: {}", filePath.toAbsolutePath());
            } else {
                log.warn("Изображение не найдено для удаления: {}", filePath.toAbsolutePath());
            }

        } catch (IOException e) {
            log.error("Ошибка при удалении изображения: {}", fileName, e);
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Файл не может быть пустым");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Можно загружать только изображения");
        }

        // Проверка размера (уже настроено в properties)
    }

    private String getBasePath(String type) {
        switch (type) {
            case "avatar":
                return avatarsPath;
            case "ad":
                return adsPath;
            default:
                throw new IllegalArgumentException("Неизвестный тип изображения: " + type);
        }
    }

    private String generateFileName(Integer entityId, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return entityId + "_" + UUID.randomUUID() + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private void createDirectoryIfNotExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            log.info("Создана директория: {}", directory.toAbsolutePath());
        }
    }
}