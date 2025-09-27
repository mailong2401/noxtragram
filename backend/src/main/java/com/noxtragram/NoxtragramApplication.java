package com.noxtragram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class NoxtragramApplication {

  public static void main(String[] args) {
    SpringApplication.run(NoxtragramApplication.class, args);
  }

  /**
   * CORS Configuration cho Flutter frontend
   * Cho phép kết nối từ các domain cụ thể
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(
                "http://localhost:3000", // React frontend
                "http://localhost:5000", // Flutter web
                "http://localhost:8081", // Flutter mobile debug
                "http://10.0.2.2:8080", // Android emulator
                "http://127.0.0.1:8080", // Localhost
                "https://yourdomain.com" // Production domain
        )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);

        // WebSocket CORS configuration
        registry.addMapping("/ws/**")
            .allowedOrigins(
                "http://localhost:3000",
                "http://localhost:5000",
                "http://localhost:8081",
                "http://10.0.2.2:8080",
                "http://127.0.0.1:8080",
                "https://yourdomain.com")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
      }
    };
  }
}
