package com.spechofy.gestionevents.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenUtils {
    private final String secretJeton = "clefSuperSecretePourAuthentificationJWT123456789"; // Doit être au moins 32 caractères
    private final long dureeExpirationJeton = 86400000; // 1 jour
    private final Key cle = Keys.hmacShaKeyFor(secretJeton.getBytes());

    public String genererJeton(String courriel) {
        return Jwts.builder()
                .setSubject(courriel)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + dureeExpirationJeton))
                .signWith(cle, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraireCourrielDuJeton(String jeton) {
        return Jwts.parserBuilder()
                .setSigningKey(cle)
                .build()
                .parseClaimsJws(jeton)
                .getBody()
                .getSubject();
    }

    public boolean validerJeton(String jeton) {
        try {
            Jwts.parserBuilder().setSigningKey(cle).build().parseClaimsJws(jeton);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Jeton expiré : " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("❌ Jeton non supporté : " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("❌ Jeton mal formé : " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("❌ Signature Jeton invalide : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Jeton vide ou null : " + e.getMessage());
        }
        return false;
    }
}