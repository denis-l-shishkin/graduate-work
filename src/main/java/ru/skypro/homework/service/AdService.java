package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.request.CreateOrUpdateAd;
import ru.skypro.homework.dto.response.Ad;
import ru.skypro.homework.dto.response.Ads;
import ru.skypro.homework.dto.response.ExtendedAd;

public interface AdService {

    Ads getAllAds();

    ExtendedAd getAdById(Integer id);

    Ad createAd(CreateOrUpdateAd properties, MultipartFile image, Authentication authentication);

    Ad updateAd(Integer id, CreateOrUpdateAd updateAd);

    void deleteAd(Integer id);

    Ads getAdsByUser(Authentication authentication);

    void updateAdImage(Integer id, MultipartFile image);

    boolean existsById(Integer id);
}