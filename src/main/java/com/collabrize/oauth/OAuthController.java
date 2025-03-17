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
        String email = oAuth2User.getAttribute("email");

        System.out.println("\n\n"+ email  +"\n\n");

        // Check if user exists in DB
        Optional<User> existingUser = userRepo.findByEmail(email);
        if (existingUser.isEmpty()) {
            // Save new user in DB
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(oAuth2User.getAttribute("name"));
            newUser.setProvider(oAuth2User.getAttribute("iss"));
            userRepo.save(newUser);
        }

        // Generate JWT token
        String jwtToken = jwtTokenProvider.generateToken(authentication);

        // Store JWT in an HTTP-only cookie
        Cookie cookie = new Cookie("auth_token", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Use HTTPS in production
        cookie.setPath("/");
        response.addCookie(cookie);

        // Redirect frontend to home page
        response.setHeader("Location", "/");
        response.setStatus(302);
    }
}
