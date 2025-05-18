package com.spechofy.gestionevents.controller;

import com.spechofy.gestionevents.dto.UtilisateurParticipeEvenement;
import com.spechofy.gestionevents.entity.EvenementLocal;
import com.spechofy.gestionevents.entity.Utilisateur;
import com.spechofy.gestionevents.entity.EvenementVirtuel;
import com.spechofy.gestionevents.service.GestionEvenementService;
import com.spechofy.gestionevents.service.ServiceGeolocalisation;
import com.spechofy.gestionevents.service.ServiceProducteurKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evenements")
public class GestionEvenementController {

    private final GestionEvenementService serviceEvenement;
    private final ServiceGeolocalisation serviceGeo;
    private final ServiceProducteurKafka serviceKafka;

    @Autowired
    public GestionEvenementController(GestionEvenementService serviceEvenement, ServiceGeolocalisation serviceGeo, ServiceProducteurKafka serviceKafka) {
        this.serviceEvenement = serviceEvenement;
        this.serviceGeo = serviceGeo;
        this.serviceKafka = serviceKafka;
    }

    @GetMapping("/locaux")
    public List<EvenementLocal> obtenirTousEvenementsLocaux() {
        return serviceEvenement.obtenirTousEvenementsLocaux();
    }

    @GetMapping("/locaux/proches")
    public List<EvenementLocal> obtenirEvenementsProches(@RequestParam double latitude, @RequestParam double longitude) {
        return serviceEvenement.obtenirTousEvenementsLocaux().stream()
                .filter(e -> e.getLatitude() != null && e.getLongitude() != null)
                .filter(e -> serviceGeo.distanceEnKm(latitude, longitude, e.getLatitude(), e.getLongitude()) <= 5)
                .collect(Collectors.toList());
    }

    @GetMapping("/locaux/suggerees")
    public List<EvenementLocal> obtenirEvenementsSuggerees(@RequestParam double latitude, @RequestParam double longitude) {
        return serviceEvenement.obtenirTousEvenementsLocaux().stream()
                .filter(e -> e.getLatitude() != null && e.getLongitude() != null)
                .sorted(Comparator.comparingDouble(e -> serviceGeo.distanceEnKm(latitude, longitude, e.getLatitude(), e.getLongitude())))
                .limit(3)
                .collect(Collectors.toList());
    }

    @PostMapping("/locaux")
    public ResponseEntity<?> creerEvenementLocal(@RequestBody EvenementLocal evenement) {
        if (serviceGeo.estZoneRestreinte(evenement.getLieu())) {
            return ResponseEntity.badRequest().body("❌ Lieu interdit pour un événement !");
        }

        try {
            double[] coordonnees = serviceGeo.obtenirCoordonneesDepuisAdresse(evenement.getLieu());
            evenement.setLatitude(coordonnees[0]);
            evenement.setLongitude(coordonnees[1]);

            EvenementLocal sauvegarde = serviceEvenement.creerEvenementLocal(evenement);
            return ResponseEntity.ok(sauvegarde);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    @GetMapping("/virtuels")
    public List<EvenementVirtuel> obtenirTousEvenementsVirtuels() {
        return serviceEvenement.obtenirTousEvenementsVirtuels();
    }

    @GetMapping("/locaux/{id}")
    public Optional<EvenementLocal> obtenirEvenementLocalParId(@PathVariable Long id) {
        return serviceEvenement.obtenirEvenementLocalParId(id);
    }

    @GetMapping("/virtuels/{id}")
    public Optional<EvenementVirtuel> obtenirEvenementVirtuelParId(@PathVariable Long id) {
        return serviceEvenement.obtenirEvenementVirtuelParId(id);
    }

    @PostMapping("/virtuels")
    public EvenementVirtuel creerEvenementVirtuel(@RequestBody EvenementVirtuel evenement) {
        return serviceEvenement.creerEvenementVirtuel(evenement);
    }

    @PutMapping("/locaux/{id}")
    public EvenementLocal modifierEvenementLocal(@PathVariable Long id, @RequestBody EvenementLocal detailsEvenement) {
        return serviceEvenement.modifierEvenementLocal(id, detailsEvenement);
    }

    @DeleteMapping("/locaux/{id}")
    public void supprimerEvenementLocal(@PathVariable Long id) {
        serviceEvenement.supprimerEvenementLocal(id);
    }

    @DeleteMapping("/virtuels/{id}")
    public void supprimerEvenementVirtuel(@PathVariable Long id) {
        serviceEvenement.supprimerEvenementVirtuel(id);
    }

    @PostMapping("/locaux/{eventId}/participer")
    public ResponseEntity<?> participerEvenementLocal(@PathVariable Long eventId, @RequestParam Long userId) {
        try {
            EvenementLocal evenementJoint = serviceEvenement.participerEvenementLocal(eventId, userId);
            return ResponseEntity.ok(evenementJoint);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    @PostMapping("/locaux/{eventId}/quitter")
    public ResponseEntity<?> quitterEvenementLocal(@PathVariable Long eventId, @RequestParam Long userId) {
        try {
            EvenementLocal evenementMisAJour = serviceEvenement.quitterEvenementLocal(eventId, userId);
            return ResponseEntity.ok(evenementMisAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    @GetMapping("/locaux/{eventId}/participants")
    public List<Utilisateur> obtenirParticipants(@PathVariable Long eventId) {
        return serviceEvenement.obtenirParticipantsEvenementLocal(eventId);
    }

    @PostMapping("/simuler-participation")
    public ResponseEntity<String> simulerParticipationUtilisateur(@RequestBody UtilisateurParticipeEvenement evenement) {
        serviceKafka.envoyerEvenementUtilisateurParticipe(evenement);
        return ResponseEntity.ok("📨 Événement Kafka envoyé avec succès : " + evenement);
    }
}