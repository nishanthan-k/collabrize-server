package com.collabrize.utils;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    // private final String JWT_SECRET = "your-secret-key"; // Use a strong secret key
    // private final long JWT_EXPIRATION = 86400000; // 24 hours

    public String generateToken(Authentication authentication) {
        // User user = (User) authentication.getPrincipal();
        // return Jwts.builder()
        //         .setSubject(user.getUsername())
        //         .setIssuedAt(new Date())
        //         .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
        //         .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
        //         .compact();
        return "jwt-token";
    }
}
