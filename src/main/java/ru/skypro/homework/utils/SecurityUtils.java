package ru.skypro.homework.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
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

    public boolean isAdOwnerOrAdmin(Authentication authentication, Integer adId) {
        if (authentication == null || adId == null) {
            log.warn("Проверка прав доступа: authentication или adId равен null");
            return false;
        }

        String email = authentication.getName();
        log.info("Проверка прав на объявление {} для пользователя {}", adId, email);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            log.info("Пользователь {} является администратором, доступ разрешен", email);
            return true;
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            log.warn("Пользователь {} не найден в БД", email);
            return false;
        }

        AdEntity ad = adRepository.findById(adId)
                .orElse(null);

        if (ad == null) {
            log.warn("Объявление с id {} не найдено", adId);
            return false;
        }

        boolean isOwner = ad.getAuthor().getId().equals(user.getId());

        if (isOwner) {
            log.info("Пользователь {} является владельцем объявления {}", email, adId);
        } else {
            log.warn("Пользователь {} НЕ является владельцем объявления {}", email, adId);
        }

        return isOwner;
    }

    public boolean isCommentOwnerOrAdmin(Authentication authentication, Integer commentId) {
        if (authentication == null || commentId == null) {
            log.warn("Проверка прав доступа: authentication или commentId равен null");
            return false;
        }

        String email = authentication.getName();
        log.info("Проверка прав на комментарий {} для пользователя {}", commentId, email);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            log.info("Пользователь {} является администратором, доступ разрешен", email);
            return true;
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            log.warn("Пользователь {} не найден в БД", email);
            return false;
        }

        CommentEntity comment = commentRepository.findById(commentId)
                .orElse(null);

        if (comment == null) {
            log.warn("Комментарий с id {} не найден", commentId);
            return false;
        }

        boolean isOwner = comment.getAuthor().getId().equals(user.getId());

        if (isOwner) {
            log.info("Пользователь {} является владельцем комментария {}", email, commentId);
        } else {
            log.warn("Пользователь {} НЕ является владельцем комментария {}", email, commentId);
        }

        return isOwner;
    }
}