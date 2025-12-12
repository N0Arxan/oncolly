package cat.teknos.oncolly.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    private final String SECRET_KEY = "super_secret_key_for_oncolly_app_must_be_very_long";

    // Token valid for 24 hours (86400000 ms)
    private final long EXPIRATION_TIME = 86400000;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // 1. GENERATE TOKEN (Used during Login)
    public String generateToken(String email, UUID userId, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId.toString()) // Add UserID to token
                .claim("role", role)                // Add Role to token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    // 2. VALIDATE TOKEN (Used by the Filter)
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 3. EXTRACT EMAIL FROM TOKEN
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}