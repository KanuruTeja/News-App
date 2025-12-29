package com.example.NewsApp.security;



import com.example.NewsApp.entity.Role;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.entity.UserRole;
import com.example.NewsApp.entity.UserRoleId;
import com.example.NewsApp.repository.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final int USER_ROLE_ID = 3;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        //  Save / fetch user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .enabled(true)
//                                .authProvider("GOOGLE")
                                .profileCompleted(true)
                                .build()
                ));

        // Assign USER role if not already present
        userRoleRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Role role = roleRepository.findById(USER_ROLE_ID)
                            .orElseThrow();

                    return userRoleRepository.save(
                            UserRole.builder()
                                    .id(new UserRoleId(user.getId(), role.getId()))
                                    .user(user)
                                    .role(role)
                                    .build()
                    );
                });

        // 3️⃣ Generate JWT with USER role
        String token = jwtTokenProvider.generateToken(email, "USER");

        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "message": "Google login successful",
              "error": false,
              "data": {
                "token": "%s",
                "profileCompleted": true,
                "role": "USER"
              }
            }
        """.formatted(token));
    }
}