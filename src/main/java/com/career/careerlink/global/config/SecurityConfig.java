package com.career.careerlink.global.config;

import com.career.careerlink.global.redis.RedisUtil;
import com.career.careerlink.global.security.JwtAuthenticationFilter;
import com.career.careerlink.global.security.JwtTokenProvider;
import com.career.careerlink.global.security.oauth.OAuth2FailureHandler;
import com.career.careerlink.global.security.oauth.OAuth2SuccessHandler;
import com.career.careerlink.global.security.oauth.UnifiedOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, redisUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           UnifiedOAuth2UserService userService,
                                           OAuth2FailureHandler failureHandler,
                                           OAuth2SuccessHandler successHandler) throws Exception {
        String[] permitAllUrls = {
                "/emp/check-bizRegNo"
                ,"/emp/registration-requests"
                ,"/emp/signup"
                ,"/job/filters"
                ,"/job/jobList"
                ,"/job/job-posting/detail"
                ,"/job/job-posting/hot"
                ,"/common/**"
                ,"/api/users/**"
                ,"/notice/**"
                ,"/faq/**"
                ,"/main/**"
                ,"/public/**"
                ,"/oauth2/**"
                ,"/login/**"
                ,"/api/auth/link/**"
        };

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permitAllUrls).permitAll()
                        .requestMatchers("/api/users/reissue").authenticated()
                        .requestMatchers("/api/users/auth/me").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(o -> o
                        .userInfoEndpoint(u -> u.userService(userService))
                        .failureHandler(failureHandler)
                        .successHandler(successHandler)
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000","https://careerlink.online")); // React 앱 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of());
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}


