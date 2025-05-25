package com.spechofy.gestionevents.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("VIRTUEL")
public class EvenementVirtuel extends Evenement {

    private String lienReunion;
}
