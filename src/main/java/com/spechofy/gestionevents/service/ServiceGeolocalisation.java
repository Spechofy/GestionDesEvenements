package com.spechofy.gestionevents.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ServiceGeolocalisation {

    private static final Logger logger = LoggerFactory.getLogger(ServiceGeolocalisation.class);

    // Map of city names to their coordinates [latitude, longitude]
    private static final Map<String, double[]> CITY_COORDINATES = new HashMap<>();
    // Set of restricted zones
    private static final Set<String> RESTRICTED_ZONES = Set.of(
            "Zone Militaire",
            "Zone 51",
            "Base Navale Toulon",
            "Site Nucléaire Marcoule"
    );

    static {
        // Populate city coordinates (latitude, longitude)
        CITY_COORDINATES.put("paris", new double[]{48.8566, 2.3522});
        CITY_COORDINATES.put("lyon", new double[]{45.7640, 4.8357});
        CITY_COORDINATES.put("marseille", new double[]{43.2965, 5.3698});
        CITY_COORDINATES.put("bordeaux", new double[]{44.8378, -0.5792});
        CITY_COORDINATES.put("toulouse", new double[]{43.6047, 1.4442});
        CITY_COORDINATES.put("lille", new double[]{50.6293, 3.0573});
        CITY_COORDINATES.put("nice", new double[]{43.7102, 7.2620});
        CITY_COORDINATES.put("strasbourg", new double[]{48.5734, 7.7521});
        CITY_COORDINATES.put("berlin", new double[]{52.5200, 13.4050});
        CITY_COORDINATES.put("london", new double[]{51.5074, -0.1278});
    }

    public double[] obtenirCoordonneesDepuisAdresse(String adresse) {
        // Input validation
        if (adresse == null || adresse.trim().isEmpty()) {
            logger.error("Adresse nulle ou vide fournie : {}", adresse);
            throw new IllegalArgumentException("L'adresse ne peut pas être nulle ou vide.");
        }

        // Normalize the address to lowercase for case-insensitive matching
        String normalizedAdresse = adresse.trim().toLowerCase();
        logger.debug("Recherche des coordonnées pour l'adresse : {}", normalizedAdresse);

        // Check if the address matches any known city
        if (CITY_COORDINATES.containsKey(normalizedAdresse)) {
            double[] coords = CITY_COORDINATES.get(normalizedAdresse);
            logger.info("Coordonnées trouvées pour {} : [latitude={}, longitude={}]",
                    normalizedAdresse, coords[0], coords[1]);
            return coords;
        }

        // If address not found, throw an exception
        logger.warn("Adresse non reconnue : {}", normalizedAdresse);
        throw new RuntimeException("Adresse non reconnue ou API non configurée : " + adresse);
    }

    public double distanceEnKm(double lat1, double lon1, double lat2, double lon2) {
        // Validate coordinates
        if (!isValidCoordinate(lat1, lon1) || !isValidCoordinate(lat2, lon2)) {
            logger.error("Coordonnées invalides : lat1={}, lon1={}, lat2={}, lon2={}",
                    lat1, lon1, lat2, lon2);
            throw new IllegalArgumentException("Les coordonnées doivent être dans des plages valides : latitude [-90, 90], longitude [-180, 180].");
        }

        // Haversine formula
        final int rayonTerreKm = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = rayonTerreKm * c;

        logger.debug("Distance calculée entre [{},{}] et [{},{}] : {} km",
                lat1, lon1, lat2, lon2, distance);
        return distance;
    }

    public boolean estZoneRestreinte(String lieu) {
        if (lieu == null || lieu.trim().isEmpty()) {
            logger.error("Lieu nul ou vide fourni : {}", lieu);
            throw new IllegalArgumentException("Le lieu ne peut pas être nul ou vide.");
        }

        String normalizedLieu = lieu.trim().toLowerCase();
        boolean isRestricted = RESTRICTED_ZONES.stream()
                .anyMatch(zone -> normalizedLieu.contains(zone.toLowerCase()));

        if (isRestricted) {
            logger.warn("Zone restreinte détectée : {}", lieu);
        } else {
            logger.debug("Zone non restreinte : {}", lieu);
        }
        return isRestricted;
    }

    // Helper method to validate coordinates
    private boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    // Method to suggest nearby cities based on coordinates
    public Map<String, Double> suggestNearbyCities(double latitude, double longitude, double maxDistanceKm) {
        if (!isValidCoordinate(latitude, longitude)) {
            logger.error("Coordonnées invalides pour suggestion : lat={}, lon={}", latitude, longitude);
            throw new IllegalArgumentException("Coordonnées invalides pour la suggestion de villes.");
        }

        Map<String, Double> nearbyCities = new HashMap<>();
        for (Map.Entry<String, double[]> entry : CITY_COORDINATES.entrySet()) {
            String city = entry.getKey();
            double[] coords = entry.getValue();
            double distance = distanceEnKm(latitude, longitude, coords[0], coords[1]);
            if (distance <= maxDistanceKm) {
                nearbyCities.put(city, distance);
            }
        }

        logger.info("Villes proches trouvées dans un rayon de {} km : {}", maxDistanceKm, nearbyCities);
        return nearbyCities;
    }
}