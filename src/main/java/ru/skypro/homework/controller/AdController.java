package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.request.CreateOrUpdateAd;
import ru.skypro.homework.dto.response.Ad;
import ru.skypro.homework.dto.response.Ads;
import ru.skypro.homework.dto.response.ExtendedAd;

import java.util.ArrayList;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        log.info("Запрос на получение всех объявлений");
        // Код получения всех объявлений
        Ads ads = new Ads();
        ads.setCount(0);
        ads.setResults(new ArrayList<>());
        return ResponseEntity.ok(ads);
        //return ResponseEntity.ok(new Ads());
    }

    @PostMapping
    public ResponseEntity<Ad> addAd(
            @RequestPart CreateOrUpdateAd properties, @RequestPart MultipartFile image) {
        log.info("Запрос на добавление объявления");
        // Код добавления объявления
        Ad ad = new Ad();
        ad.setPk(1);
        ad.setAuthor(1);
        ad.setTitle(properties.getTitle());
        ad.setPrice(properties.getPrice());
        ad.setImage("/ads/1/image");
        return ResponseEntity.status(HttpStatus.CREATED).body(ad);
        //return ResponseEntity.status(HttpStatus.CREATED).body(new Ads());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable Integer id) {
        log.info("Запрос на получение объявления");
        // Код получения объявления по id
        ExtendedAd extendedAd = new ExtendedAd();
        extendedAd.setPk(id);
        extendedAd.setAuthorFirstName("Иван");
        extendedAd.setAuthorLastName("Иванов");
        extendedAd.setDescription("Описание объявления");
        extendedAd.setEmail("user@ex.com");
        extendedAd.setImage("/ads/" + id + "/image");
        extendedAd.setPhone("+7-123-456-78-90");
        extendedAd.setPrice(1000);
        extendedAd.setTitle("Заголовок");
        return ResponseEntity.ok(extendedAd);
        //return ResponseEntity.ok(new ExtendedAd());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAd(@PathVariable Integer id) {
        log.info("Запрос на удаление объявления");
        // Код удаления объявления
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAds(
            @PathVariable Integer id, @RequestBody CreateOrUpdateAd createOrUpdateAd) {
        log.info("Запрос на обновление объявления");
        // Код обновления объявления
        Ad ad = new Ad();
        ad.setPk(id);
        ad.setAuthor(1);
        ad.setTitle(createOrUpdateAd.getTitle());
        ad.setPrice(createOrUpdateAd.getPrice());
        ad.setImage("/ads/" + id + "/image");
        return ResponseEntity.ok(ad);
        //return ResponseEntity.ok(new Ad());
    }

    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe() {
        log.info("Запрос на получение объявлений текущего пользователя");
        // Код получения объявлений текущего пользователя
        Ads ads = new Ads();
        ads.setCount(0);
        ads.setResults(new ArrayList<>());
        return ResponseEntity.ok(ads);
        //return ResponseEntity.ok(new Ads());
    }

    @PatchMapping("/{id}/image")
    public ResponseEntity<?> updateImage(
            @PathVariable Integer id, @RequestParam MultipartFile image) {
        log.info("Запрос на обновление изображения объявления");
        // Код обновления изображения
        return ResponseEntity.ok().build();
    }
}