package com.spechofy.gestionevents.service;

import com.spechofy.gestionevents.entity.Utilisateur;
import com.spechofy.gestionevents.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GestionUtilisateurService {

    private final UtilisateurRepository repoUtilisateur;

    @Autowired
    public GestionUtilisateurService(UtilisateurRepository repoUtilisateur) {
        this.repoUtilisateur = repoUtilisateur;
    }

    public List<Utilisateur> obtenirTousUtilisateurs() {
        return repoUtilisateur.findAll();
    }

    public Optional<Utilisateur> obtenirUtilisateurParId(Long id) {
        return repoUtilisateur.findById(id);
    }

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        return repoUtilisateur.save(utilisateur);
    }

    public Utilisateur modifierUtilisateur(Long id, Utilisateur details) {
        return repoUtilisateur.findById(id)
                .map(user -> {
                    user.setNomUtilisateur(details.getNomUtilisateur());
                    user.setCourriel(details.getCourriel());
                    user.setMotDePasse(details.getMotDePasse());
                    user.setRole(details.getRole());
                    return repoUtilisateur.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public void supprimerUtilisateur(Long id) {
        repoUtilisateur.deleteById(id);
    }
}