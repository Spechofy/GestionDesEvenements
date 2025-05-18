package com.spechofy.gestionevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestionEvenementsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GestionEvenementsApplication.class, args);
        System.out.println("🚀 Microservice GestionEvenements lancé avec succès !");
    }
}