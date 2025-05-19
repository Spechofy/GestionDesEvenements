package com.spechofy.gestionevents.repository;

import com.spechofy.gestionevents.entity.EvenementLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvenementLocalRepository extends JpaRepository<EvenementLocal, Long> {

    List<EvenementLocal> findByCreateurIdentifiant(Long identifiant); // Corrected from findByCreateurId

    @Query("SELECT e FROM EvenementLocal e WHERE LOWER(e.lieu) = LOWER(:lieu) " +
            "AND e.date < :fin AND :debut < e.date")
    List<EvenementLocal> trouverEvenementsConflits(String lieu, LocalDateTime debut, LocalDateTime fin);
}