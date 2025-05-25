package com.spechofy.gestionevents.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final TokenAuthFilter tokenAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(TokenAuthFilter tokenAuthFilter, UserDetailsService userDetailsService) {
        this.tokenAuthFilter = tokenAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/evenements").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/evenements").permitAll()
                        .requestMatchers("/api/evenements/simuler-participation").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/evenements/locaux").permitAll()
                        .requestMatchers("/api/evenements/**").permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/api/evenements/*/participer", "POST")).hasRole("UTILISATEUR")
                        .requestMatchers(new AntPathRequestMatcher("/api/evenements/*/quitter", "POST")).hasRole("UTILISATEUR")
                        .requestMatchers(HttpMethod.POST, "/api/evenements/**/participer").hasRole("UTILISATEUR")
                        .requestMatchers(HttpMethod.POST, "/api/evenements/**/quitter").hasRole("UTILISATEUR")
                        .requestMatchers("/api/evenements/locaux/{eventId}/participer").hasRole("UTILISATEUR")
                        .requestMatchers("/api/evenements/locaux/{eventId}/quitter").hasRole("UTILISATEUR")
                        .requestMatchers(HttpMethod.GET, "/api/evenements/locaux/suggerees**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/evenements/locaux/proches**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/evenements/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/evenements/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/evenements/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/evenements/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class);

        System.out.println("🔐 Sécurité configurée avec succès !");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5175"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        System.out.println("🚀 CORS configuré pour : " + configuration.getAllowedOrigins());
        return source;
    }

    @Bean
    public PasswordEncoder encodeurMotDePasse() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager gestionnaireAuthentification(AuthenticationConfiguration config) throws Exception {
        DaoAuthenticationProvider fournisseurAuth = new DaoAuthenticationProvider();
        fournisseurAuth.setUserDetailsService(userDetailsService);
        fournisseurAuth.setPasswordEncoder(encodeurMotDePasse());
        return new ProviderManager(fournisseurAuth);
    }
}