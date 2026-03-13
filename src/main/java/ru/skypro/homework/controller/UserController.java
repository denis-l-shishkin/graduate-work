package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.request.NewPassword;
import ru.skypro.homework.dto.request.UpdateUser;
import ru.skypro.homework.dto.response.User;
import ru.skypro.homework.service.impl.UserServiceImpl;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(@RequestBody NewPassword newPassword, Authentication authentication) {
        log.info("Запрос на смену пароля от пользователя: {}", authentication.getName());
        userService.updatePassword(newPassword, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUser(Authentication authentication) {
        log.info("Запрос информации о пользователе: {}", authentication.getName());

        User user = userService.getUserInfo(authentication);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser, Authentication authentication) {
        log.info("Запрос на обновление информации пользователя: {}", authentication.getName());

        UpdateUser updated = userService.updateUserInfo(updateUser, authentication);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/me/image")
    public ResponseEntity<?> updateUserImage(@RequestParam MultipartFile image, Authentication authentication) {
        log.info("Запрос на обновление аватара пользователя: {}", authentication.getName());

        userService.updateAvatar(image, authentication);
        return ResponseEntity.ok().build();
    }
}