package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.request.CreateOrUpdateAd;
import ru.skypro.homework.dto.response.Ad;
import ru.skypro.homework.dto.response.Ads;
import ru.skypro.homework.dto.response.ExtendedAd;
import ru.skypro.homework.service.AdService;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        log.info("Запрос на получение всех объявлений");
        return ResponseEntity.ok(adService.getAllAds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable Integer id) {
        log.info("Запрос на получение объявления с id: {}", id);
        return ResponseEntity.ok(adService.getAdById(id));
    }

    @PostMapping
    public ResponseEntity<Ad> addAd(@RequestPart CreateOrUpdateAd properties,
                                    @RequestPart MultipartFile image,
                                    Authentication authentication) {
        log.info("Запрос на добавление объявления от пользователя: {}", authentication.getName());
        Ad createdAd = adService.createAd(properties, image, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAd);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityUtils.isAdOwnerOrAdmin(authentication, #id)")
    public ResponseEntity<?> removeAd(@PathVariable Integer id) {
        log.info("Запрос на удаление объявления с id: {}", id);
        adService.deleteAd(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@securityUtils.isAdOwnerOrAdmin(authentication, #id)")
    public ResponseEntity<Ad> updateAds(@PathVariable Integer id,
                                        @RequestBody CreateOrUpdateAd updateAd) {
        log.info("Запрос на обновление объявления с id: {}", id);
        Ad updatedAd = adService.updateAd(id, updateAd);
        return ResponseEntity.ok(updatedAd);
    }

    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        log.info("Запрос на получение объявлений пользователя: {}", authentication.getName());
        return ResponseEntity.ok(adService.getAdsByUser(authentication));
    }

    @PatchMapping("/{id}/image")
    @PreAuthorize("@securityUtils.isAdOwnerOrAdmin(authentication, #id)")
    public ResponseEntity<?> updateImage(@PathVariable Integer id,
                                         @RequestParam MultipartFile image) {
        log.info("Запрос на обновление изображения объявления с id: {}", id);
        adService.updateAdImage(id, image);
        return ResponseEntity.ok().build();
    }
}