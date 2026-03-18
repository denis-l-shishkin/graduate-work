package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.entity.UserEntity;

public interface UserService {
    UserEntity getCurrentUser(Authentication authentication);
    UserEntity getUserByEmail(String email);
    byte[] getAvatar(Integer userId);
}