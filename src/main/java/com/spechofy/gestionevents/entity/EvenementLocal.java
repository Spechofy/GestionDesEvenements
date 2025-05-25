package com.spechofy.gestionevents.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("LOCAL")
public class EvenementLocal extends Evenement {

    @Column(name = "lieu")
    private String lieu;

    public EvenementLocal avecDetailsMisAJour(EvenementLocal detailsEvenement) {
        this.setTitre(detailsEvenement.getTitre());
        this.setDate(detailsEvenement.getDate());
        this.setLieu(detailsEvenement.getLieu());
        return this;
    }
}
