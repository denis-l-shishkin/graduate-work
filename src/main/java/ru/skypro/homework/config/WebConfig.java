package ru.skypro.homework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.avatars.path}")
    private String avatarsPath;

    @Value("${upload.ads.path}")
    private String adsPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + avatarsPath);

        registry.addResourceHandler("/ad-images/**")
                .addResourceLocations("file:" + adsPath);
    }
}