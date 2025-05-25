package com.spechofy.gestionevents.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.spechofy.gestionevents.dto.UtilisateurParticipeEvenement;
import com.spechofy.gestionevents.entity.EvenementLocal;
import com.spechofy.gestionevents.entity.EvenementVirtuel;
import com.spechofy.gestionevents.entity.Utilisateur;
import com.spechofy.gestionevents.service.GestionEvenementService;
import com.spechofy.gestionevents.service.GestionUtilisateurService;
import com.spechofy.gestionevents.service.ServiceGeolocalisation;
import com.spechofy.gestionevents.service.ServiceProducteurKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evenements")
public class GestionEvenementController {

    private static final Logger logger = LoggerFactory.getLogger(GestionEvenementController.class);
    private final GestionEvenementService serviceEvenement;
    private final ServiceGeolocalisation serviceGeo;
    private final ServiceProducteurKafka serviceKafka;
    private final GestionUtilisateurService utilisateurService;

    @Autowired
    public GestionEvenementController(
            GestionEvenementService serviceEvenement,
            ServiceGeolocalisation serviceGeo,
            ServiceProducteurKafka serviceKafka,
            GestionUtilisateurService utilisateurService) {
        this.serviceEvenement = serviceEvenement;
        this.serviceGeo = serviceGeo;
        this.serviceKafka = serviceKafka;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping(path = "/locaux", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EvenementLocal>> obtenirTousEvenementsLocaux() {
        List<EvenementLocal> events = serviceEvenement.obtenirTousEvenementsLocaux();
        return ResponseEntity.ok(events);
    }

    @GetMapping(path = "/locaux/proches", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EvenementLocal>> obtenirEvenementsProches(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        List<EvenementLocal> proches = serviceEvenement.obtenirTousEvenementsLocaux().stream()
                .filter(e -> e.getLatitude() != null && e.getLongitude() != null)
                .filter(e -> serviceGeo.distanceEnKm(latitude, longitude, e.getLatitude(), e.getLongitude()) <= 5)
                .collect(Collectors.toList());
        return ResponseEntity.ok(proches);
    }

    @GetMapping(path = "/locaux/suggerees", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EvenementLocal>> obtenirEvenementsSuggerees(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        List<EvenementLocal> suggerees = serviceEvenement.obtenirTousEvenementsLocaux().stream()
                .filter(e -> e.getLatitude() != null && e.getLongitude() != null)
                .sorted(Comparator.comparingDouble(e -> serviceGeo.distanceEnKm(latitude, longitude, e.getLatitude(), e.getLongitude())))
                .limit(3)
                .collect(Collectors.toList());
        return ResponseEntity.ok(suggerees);
    }

    @PostMapping(path = "/locaux", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> creerEvenementLocal(@RequestBody EvenementLocal evenement) {
        logger.info("Création événement local: {}", evenement);
        try {
            // Valider créateur
            if (evenement.getCreateur() == null || evenement.getCreateur().getIdentifiant() == null) {
                return ResponseEntity.badRequest().body(" Identifiant de créateur requis.");
            }
            var optUser = utilisateurService.obtenirUtilisateurParId(evenement.getCreateur().getIdentifiant());
            if (optUser.isEmpty()) {
                return ResponseEntity.badRequest().body(" Créateur introuvable.");
            }
            evenement.setCreateur(optUser.get());
            // Géolocalisation
            double[] coords = serviceGeo.obtenirCoordonneesDepuisAdresse(evenement.getLieu());
            evenement.setLatitude(coords[0]);
            evenement.setLongitude(coords[1]);
            var saved = serviceEvenement.creerEvenementLocal(evenement);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ Validation: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Erreur création: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ " + e.getMessage());
        }
    }

    @PostMapping(path = "/locaux/{eventId}/participer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> participerEvenementLocal(@PathVariable Long eventId,
                                                      @RequestParam Long userId) {
        logger.info("Participation event {} by user {}", eventId, userId);
        try {
            // Vérifier si l'utilisateur participe déjà à l'événement
            EvenementLocal evenement = serviceEvenement.obtenirEvenementLocalParId(eventId)
                    .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

            Optional<Utilisateur> utilisateurOpt = utilisateurService.obtenirUtilisateurParId(userId);
            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ Utilisateur non trouvé.");
            }

            Utilisateur utilisateur = utilisateurOpt.get();

            // Vérifier si l'utilisateur est déjà dans la liste des participants
            if (evenement.getParticipants().contains(utilisateur)) {
                return ResponseEntity.badRequest().body("❌ L'utilisateur participe déjà à cet événement.");
            }

            // Ajouter l'utilisateur à l'événement
            evenement.getParticipants().add(utilisateur);
            utilisateur.getEvenementsParticipes().add(evenement);

            // Sauvegarder les modifications
            serviceEvenement.creerEvenementLocal(evenement);  // Sauvegarder l'événement mis à jour
            utilisateurService.sauvegarderUtilisateur(utilisateur);  // Sauvegarder l'utilisateur

            return ResponseEntity.ok(evenement);  // Retourner l'événement mis à jour
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }


    @PostMapping(path = "/locaux/{eventId}/quitter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> quitterEvenementLocal(@PathVariable Long eventId,
                                                   @RequestParam Long userId) {
        try {
            var updated = serviceEvenement.quitterEvenementLocal(eventId, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    @GetMapping(path = "/locaux/{eventId}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Utilisateur>> obtenirParticipants(@PathVariable Long eventId) {
        try {
            var participants = serviceEvenement.obtenirParticipantsEvenementLocal(eventId);
            return ResponseEntity.ok(participants);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping(path = "/virtuels", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EvenementVirtuel>> obtenirTousEvenementsVirtuels() {
        return ResponseEntity.ok(serviceEvenement.obtenirTousEvenementsVirtuels());
    }

    @GetMapping(path = "/locaux/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EvenementLocal> obtenirEvenementLocalParId(@PathVariable Long id) {
        return serviceEvenement.obtenirEvenementLocalParId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/virtuels/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EvenementVirtuel> obtenirEvenementVirtuelParId(@PathVariable Long id) {
        Optional<EvenementVirtuel> evenement = Optional.ofNullable(serviceEvenement.obtenirEvenementVirtuelParId(id));
        if (evenement.isPresent()) {
            return ResponseEntity.ok(evenement.get()); // Renvoyer l'événement trouvé
        } else {
            return ResponseEntity.notFound().build(); // Si l'événement n'existe pas, retourner 404
        }
    }


    @PostMapping(path = "/virtuels", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EvenementVirtuel> creerEvenementVirtuel(@RequestBody EvenementVirtuel evenement) {
        return ResponseEntity.ok(serviceEvenement.creerEvenementVirtuel(evenement));
    }

    @PutMapping(path = "/locaux/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EvenementLocal> modifierEvenementLocal(@PathVariable Long id,
                                                                 @RequestBody EvenementLocal details) {
        try {
            return ResponseEntity.ok(serviceEvenement.modifierEvenementLocal(id, details));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping(path = "/locaux/{id}")
    public ResponseEntity<Void> supprimerEvenementLocal(@PathVariable Long id) {
        serviceEvenement.supprimerEvenementLocal(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/virtuels/{id}")
    public ResponseEntity<Void> supprimerEvenementVirtuel(@PathVariable Long id) {
        serviceEvenement.supprimerEvenementVirtuel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/simuler-participation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ParticipationUtilisateur(@RequestBody UtilisateurParticipeEvenement evenement) {
        serviceKafka.envoyerEvenementUtilisateurParticipe(evenement);
        return ResponseEntity.ok("📨 Kafka avec succès : " + evenement);
    }
}
