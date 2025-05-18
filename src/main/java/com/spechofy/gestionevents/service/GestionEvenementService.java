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

    private final EvenementLocalRepository repoEvenementLocal;
    private final EvenementVirtuelRepository repoEvenementVirtuel;
    private final UtilisateurRepository repoUtilisateur;

    @Autowired
    public GestionEvenementService(EvenementLocalRepository repoEvenementLocal,
                                   EvenementVirtuelRepository repoEvenementVirtuel,
                                   UtilisateurRepository repoUtilisateur) {
        this.repoEvenementLocal = repoEvenementLocal;
        this.repoEvenementVirtuel = repoEvenementVirtuel;
        this.repoUtilisateur = repoUtilisateur;
    }

    public List<EvenementLocal> obtenirTousEvenementsLocaux() {
        return repoEvenementLocal.findAll();
    }

    public List<EvenementVirtuel> obtenirTousEvenementsVirtuels() {
        return repoEvenementVirtuel.findAll();
    }

    public Optional<EvenementLocal> obtenirEvenementLocalParId(Long id) {
        return repoEvenementLocal.findById(id);
    }

    public Optional<EvenementVirtuel> obtenirEvenementVirtuelParId(Long id) {
        return repoEvenementVirtuel.findById(id);
    }

    public EvenementLocal creerEvenementLocal(EvenementLocal evenement) {
        return repoEvenementLocal.save(evenement);
    }

    public EvenementVirtuel creerEvenementVirtuel(EvenementVirtuel evenement) {
        return repoEvenementVirtuel.save(evenement);
    }

    public EvenementLocal modifierEvenementLocal(Long id, EvenementLocal details) {
        return repoEvenementLocal.findById(id)
                .map(evenement -> {
                    evenement.avecDetailsMisAJour(details);
                    return repoEvenementLocal.save(evenement);
                })
                .orElseThrow(() -> new RuntimeException("Événement local non trouvé"));
    }

    public void supprimerEvenementLocal(Long id) {
        repoEvenementLocal.deleteById(id);
    }

    public void supprimerEvenementVirtuel(Long id) {
        repoEvenementVirtuel.deleteById(id);
    }

    public EvenementLocal participerEvenementLocal(Long idEvenement, Long idUtilisateur) {
        EvenementLocal evenement = repoEvenementLocal.findById(idEvenement)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        Utilisateur utilisateur = repoUtilisateur.findById(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        evenement.getParticipants().add(utilisateur);
        return repoEvenementLocal.save(evenement);
    }

    public EvenementLocal quitterEvenementLocal(Long idEvenement, Long idUtilisateur) {
        EvenementLocal evenement = repoEvenementLocal.findById(idEvenement)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        Utilisateur utilisateur = repoUtilisateur.findById(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        evenement.getParticipants().remove(utilisateur);
        return repoEvenementLocal.save(evenement);
    }

    public List<Utilisateur> obtenirParticipantsEvenementLocal(Long idEvenement) {
        EvenementLocal evenement = repoEvenementLocal.findById(idEvenement)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        return evenement.getParticipants();
    }
}