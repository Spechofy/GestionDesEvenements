package com.spechofy.gestionevents.config;

import com.spechofy.gestionevents.dto.UtilisateurParticipeEvenement;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigurationProducteurKafka {

    @Bean
    public ProducerFactory<String, UtilisateurParticipeEvenement> fabriqueProducteur() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configuration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configuration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configuration);
    }

    @Bean
    public KafkaTemplate<String, UtilisateurParticipeEvenement> modeleKafka() {
        return new KafkaTemplate<>(fabriqueProducteur());
    }
}