package com.example.bank_rest.security.jwt;

import com.example.bank_rest.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Setter
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.access.token.secretKey}")
    private String jwtAccessTokenSecretKey;

    @Value("${jwt.access.token.expire.date}")
    private Long jwtAccessTokenExpireDate;

    @Value("${jwt.refresh.token.secretKey}")
    private String jwtRefreshTokenSecretKey;

    @Value("${jwt.refresh.token.expire.date}")
    private Long jwtRefreshTokenExpireDate;

    public String generateAccessToken(User user) {
        Date currentTime = new Date();

        List<String> roleNames = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(currentTime)
                .expiration(new Date(currentTime.getTime() + jwtAccessTokenExpireDate))
                .signWith(getAccessTokenSecretKey())
                .claim("roles", roleNames)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date currentTime = new Date();

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(currentTime)
                .expiration(new Date(currentTime.getTime() + jwtRefreshTokenExpireDate))
                .signWith(getRefreshTokenSecretKey())
                .compact();
    }


    private SecretKey getAccessTokenSecretKey() {
        return Keys.hmacShaKeyFor(jwtAccessTokenSecretKey.getBytes());
    }

    private SecretKey getRefreshTokenSecretKey() {
        return Keys.hmacShaKeyFor(jwtRefreshTokenSecretKey.getBytes());
    }

    public void validateAccessToken(String accessToken) {
        try {
            SecretKey key = getAccessTokenSecretKey();
            Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken);
        } catch (SecurityException | MalformedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        } catch (ExpiredJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT token compact of handler are invalid.");
        }
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            SecretKey key = getRefreshTokenSecretKey();
            Jwts.parser().verifyWith(key).build().parseSignedClaims(refreshToken);
        } catch (SecurityException | MalformedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        } catch (ExpiredJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT token compact of handler are invalid.");
        }
    }

    public Claims accessTokenClaims(String accessToken) {
        return Jwts
                .parser()
                .verifyWith(getAccessTokenSecretKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
    }

    public Claims refreshTokenClaims(String refreshToken) {
        return Jwts.parser().verifyWith(getRefreshTokenSecretKey()).build().parseSignedClaims(refreshToken).getPayload();
    }

}