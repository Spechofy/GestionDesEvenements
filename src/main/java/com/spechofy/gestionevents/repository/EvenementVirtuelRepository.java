package com.spechofy.gestionevents.repository;

import com.spechofy.gestionevents.entity.EvenementVirtuel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvenementVirtuelRepository extends JpaRepository<EvenementVirtuel, Long> {
    List<EvenementVirtuel> findByCreateurIdentifiant(Long identifiant); // Corrected from findByCreateurId
}