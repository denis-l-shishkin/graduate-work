package ru.skypro.homework.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class Ads {
    private Integer count;
    private List<Ad> results;
}