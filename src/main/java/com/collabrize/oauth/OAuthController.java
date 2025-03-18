package com.collabrize.oauth;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.collabrize.user.UserRepo;
import com.collabrize.user.domain.User;
import com.collabrize.utils.JwtTokenProvider;
import com.collabrize.oauth.enums.OAuthProvider;
import com.collabrize.user.enums.Roles;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuthController {
    private final UserRepo userRepo;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/callback")
    public void oauthSuccess(Authentication authentication, HttpServletResponse response) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        System.out.println("\n\n" + oAuth2User.getAttributes().toString() + "\n\n");

        // Extract OAuth ID (GitHub uses Integer, Google uses String)
        Object oauthIdObj = oAuth2User.getAttribute("id");
        String oauthId =
                (oauthIdObj != null) ? String.valueOf(oauthIdObj) : oAuth2User.getAttribute("sub");

        if (oauthId == null) {
            throw new RuntimeException("OAuth ID is missing, authentication failed.");
        }

        // Identify provider
        OAuthProvider provider;
        if (oAuth2User.getAttribute("sub") != null) {
            provider = OAuthProvider.GOOGLE;
        } else {
            provider = OAuthProvider.GITHUB;
        }

        // Extract user details
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = provider == OAuthProvider.GITHUB ? oAuth2User.getAttribute("avatar_url")
                : oAuth2User.getAttribute("picture");
        String email = oAuth2User.getAttribute("email");

        // **Handle missing email for GitHub users**
        if (email == null || email.isEmpty()) {
            email = "github_user_" + oauthId + "@github.com"; // Common approach used by major
                                                              // platforms
            System.out.println("GitHub user has no public email, using fallback: " + email);
        }

        // **Ensure user exists in the database**
        Optional<User> existingUser = userRepo.findByOauthId(oauthId);
        if (existingUser.isEmpty()) {
            User newUser = User.builder().email(email).name(name).avatarUrl(avatarUrl)
                    .oauthId(oauthId).provider(provider).role(Roles.USER) // Assign default role
                    .build();
            userRepo.save(newUser);
            System.out.println("New user created: " + email);
        }

        // **Generate JWT token**
        String jwtToken = jwtTokenProvider.generateToken(authentication);

        // **Store JWT in HTTP-only cookie**
        Cookie cookie = new Cookie("auth_token", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        // **Redirect frontend**
        response.setHeader("Location", "/");
        response.setStatus(302);
    }
}
