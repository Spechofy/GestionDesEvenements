package com.spechofy.gestionevents.service;

import org.springframework.stereotype.Service;

@Service
public class ServiceGeolocalisation {

    public double[] obtenirCoordonneesDepuisAdresse(String adresse) {
        // Simulation : Retourne des coordonnées fictives pour l'exemple
        // En production, utilisez une API comme Google Maps ou OpenStreetMap
        if ("Paris".equalsIgnoreCase(adresse)) {
            return new double[]{48.8566, 2.3522}; // Latitude, Longitude de Paris
        }
        throw new RuntimeException("Adresse non reconnue ou API non configurée");
    }

    public double distanceEnKm(double lat1, double lon1, double lat2, double lon2) {
        // Formule simplifiée de la distance haversine (approximation)
        final int rayonTerreKm = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return rayonTerreKm * c;
    }

    public boolean estZoneRestreinte(String lieu) {
        // Simulation : Retourne faux sauf pour certains lieux restreints
        return "Zone interdite".equalsIgnoreCase(lieu);
    }
}