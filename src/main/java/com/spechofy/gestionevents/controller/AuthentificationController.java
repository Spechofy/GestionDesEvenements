package com.spechofy.gestionevents.controller;

import com.spechofy.gestionevents.config.TokenUtils;
import com.spechofy.gestionevents.entity.Role;
import com.spechofy.gestionevents.entity.Utilisateur;
import com.spechofy.gestionevents.repository.UtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthentificationController {

    private final UtilisateurRepository utilisateurRepo;
    private final PasswordEncoder encodeurMotDePasse;
    private final TokenUtils tokenUtils;

    public AuthentificationController(UtilisateurRepository utilisateurRepo, PasswordEncoder encodeurMotDePasse, TokenUtils tokenUtils) {
        this.utilisateurRepo = utilisateurRepo;
        this.encodeurMotDePasse = encodeurMotDePasse;
        this.tokenUtils = tokenUtils;
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> inscrireUtilisateur(@RequestBody Utilisateur utilisateur) {
        if (utilisateur.getRole() == null) {
            utilisateur.setRole(Role.UTILISATEUR);
        }

        utilisateur.setMotDePasse(encodeurMotDePasse.encode(utilisateur.getMotDePasse()));
        utilisateurRepo.save(utilisateur);
        return ResponseEntity.ok("Utilisateur inscrit !");
    }

    @PostMapping("/connexion")
    public ResponseEntity<?> connecterUtilisateur(@RequestBody Utilisateur utilisateur) {
        Optional<Utilisateur> dbUser = utilisateurRepo.findByCourriel(utilisateur.getCourriel());

        if (dbUser.isPresent() && encodeurMotDePasse.matches(utilisateur.getMotDePasse(), dbUser.get().getMotDePasse())) {
            String jeton = tokenUtils.genererJeton(utilisateur.getCourriel());

            Map<String, Object> reponse = new HashMap<>();
            reponse.put("jeton", jeton);
            reponse.put("idUtilisateur", dbUser.get().getIdentifiant()); // Remplacement de getId() par getIdentifiant()

            return ResponseEntity.ok(reponse);
        }

        return ResponseEntity.status(403).body("Identifiants invalides !");
    }
}