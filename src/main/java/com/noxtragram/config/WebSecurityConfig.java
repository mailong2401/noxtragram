package com.noxtragram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // THÊM DÒNG NÀY
        .csrf(csrf -> csrf.disable()) // Tạm disable CSRF cho development
        .authorizeHttpRequests(authz -> authz
            .anyRequest().permitAll() // Cho phép tất cả request mà không cần authentication
        );

    return http.build();
  }

  // Thêm bean PasswordEncoder để Spring inject vào UserServiceImpl
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // THÊM BEAN CORS CONFIGURATION - ĐÂY LÀ PHẦN QUAN TRỌNG
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:3000", // React frontend
        "http://localhost:5000", // Flutter web
        "http://localhost:8081", // Flutter mobile debug
        "http://10.0.2.2:8080", // Android emulator
        "http://127.0.0.1:8080", // Localhost
        "http://localhost:8080", // Chính server của bạn
        "https://yourdomain.com" // Production domain
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L); // Cache preflight request 1 giờ

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả endpoints
    return source;
  }
}
