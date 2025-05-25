package com.spechofy.gestionevents.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class ServiceGeolocalisation {

    private final WebClient geolocationWebClient;

    public ServiceGeolocalisation(WebClient geolocationWebClient) {
        this.geolocationWebClient = geolocationWebClient;
    }

    public double distanceEnKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    @Cacheable("geocoding")
    public double[] obtenirCoordonneesDepuisAdresse(String adresse) {
        try {
            NominatimResult[] results = geolocationWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("q", adresse)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()

                    .bodyToMono(NominatimResult[].class)
                    .block();

            if (results == null || results.length == 0) {
                throw new RuntimeException("Adresse OSM introuvable: " + adresse);
            }
            double lat = Double.parseDouble(results[0].lat);
            double lon = Double.parseDouble(results[0].lon);
            return new double[]{lat, lon};
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Erreur HTTP OSM: " + e.getStatusCode(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur géocodage OSM.", e);
        }
    }

    @Data
    private static class NominatimResult {
        @JsonAlias("lat") String lat;
        @JsonAlias("lon") String lon;
    }
}
