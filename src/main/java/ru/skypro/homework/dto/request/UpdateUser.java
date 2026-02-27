package ru.skypro.homework.dto.request;

import lombok.Data;

@Data
public class UpdateUser {
    private String firstName;
    private String lastName;
    private String phone;
}