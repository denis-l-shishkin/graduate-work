package ru.skypro.homework.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.skypro.homework.service.impl.CustomUserDetailsService;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars/**",
            "/login",
            "/register"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .cors() // Включаем CORS
                .and()
                .authorizeHttpRequests(
                        authorization ->
                                authorization
                                        // Разрешаем OPTIONS запросы для preflight
                                        .mvcMatchers(HttpMethod.OPTIONS, "/**")
                                        .permitAll()
                                        .mvcMatchers(AUTH_WHITELIST)
                                        .permitAll()
                                        // GET запросы к объявлениям доступны всем
                                        .mvcMatchers(HttpMethod.GET, "/ads", "/ads/{id}")
                                        .permitAll()
                                        // GET запросы к картинкам доступны всем (ВАЖНО!)
                                        .mvcMatchers(HttpMethod.GET, "/ads/*/image", "/users/*/image")
                                        .permitAll()
                                        // Все остальные запросы к API требуют авторизации
                                        .mvcMatchers("/ads/**", "/users/**")
                                        .authenticated()
                                        .anyRequest()
                                        .authenticated())
                .httpBasic(withDefaults())
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}