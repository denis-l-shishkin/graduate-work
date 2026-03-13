package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.request.Register;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    @Override
    public boolean login(String userName, String password) {
        log.info("Попытка входа пользователя: {}", userName);

        return userRepository.findByEmail(userName)
                .map(user -> {
                    boolean matches = encoder.matches(password, user.getPassword());
                    if (!matches) {
                        log.warn("Неверный пароль для пользователя: {}", userName);
                        throw new BadCredentialsException("Неверный пароль");
                    }
                    log.info("Успешный вход пользователя: {}", userName);
                    return true;
                })
                .orElseThrow(() -> new BadCredentialsException("Пользователь не найден: " + userName));
    }

    @Override
    @Transactional
    public boolean register(Register register) {
        log.info("Регистрация нового пользователя: {}", register.getUsername());

        // Проверяем, существует ли пользователь
        if (userRepository.findByEmail(register.getUsername()).isPresent()) {
            log.warn("Пользователь уже существует: {}", register.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь уже существует");
        }

        // Создаем сущность пользователя через маппер
        UserEntity userEntity = userMapper.toEntity(register);
        userEntity.setPassword(encoder.encode(register.getPassword()));

        // Сохраняем в БД
        userRepository.save(userEntity);

        log.info("Пользователь успешно зарегистрирован: {}", register.getUsername());
        return true;
    }
}