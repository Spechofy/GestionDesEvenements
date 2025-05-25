package com.spechofy.gestionevents.config;

import com.spechofy.gestionevents.dto.UtilisateurParticipeEvenement;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigurationProducteurKafka {

    // 1) Création automatique du topic "user-participation-events" si absent
    @Bean
    public NewTopic userParticipationTopic() {
        return new NewTopic("user-participation-events", 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, UtilisateurParticipeEvenement> fabriqueProducteur() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, UtilisateurParticipeEvenement> modeleKafka() {
        return new KafkaTemplate<>(fabriqueProducteur());
    }
}
