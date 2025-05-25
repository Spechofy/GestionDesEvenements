package com.spechofy.gestionevents.service;

import com.spechofy.gestionevents.entity.EvenementLocal;
import com.spechofy.gestionevents.entity.EvenementVirtuel;
import com.spechofy.gestionevents.entity.Utilisateur;
import com.spechofy.gestionevents.repository.EvenementLocalRepository;
import com.spechofy.gestionevents.repository.EvenementVirtuelRepository;
import com.spechofy.gestionevents.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GestionEvenementService {

    private final EvenementLocalRepository evenementLocalRepository;
    private final EvenementVirtuelRepository evenementVirtuelRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public GestionEvenementService(EvenementLocalRepository evenementLocalRepository,
                                   EvenementVirtuelRepository evenementVirtuelRepository,
                                   UtilisateurRepository utilisateurRepository) {
        this.evenementLocalRepository = evenementLocalRepository;
        this.evenementVirtuelRepository = evenementVirtuelRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<EvenementLocal> obtenirTousEvenementsLocaux() {
        return evenementLocalRepository.findAll();
    }

    public EvenementLocal creerEvenementLocal(EvenementLocal evenement) {
        return evenementLocalRepository.save(evenement);
    }

    public EvenementLocal modifierEvenementLocal(Long id, EvenementLocal details) {
        Optional<EvenementLocal> existingEvent = evenementLocalRepository.findById(id);
        if (existingEvent.isPresent()) {
            EvenementLocal evenement = existingEvent.get();
            evenement.avecDetailsMisAJour(details);
            return evenementLocalRepository.save(evenement);
        }
        throw new RuntimeException("Événement introuvable");
    }

    public void supprimerEvenementLocal(Long id) {
        evenementLocalRepository.deleteById(id);
    }

    public Optional<EvenementLocal> obtenirEvenementLocalParId(Long id) {
        return evenementLocalRepository.findById(id);
    }

    public List<Utilisateur> obtenirParticipantsEvenementLocal(Long eventId) {
        Optional<EvenementLocal> eventOpt = evenementLocalRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            return eventOpt.get().getParticipants();
        }
        throw new RuntimeException("Événement non trouvé");
    }

    public EvenementLocal participerEvenementLocal(Long eventId, Long userId) {
        Optional<EvenementLocal> eventOpt = evenementLocalRepository.findById(eventId);
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (eventOpt.isPresent() && userOpt.isPresent()) {
            EvenementLocal evenement = eventOpt.get();
            Utilisateur utilisateur = userOpt.get();
            evenement.getParticipants().add(utilisateur);
            utilisateur.getEvenementsParticipes().add(evenement);
            evenementLocalRepository.save(evenement);
            utilisateurRepository.save(utilisateur);
            return evenement;
        }
        throw new RuntimeException("Événement ou utilisateur non trouvé");
    }

    public EvenementLocal quitterEvenementLocal(Long eventId, Long userId) {
        Optional<EvenementLocal> eventOpt = evenementLocalRepository.findById(eventId);
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (eventOpt.isPresent() && userOpt.isPresent()) {
            EvenementLocal evenement = eventOpt.get();
            Utilisateur utilisateur = userOpt.get();
            evenement.getParticipants().remove(utilisateur);
            utilisateur.getEvenementsParticipes().remove(evenement);
            evenementLocalRepository.save(evenement);
            utilisateurRepository.save(utilisateur);
            return evenement;
        }
        throw new RuntimeException("Événement ou utilisateur non trouvé");
    }

    public List<EvenementVirtuel> obtenirTousEvenementsVirtuels() {
        return evenementVirtuelRepository.findAll();
    }

    public EvenementVirtuel creerEvenementVirtuel(EvenementVirtuel evenement) {
        return evenementVirtuelRepository.save(evenement);
    }

    public EvenementVirtuel obtenirEvenementVirtuelParId(Long id) {
        return evenementVirtuelRepository.findById(id).orElseThrow(() -> new RuntimeException("Événement virtuel non trouvé"));
    }

    public void supprimerEvenementVirtuel(Long id) {
        evenementVirtuelRepository.deleteById(id);
    }
}
