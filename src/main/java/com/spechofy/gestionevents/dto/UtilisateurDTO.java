package com.spechofy.gestionevents.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurDTO {
    private Long identifiant;
    private String nomUtilisateur;
    private String courriel;
    private List<Long> idsEvenements; // Liste des événements créés par l'utilisateur
}