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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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

        // Проверка на наличие изображения
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Изображение обязательно");
        }

        UserEntity author = userService.getCurrentUser(authentication);
        AdEntity ad = adMapper.toEntity(properties, author);
        AdEntity savedAd = adRepository.save(ad);

        String imagePath = saveAdImage(savedAd.getPk(), image);
        savedAd.setImagePath(imagePath);
        savedAd = adRepository.save(savedAd);

        log.info("Объявление создано с id: {}", savedAd.getPk());
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
    @Transactional
    public void deleteAd(Integer id) {
        log.info("Удаление объявления с id: {}", id);

        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено: " + id));

        if (ad.getImagePath() != null) {
            deleteAdImage(ad.getImagePath());
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
            deleteAdImage(ad.getImagePath());
        }

        String imagePath = saveAdImage(id, image);
        ad.setImagePath(imagePath);
        adRepository.save(ad);

        log.info("Изображение для объявления {} обновлено", id);
    }

    private String saveAdImage(Integer adId, MultipartFile image) {
        try {
            Path uploadDir = Paths.get(adsPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("Создана директория для изображений объявлений: {}", uploadDir.toAbsolutePath());
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Можно загружать только изображения");
            }

            String extension = getFileExtension(image.getOriginalFilename());
            String fileName = adId + "_" + UUID.randomUUID() + extension;

            Path filePath = uploadDir.resolve(fileName);

            //Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            image.transferTo(filePath);

            log.info("Изображение объявления сохранено: {}", filePath.toAbsolutePath());

            return "/ad-images/" + fileName;

        } catch (IOException e) {
            log.error("Ошибка при сохранении изображения объявления", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при сохранении изображения", e);
        }
    }

    private void deleteAdImage(String imageUrl) {
        try {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(adsPath, fileName);

            Files.deleteIfExists(filePath);
            log.info("Изображение удалено: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при удалении изображения: {}", imageUrl, e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public byte[] getAdImage(Integer adId) {
        log.info("Получение изображения объявления с id: {}", adId);

        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено: " + adId));

        if (ad.getImagePath() == null || ad.getImagePath().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Изображение не найдено");
        }

        try {
            String fileName = ad.getImagePath().substring(ad.getImagePath().lastIndexOf("/") + 1);
            Path filePath = Paths.get(adsPath, fileName);

            if (!Files.exists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл изображения не найден");
            }

            return Files.readAllBytes(filePath);

        } catch (IOException e) {
            log.error("Ошибка при чтении изображения", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при чтении изображения");
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return adRepository.existsById(id);
    }
}