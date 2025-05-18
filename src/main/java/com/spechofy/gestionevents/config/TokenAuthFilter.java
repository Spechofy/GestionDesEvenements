package com.spechofy.gestionevents.config;

import com.spechofy.gestionevents.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenAuthFilter extends OncePerRequestFilter {

    private final TokenUtils tokenUtils;
    private final UserDetailsService userDetailsService;

    public TokenAuthFilter(TokenUtils tokenUtils, UserDetailsServiceImpl userDetailsService) {
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String chemin = request.getRequestURI();
        System.out.println("🔒 Tentative d'accès à : " + chemin);

        if (chemin.matches("^/api/evenements/locaux/(suggerees|proches)(/)?(\\?.*)?$")) {
            System.out.println("🚫 Filtre Token ignoré pour : " + chemin);
            filterChain.doFilter(request, response);
            return;
        }

        String jeton = request.getHeader("Authorization");
        System.out.println("🔍 En-tête Authorization reçu : " + jeton);

        if (jeton != null && jeton.startsWith("Bearer ")) {
            jeton = jeton.substring(7);

            try {
                if (!tokenUtils.validerJeton(jeton)) {
                    System.out.println("❌ Jeton invalide !");
                    filterChain.doFilter(request, response);
                    return;
                }

                String courriel = tokenUtils.extraireCourrielDuJeton(jeton);
                System.out.println("📧 Courriel extrait du jeton : " + courriel);

                if (courriel != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    System.out.println("🔎 Chargement de l'utilisateur depuis UserDetailsService...");
                    UserDetails userDetails = userDetailsService.loadUserByUsername(courriel);
                    System.out.println("✅ Utilisateur trouvé : " + userDetails.getUsername());
                    System.out.println("🔐 Rôles utilisateur : " + userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("🧪 Authentification injectée dans SecurityContext !");
                }
            } catch (Exception e) {
                System.out.println("❌ Erreur Token/filtre : " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Aucun jeton ou mauvais format !");
        }

        System.out.println("🛡️ Authentification actuelle dans SecurityContext : " +
                SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);

        System.out.println("➡️ Fin du filtre. Contexte final : " +
                SecurityContextHolder.getContext().getAuthentication());
    }
}