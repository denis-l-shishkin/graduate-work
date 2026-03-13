package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.request.NewPassword;
import ru.skypro.homework.dto.request.UpdateUser;
import ru.skypro.homework.dto.response.User;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${upload.avatars.path}")
    private String avatarsPath;

    @Override
    public UserEntity getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден: " + email));
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден: " + email));
    }

    @Override
    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public boolean isAdOwner(Authentication authentication, Integer adId) {
        if (isAdmin(authentication)) {
            log.info("Администратор имеет право на редактирование объявления {}", adId);
            return true;
        }

        UserEntity currentUser = getCurrentUser(authentication);

        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено: " + adId));

        boolean isOwner = ad.getAuthor().getId().equals(currentUser.getId());

        if (isOwner) {
            log.info("Пользователь {} является владельцем объявления {}", currentUser.getEmail(), adId);
        } else {
            log.warn("Пользователь {} НЕ является владельцем объявления {}", currentUser.getEmail(), adId);
        }

        return isOwner;
    }

    @Override
    public boolean isCommentOwner(Authentication authentication, Integer commentId) {
        if (isAdmin(authentication)) {
            log.info("Администратор имеет право на редактирование комментария {}", commentId);
            return true;
        }

        UserEntity currentUser = getCurrentUser(authentication);

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Комментарий не найден: " + commentId));

        boolean isOwner = comment.getAuthor().getId().equals(currentUser.getId());

        if (isOwner) {
            log.info("Пользователь {} является владельцем комментария {}", currentUser.getEmail(), commentId);
        } else {
            log.warn("Пользователь {} НЕ является владельцем комментария {}", currentUser.getEmail(), commentId);
        }

        return isOwner;
    }

    public User getUserInfo(Authentication authentication) {
        log.info("Получение информации о пользователе: {}", authentication.getName());

        UserEntity user = getCurrentUser(authentication);
        return userMapper.toDto(user);
    }

    public UpdateUser updateUserInfo(UpdateUser updateUser, Authentication authentication) {
        log.info("Обновление информации пользователя: {}", authentication.getName());

        UserEntity user = getCurrentUser(authentication);
        userMapper.updateEntity(updateUser, user);
        userRepository.save(user);

        return updateUser;
    }

    public void updatePassword(NewPassword newPassword, Authentication authentication) {
        log.info("Обновление пароля пользователя: {}", authentication.getName());

        UserEntity user = getCurrentUser(authentication);

        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            log.warn("Неверный текущий пароль для пользователя {}", authentication.getName());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный текущий пароль");
        }

        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(user);

        log.info("Пароль пользователя {} успешно обновлен", authentication.getName());
    }

    public void updateAvatar(MultipartFile image, Authentication authentication) {
        log.info("Обновление аватара пользователя: {}", authentication.getName());

        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Файл не может быть пустым");
        }

        UserEntity user = getCurrentUser(authentication);

        if (user.getImagePath() != null) {
            deleteAvatarFile(user.getImagePath());
        }

        String avatarUrl = saveAvatarFile(user.getId(), image);
        user.setImagePath(avatarUrl);
        userRepository.save(user);

        log.info("Аватар пользователя {} обновлен", authentication.getName());
    }

    private String saveAvatarFile(Integer userId, MultipartFile image) {
        try {
            Path uploadDir = Paths.get(avatarsPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("Создана директория для аватаров: {}", uploadDir.toAbsolutePath());
            }

            if (image.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Файл не может быть пустым");
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Можно загружать только изображения");
            }

            String extension = getFileExtension(image.getOriginalFilename());
            String fileName = userId + "_" + UUID.randomUUID() + extension;
            Path filePath = uploadDir.resolve(fileName);

            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Аватар сохранен: {}", filePath.toAbsolutePath());

            return "/avatars/" + fileName;

        } catch (IOException e) {
            log.error("Ошибка при сохранении аватара", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при сохранении аватара", e);
        }
    }

    private void deleteAvatarFile(String avatarUrl) {
        try {
            String fileName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(avatarsPath, fileName);

            Files.deleteIfExists(filePath);
            log.info("Аватар удален: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при удалении аватара: {}", avatarUrl, e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}