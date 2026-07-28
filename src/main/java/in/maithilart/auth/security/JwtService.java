package in.maithilart.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private final Key key;
    private final long expirationMinutes;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes}") long expirationMinutes) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(
            String userId,
            String email,
            String fullName,
            Set<String> roles) {

        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirationMinutes * 60);

//        return Jwts.builder()
//                .setSubject(userId)
//                .claim("email", email)
//                .claim("roles", roles)
//                .setIssuedAt(Date.from(now))
//                .setExpiration(Date.from(expiry))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer("maithilart-auth")
                .setAudience("maithilart-api")
                .setId(UUID.randomUUID().toString())   // jti
                .claim("email", email)
                .claim("fullName", fullName)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Instant getExpiryInstant(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();
        return expiration.toInstant();
    }

    public Claims validateAndGetClaims(String token) {

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
            		.requireIssuer("maithilart-auth")
            	    .requireAudience("maithilart-api")
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return claimsJws.getBody();

        } catch (JwtException ex) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
    }

 // 2. Token se JTI nikalne ka method
    public String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }

 // Generic method jo kisi bhi claim (ID, Subject, etc.) को extract karta hai
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Ye method poore JWT payload (Claims) ko parse karta hai
    private Claims extractAllClaims(String token) {
        return  Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
