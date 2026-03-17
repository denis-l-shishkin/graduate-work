package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.entity.UserEntity;

public interface UserService {
    UserEntity getCurrentUser(Authentication authentication);
    UserEntity getUserByEmail(String email);
    boolean isAdmin(Authentication authentication);
    boolean isAdOwner(Authentication authentication, Integer adId);
    boolean isCommentOwner(Authentication authentication, Integer commentId);
    byte[] getAvatar(Integer userId);
}