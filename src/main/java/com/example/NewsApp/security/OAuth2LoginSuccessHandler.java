package com.example.NewsApp.security;

import com.example.NewsApp.dto.AuthProvider;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.repository.UserRepository;
import com.example.NewsApp.repository.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // ✅ FIND OR CREATE USER
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .enabled(true)
                                .authProvider(AuthProvider.GOOGLE) // ✅ FIXED
                                .profileCompleted(false)
                                .build()
                ));

        // ✅ DEFAULT ROLE FOR INCOMPLETE PROFILE
        String role = "INCOMPLETE";

        // ✅ IF PROFILE IS COMPLETE → FETCH ACTUAL ROLE
        if (user.isProfileCompleted()) {
            role = userRoleRepository.findByUser(user)
                    .map(ur -> ur.getRole().getName().toUpperCase())
                    .orElse("INCOMPLETE");
        }

        String token = jwtTokenProvider.generateToken(email, role);

        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "message": "Google login successful",
              "error": false,
              "data": {
                "token": "%s",
                "profileCompleted": %b,
                "role": "%s"
              }
            }
        """.formatted(token, user.isProfileCompleted(), role));
    }
}
