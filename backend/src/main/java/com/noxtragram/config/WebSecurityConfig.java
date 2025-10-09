package com.noxtragram.config;

import com.noxtragram.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  private final JwtAuthTokenFilter jwtAuthTokenFilter;

  public WebSecurityConfig(JwtAuthTokenFilter jwtAuthTokenFilter) {
    this.jwtAuthTokenFilter = jwtAuthTokenFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // ✅ Bật CORS với cấu hình custom
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // ⚠️ Tắt CSRF cho REST API (vì frontend gọi qua token, không dùng form)
        .csrf(csrf -> csrf.disable())
        // Không tạo session
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Cấu hình quyền truy cập
        .authorizeHttpRequests(authz -> authz
            // ✅ Public endpoints
            .requestMatchers(
                "/uploads/**",
                "/users/login",
                "/users/register",
                "/users/check-email",
                "/users/check-username",
                "/api/uploads/**",
                "/ws/**" // Thêm WebSocket nếu bạn có
            ).permitAll()

            // ✅ Cho phép test API post (tạm mở)
            .requestMatchers("/api/posts/**").permitAll()

            // Protected endpoints
            .requestMatchers(
                "/users/me/**",
                "/users/me",
                "/users/me/profile-picture",
                "/users/*/follow/*",
                "/comments/**")
            .authenticated()

            // Admin endpoints
            .requestMatchers(
                "/admin/**",
                "/users/*/verify",
                "/users/*/deactivate")
            .hasRole("ADMIN")

            .anyRequest().authenticated())
        // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
        .addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  // ✅ Cấu hình CORS cho phép React truy cập
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
