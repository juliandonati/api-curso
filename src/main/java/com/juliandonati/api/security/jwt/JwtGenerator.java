package com.juliandonati.api.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtGenerator {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)); // Ponemos un Charset ara que no varíe según el SO.
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate  = new Date();
        Date expiryDate = new Date(currentDate.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512) // Constante que representa un algoritmo de representación que toma la clave
                                                           // secreta que le dimos y genera una firma digital de 512 bits que garantiza
                                                           // integridad (que nadie modifico el token), y autenticidad (el token fue
                                                           // generado por quien tiene la clave secreta).
                .compact();

        return token;
    }

    public String getUsernameFromJwt(String token) {
        // Claims: contenido del token / mapa de su payload.
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()// Preparamos un lector de tokens con su clave secreta.
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
        }
        catch (MalformedJwtException e){
            System.err.println("JWT inválido: " + e.getMessage());
        }
        catch (ExpiredJwtException e){
            System.err.println("JWT expirado: " + e.getMessage());
        }
        catch (UnsupportedJwtException e){
            System.err.println("JWT no soportado: "+ e.getMessage());
        }
        catch (IllegalArgumentException e){
            System.err.println("String de Claim JWT vacío: " + e.getMessage());
        }
        catch (SignatureException e){
            System.err.println("JWT firma fallada: " + e.getMessage());
        }

        return true;
    }
}
