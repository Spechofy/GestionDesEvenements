package com.spechofy.gestionevents.controller;

import com.spechofy.gestionevents.entity.Utilisateur;
import com.spechofy.gestionevents.service.GestionUtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class GestionUtilisateurController {

    private final GestionUtilisateurService serviceUtilisateur;

    public GestionUtilisateurController(GestionUtilisateurService serviceUtilisateur) {
        this.serviceUtilisateur = serviceUtilisateur;
    }

    @GetMapping
    public List<Utilisateur> obtenirTousUtilisateurs() {
        return serviceUtilisateur.obtenirTousUtilisateurs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> obtenirUtilisateurParId(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = serviceUtilisateur.obtenirUtilisateurParId(id);
        return utilisateur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> creerUtilisateur(@RequestBody Utilisateur utilisateur) {
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isEmpty()) {
            return ResponseEntity.badRequest().body("Le champ 'motDePasse' est requis !");
        }

        try {
            Utilisateur utilisateurSauvegarde = serviceUtilisateur.creerUtilisateur(utilisateur);
            return ResponseEntity.ok(utilisateurSauvegarde);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'inscription : " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> modifierUtilisateur(@PathVariable Long id, @RequestBody Utilisateur detailsUtilisateur) {
        try {
            Utilisateur utilisateurMisAJour = serviceUtilisateur.modifierUtilisateur(id, detailsUtilisateur);
            return ResponseEntity.ok(utilisateurMisAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        serviceUtilisateur.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }



}