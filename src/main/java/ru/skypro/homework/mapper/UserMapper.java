package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.response.User;
import ru.skypro.homework.dto.request.Register;
import ru.skypro.homework.dto.request.UpdateUser;
import ru.skypro.homework.entity.UserEntity;

@Component
public class UserMapper {

    public User toDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        User dto = new User();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setPhone(entity.getPhone());
        dto.setRole(entity.getRole());

        if (entity.getImagePath() != null && !entity.getImagePath().isEmpty()) {
            String imagePath = entity.getImagePath();
            if (imagePath.startsWith("/avatars/")) {
                dto.setImage(imagePath);
            } else {
                String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                dto.setImage("/avatars/" + fileName);
            }
        }

        return dto;
    }

    public UserEntity toEntity(Register register) {
        if (register == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setEmail(register.getUsername());
        entity.setPassword(register.getPassword());
        entity.setFirstName(register.getFirstName());
        entity.setLastName(register.getLastName());
        entity.setPhone(register.getPhone());
        entity.setRole(register.getRole());

        return entity;
    }

    public void updateEntity(UpdateUser updateUser, UserEntity entity) {
        if (updateUser == null || entity == null) {
            return;
        }

        if (updateUser.getFirstName() != null) {
            entity.setFirstName(updateUser.getFirstName());
        }
        if (updateUser.getLastName() != null) {
            entity.setLastName(updateUser.getLastName());
        }
        if (updateUser.getPhone() != null) {
            entity.setPhone(updateUser.getPhone());
        }
    }
}