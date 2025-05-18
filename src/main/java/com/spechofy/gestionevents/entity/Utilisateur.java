package com.spechofy.gestionevents.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "utilisateurs")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifiant;

    private String nomUtilisateur;
    private String courriel;

    @JsonProperty("motDePasse")
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "createur", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Evenement> evenementsCrees = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "utilisateur_evenements",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "evenement_id")
    )
    @JsonIgnore
    private List<Evenement> evenementsParticipes = new ArrayList<>();
}