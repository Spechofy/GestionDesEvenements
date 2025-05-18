package com.spechofy.gestionevents.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("LOCAL")
public class EvenementLocal extends Evenement {
    private String lieu;
    public EvenementLocal avecDetailsMisAJour(EvenementLocal detailsEvenement) {
        this.setTitre(detailsEvenement.getTitre());
        this.setDate(detailsEvenement.getDate());
        this.setLieu(detailsEvenement.getLieu());
        return this;
    }
}