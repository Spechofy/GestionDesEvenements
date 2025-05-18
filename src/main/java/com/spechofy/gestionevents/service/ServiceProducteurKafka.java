package com.spechofy.gestionevents.service;

import com.spechofy.gestionevents.dto.UtilisateurParticipeEvenement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ServiceProducteurKafka {

    private final KafkaTemplate<String, UtilisateurParticipeEvenement> kafkaTemplate;

    @Autowired
    public ServiceProducteurKafka(KafkaTemplate<String, UtilisateurParticipeEvenement> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void envoyerEvenementUtilisateurParticipe(UtilisateurParticipeEvenement evenement) {
        kafkaTemplate.send("user-participation-events", evenement.getIdEvenement().toString(), evenement);
    }
}