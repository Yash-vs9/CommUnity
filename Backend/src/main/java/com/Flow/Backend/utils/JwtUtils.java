package com.Flow.Backend.utils;

import com.Flow.Backend.exceptions.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JwtUtils {
    @Value("${SECRET_KEY}")
    private String SECRET_KEY;
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    @PostConstruct
    public void init() {
        System.out.println("JWT secret: " + this.SECRET_KEY);
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {

        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        try{
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch(ExpiredJwtException e){
            throw  new InvalidTokenException("Your Session has Expired, Login again");
        }
        catch (JwtException e){
            throw new InvalidTokenException("Invalid token. Please log in.");
        }

    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 120))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {

        return !isTokenExpired(token);
    }
}
