package com.satish.authandauthorization.oauth2_jan2025.Services;


import com.satish.authandauthorization.oauth2_jan2025.models.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static javax.crypto.Cipher.SECRET_KEY;

@Service
public class JwtService {
    private static final long REFRESH_EXPIRATION = 86400L;
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder().setSubject(user.getEmail()).setIssuedAt(now)
                .setExpiration(expiration).signWith(getSigningKey()).compact();
    }

    public String validateToken(String token) throws Exception{
        try{
            return Jwts.parser().setSigningKey(getSigningKey())
                    .build().
                    parseClaimsJws(token).getBody().getSubject();
        }catch (ExpiredJwtException ex){
            throw new RuntimeException("Expired JWT token");
        }catch (SignatureException ex){
            throw new Exception("Invalid JWT signature");
        }catch (MalformedJwtException ex){
            throw new Exception("Invalid JWT token");
        }
    }


    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, String.valueOf(SECRET_KEY))
                .compact();
    }

    // Get refresh token expiry
    public Date getRefreshTokenExpiry() {
        return new Date(System.currentTimeMillis() + REFRESH_EXPIRATION);
    }

    public boolean validateRefreshToken(String refreshToken, User user) {
        return refreshToken.equals(user.getRefreshToken()) && user.getRefreshTokenExpiry().after(new Date());
    }
}
