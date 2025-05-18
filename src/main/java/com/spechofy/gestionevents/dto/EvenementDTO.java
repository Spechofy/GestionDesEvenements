package com.spechofy.gestionevents.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvenementDTO {
    private Long identifiant;
    private String titre;
    private String lieu;
    private LocalDateTime date;
    private String typeEvenement;
    private Long idOrganisateur;
}