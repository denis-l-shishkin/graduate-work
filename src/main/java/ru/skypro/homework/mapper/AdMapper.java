package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.response.Ad;
import ru.skypro.homework.dto.response.Ads;
import ru.skypro.homework.dto.response.ExtendedAd;
import ru.skypro.homework.dto.request.CreateOrUpdateAd;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdMapper {

    public Ad toAdDto(AdEntity entity) {
        if (entity == null) {
            return null;
        }

        Ad dto = new Ad();
        dto.setAuthor(entity.getAuthor().getId());
        dto.setPk(entity.getPk());
        dto.setPrice(entity.getPrice());
        dto.setTitle(entity.getTitle());

        if (entity.getImagePath() != null && !entity.getImagePath().isEmpty()) {
            dto.setImage("/ads/" + entity.getPk() + "/image");
        }

        return dto;
    }

    public Ads toAdsDto(List<AdEntity> entities) {
        Ads dto = new Ads();

        if (entities == null || entities.isEmpty()) {
            dto.setCount(0);
            dto.setResults(List.of());  // пустой список вместо null
            return dto;
        }

        dto.setCount(entities.size());

        List<Ad> adDtos = entities.stream()
                .map(this::toAdDto)
                .collect(Collectors.toList());

        dto.setResults(adDtos);
        return dto;
    }

    public ExtendedAd toExtendedAdDto(AdEntity entity) {
        if (entity == null) {
            return null;
        }

        ExtendedAd dto = new ExtendedAd();
        dto.setPk(entity.getPk());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setTitle(entity.getTitle());

        UserEntity author = entity.getAuthor();
        if (author != null) {
            dto.setAuthorFirstName(author.getFirstName());
            dto.setAuthorLastName(author.getLastName());
            dto.setEmail(author.getEmail());
            dto.setPhone(author.getPhone());
        }

        if (entity.getImagePath() != null && !entity.getImagePath().isEmpty()) {
            dto.setImage("/ads/" + entity.getPk() + "/image");
        }

        return dto;
    }

    public AdEntity toEntity(CreateOrUpdateAd createAd, UserEntity author) {
        if (createAd == null || author == null) {
            return null;
        }

        AdEntity entity = new AdEntity();
        entity.setTitle(createAd.getTitle());
        entity.setPrice(createAd.getPrice());
        entity.setDescription(createAd.getDescription());
        entity.setAuthor(author);

        return entity;
    }

    public void updateEntity(CreateOrUpdateAd updateAd, AdEntity entity) {
        if (updateAd == null || entity == null) {
            return;
        }

        if (updateAd.getTitle() != null) {
            entity.setTitle(updateAd.getTitle());
        }
        if (updateAd.getPrice() != null) {
            entity.setPrice(updateAd.getPrice());
        }
        if (updateAd.getDescription() != null) {
            entity.setDescription(updateAd.getDescription());
        }
    }
}