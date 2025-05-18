package com.spechofy.gestionevents.service;

import com.spechofy.gestionevents.entity.Utilisateur;
import com.spechofy.gestionevents.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository repoUtilisateur;

    @Autowired
    public UserDetailsServiceImpl(UtilisateurRepository repoUtilisateur) {
        this.repoUtilisateur = repoUtilisateur;
    }

    @Override
    public UserDetails loadUserByUsername(String courriel) throws UsernameNotFoundException {
        Utilisateur utilisateur = repoUtilisateur.findByCourriel(courriel)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec courriel : " + courriel));

        SimpleGrantedAuthority autorite = new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name());
        return new User(
                utilisateur.getCourriel(),
                utilisateur.getMotDePasse(),
                Collections.singletonList(autorite)
        );
    }
}