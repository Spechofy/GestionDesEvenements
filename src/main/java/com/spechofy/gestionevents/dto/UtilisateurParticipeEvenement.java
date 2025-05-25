package com.spechofy.gestionevents.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurParticipeEvenement {
    private Long idUtilisateur;
    private Long idEvenement;
    private String nomEvenement;
}
