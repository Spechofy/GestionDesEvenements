package com.spechofy.gestionevents.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "evenements")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_evenement", discriminatorType = DiscriminatorType.STRING)
public abstract class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifiant;

    private String titre;
    private LocalDateTime date;

    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    @ManyToMany(mappedBy = "evenementsParticipes", targetEntity = Utilisateur.class)
    @JsonIgnore
    private List<Utilisateur> participants = new ArrayList<>();
}