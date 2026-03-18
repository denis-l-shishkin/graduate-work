package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.utils.SecurityUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    private final ImageService imageService; // Добавляем ImageService

    @Override
    public UserEntity getCurrentUser(Authentication authentication) {
        return securityUtils.getCurrentUser(authentication);
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден: " + email));
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
            imageService.deleteImage(user.getImagePath(), "avatar");
        }

        String fileName = imageService.saveImage(image, "avatar", user.getId());
        user.setImagePath(fileName);
        userRepository.save(user);

        log.info("Аватар пользователя {} обновлен", authentication.getName());
    }

    public byte[] getAvatar(Integer userId) {
        log.info("Получение аватара пользователя с id: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден: " + userId));

        if (user.getImagePath() == null || user.getImagePath().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Аватар не найден");
        }

        return imageService.getImage(user.getImagePath(), "avatar");
    }
}