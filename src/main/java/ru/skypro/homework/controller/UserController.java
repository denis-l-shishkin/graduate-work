package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.request.NewPassword;
import ru.skypro.homework.dto.request.UpdateUser;
import ru.skypro.homework.dto.response.User;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(@RequestBody NewPassword newPassword) {
        log.info("Запрос на смену пароля");
        // Код смены пароля
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUser() {
        log.info("Запрос на текущего пользователя");
        // Код получения информации о пользователе
        User user = new User();
        user.setId(1);
        user.setEmail("user@ex.com");
        user.setFirstName("Иван");
        user.setLastName("Иванов");
        user.setPhone("+7-123-456-78-90");
        user.setRole(Role.USER);
        user.setImage("/users/me/image");
        return ResponseEntity.ok(user);
        //return ResponseEntity.ok(new User());
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser) {
        log.info("Запрос на обновление пользователя");
        // Код обновления информации о пользователе
        return ResponseEntity.ok(updateUser);
    }

    @PatchMapping("/me/image")
    public ResponseEntity<?> updateUserImage(@RequestParam MultipartFile image) {
        log.info("Запрос на обновление аватара пользователя");
        // Код обновления аватара
        return ResponseEntity.ok().build();
    }
}