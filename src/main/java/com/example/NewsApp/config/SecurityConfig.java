package com.example.NewsApp.config;

import com.example.NewsApp.security.JwtAuthenticationFilter;
import com.example.NewsApp.security.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                {
                                  "message": "Unauthorized or invalid token",
                                  "error": true,
                                  "status": 401
                                }
                            """);
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/reporter/**")
                        .hasAnyRole("ADMIN", "REPORTER")
                        .requestMatchers("/api/auth/user/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/admin/news/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("api/users/**").permitAll()
                        .requestMatchers("/api/tickets/**").permitAll()
                        .anyRequest().authenticated()
                )

                //  GOOGLE OAUTH
                .oauth2Login(oauth ->
                        oauth.successHandler(oAuth2LoginSuccessHandler)
                )

                //  JWT FILTER
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
