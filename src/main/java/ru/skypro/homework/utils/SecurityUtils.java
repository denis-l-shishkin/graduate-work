package ru.skypro.homework.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

@Component("securityUtils")
@RequiredArgsConstructor
@Slf4j
public class SecurityUtils {

    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final CommentRepository commentRepository;

    public boolean isAdmin(Authentication authentication) {
        if (authentication == null) return false;

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public UserEntity getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не авторизован");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Пользователь не найден: " + email));
    }

    public boolean isAdOwnerOrAdmin(Authentication authentication, Integer adId) {
        if (authentication == null || adId == null) {
            log.warn("Проверка прав доступа: authentication или adId равен null");
            return false;
        }

        if (isAdmin(authentication)) {
            log.info("Администратор имеет доступ к объявлению {}", adId);
            return true;
        }

        try {
            UserEntity user = getCurrentUser(authentication);

            AdEntity ad = adRepository.findById(adId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Объявление не найдено: " + adId));

            boolean isOwner = ad.getAuthor().getId().equals(user.getId());

            if (isOwner) {
                log.info("Пользователь {} является владельцем объявления {}", user.getEmail(), adId);
            } else {
                log.warn("Пользователь {} НЕ является владельцем объявления {}", user.getEmail(), adId);
            }

            return isOwner;

        } catch (ResponseStatusException e) {
            log.error("Ошибка при проверке прав на объявление: {}", e.getMessage());
            return false;
        }
    }

    public boolean isCommentOwnerOrAdmin(Authentication authentication, Integer commentId) {
        if (authentication == null || commentId == null) {
            log.warn("Проверка прав доступа: authentication или commentId равен null");
            return false;
        }

        if (isAdmin(authentication)) {
            log.info("Администратор имеет доступ к комментарию {}", commentId);
            return true;
        }

        try {
            UserEntity user = getCurrentUser(authentication);

            CommentEntity comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Комментарий не найден: " + commentId));

            boolean isOwner = comment.getAuthor().getId().equals(user.getId());

            if (isOwner) {
                log.info("Пользователь {} является владельцем комментария {}", user.getEmail(), commentId);
            } else {
                log.warn("Пользователь {} НЕ является владельцем комментария {}", user.getEmail(), commentId);
            }

            return isOwner;

        } catch (ResponseStatusException e) {
            log.error("Ошибка при проверке прав на комментарий: {}", e.getMessage());
            return false;
        }
    }

    public boolean isAdOwner(Authentication authentication, Integer adId) {
        if (authentication == null || adId == null) return false;

        try {
            UserEntity user = getCurrentUser(authentication);

            AdEntity ad = adRepository.findById(adId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Объявление не найдено: " + adId));

            return ad.getAuthor().getId().equals(user.getId());

        } catch (ResponseStatusException e) {
            return false;
        }
    }

    public boolean isCommentOwner(Authentication authentication, Integer commentId) {
        if (authentication == null || commentId == null) return false;

        try {
            UserEntity user = getCurrentUser(authentication);

            CommentEntity comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Комментарий не найден: " + commentId));

            return comment.getAuthor().getId().equals(user.getId());

        } catch (ResponseStatusException e) {
            return false;
        }
    }
}