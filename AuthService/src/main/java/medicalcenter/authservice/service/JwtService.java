package medicalcenter.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import medicalcenter.authservice.model.entity.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-expiration-time}")
    private Long accessTokenExpiration;

    @Getter
    @Value("${security.jwt.refresh-expiration-time}")
    private Long refreshExpirationTime;

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        if (userDetails instanceof User u) {
            if (u.getFirstName() != null) claims.put("firstName", u.getFirstName());
            if (u.getLastName() != null) claims.put("lastName", u.getLastName());
            if (u.getAvatarUrl() != null) claims.put("avatarUrl", u.getAvatarUrl());
            // ВАЖНО: Добавляем userId (patientId) в токен
            if (u.getId() != null) {
                claims.put("userId", u.getId().toString());
                claims.put("patientId", u.getId().toString()); // Для совместимости
            }
        }
        return createToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), refreshExpirationTime);
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String phone = userDetails.getUsername();
        return phone.equals(extractPhone(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractPhone(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }
}
