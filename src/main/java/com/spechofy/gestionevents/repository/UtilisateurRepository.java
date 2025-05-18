package com.spechofy.gestionevents.repository;

import com.spechofy.gestionevents.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByCourriel(String courriel);

    List<Utilisateur> findByEvenementsParticipes_Id(Long idEvenement);
}