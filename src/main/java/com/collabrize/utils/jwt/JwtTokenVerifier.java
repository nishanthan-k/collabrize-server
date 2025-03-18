package com.collabrize.utils.jwt;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenVerifier {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenVerifier(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String getUserEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build()
                .parseClaimsJws(token).getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
