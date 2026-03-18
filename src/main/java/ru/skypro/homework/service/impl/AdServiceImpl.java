package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.request.CreateOrUpdateAd;
import ru.skypro.homework.dto.response.Ad;
import ru.skypro.homework.dto.response.Ads;
import ru.skypro.homework.dto.response.ExtendedAd;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final UserService userService;
    private final ImageService imageService;

    @Value("${upload.ads.path}")
    private String adsPath;

    @Override
    public Ads getAllAds() {
        log.info("Получение всех объявлений");
        List<AdEntity> ads = adRepository.findAll();
        return adMapper.toAdsDto(ads);
    }

    @Override
    public ExtendedAd getAdById(Integer id) {
        log.info("Получение объявления по id: {}", id);
        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено: " + id));
        return adMapper.toExtendedAdDto(ad);
    }

    @Override
    public Ad createAd(CreateOrUpdateAd properties, MultipartFile image, Authentication authentication) {
        log.info("Создание нового объявления пользователем: {}", authentication.getName());

        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Изображение обязательно");
        }

        UserEntity author = userService.getCurrentUser(authentication);
        AdEntity ad = adMapper.toEntity(properties, author);
        AdEntity savedAd = adRepository.save(ad);

        String fileName = imageService.saveImage(image, "ad", savedAd.getPk());
        savedAd.setImagePath(fileName);
        savedAd = adRepository.save(savedAd);

        return adMapper.toAdDto(savedAd);
    }

    @Override
    public Ad updateAd(Integer id, CreateOrUpdateAd updateAd) {
        log.info("Обновление объявления с id: {}", id);

        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено: " + id));

        adMapper.updateEntity(updateAd, ad);
        AdEntity updatedAd = adRepository.save(ad);

        return adMapper.toAdDto(updatedAd);
    }

    @Override
    public void deleteAd(Integer id) {
        log.info("Удаление объявления с id: {}", id);

        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено: " + id));

        if (ad.getImagePath() != null) {
            imageService.deleteImage(ad.getImagePath(), "ad");
        }

        adRepository.delete(ad);
        log.info("Объявление с id: {} удалено", id);
    }

    @Override
    public Ads getAdsByUser(Authentication authentication) {
        log.info("Получение объявлений пользователя: {}", authentication.getName());

        UserEntity user = userService.getCurrentUser(authentication);
        List<AdEntity> userAds = adRepository.findByAuthor(user);

        return adMapper.toAdsDto(userAds);
    }

    @Override
    public void updateAdImage(Integer id, MultipartFile image) {
        log.info("Обновление изображения для объявления с id: {}", id);

        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Изображение обязательно");
        }

        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено: " + id));

        if (ad.getImagePath() != null) {
            imageService.deleteImage(ad.getImagePath(), "ad");
        }

        String fileName = imageService.saveImage(image, "ad", id);
        ad.setImagePath(fileName);
        adRepository.save(ad);

        log.info("Изображение для объявления {} обновлено", id);
    }

    @Override
    public byte[] getAdImage(Integer adId) {
        log.info("Получение изображения объявления с id: {}", adId);

        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено: " + adId));

        if (ad.getImagePath() == null || ad.getImagePath().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Изображение не найдено");
        }

        return imageService.getImage(ad.getImagePath(), "ad");
    }

    @Override
    public boolean existsById(Integer id) {
        return adRepository.existsById(id);
    }
}