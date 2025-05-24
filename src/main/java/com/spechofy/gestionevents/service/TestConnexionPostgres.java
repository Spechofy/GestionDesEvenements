package com.spechofy.gestionevents.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnexionPostgres {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/Events-db";
        String user = "dahia";
        String password = "secret";

        System.out.println("Tentative de connexion à PostgreSQL...");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Connexion réussie à PostgreSQL !");
            } else {
                System.out.println(" Échec de connexion !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }
}