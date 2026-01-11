package com.jakarta.udb.agencetransportpart3.integration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * REST Client for communicating with Service 2 (Chauffeur Management)
 */
@ApplicationScoped
public class ChauffeurServiceClient {

    private static final Logger LOGGER = Logger.getLogger(ChauffeurServiceClient.class.getName());
    private static final String CHAUFFEUR_SERVICE_URL = "http://localhost:8080/AgenceTransportPART2/api/chauffeurs";

    private final Client client;
    private final Jsonb jsonb;

    public ChauffeurServiceClient() {
        this.client = ClientBuilder.newClient();
        this.jsonb = JsonbBuilder.create();
    }

    /**
     * Check if a chauffeur is available for a specific date
     */
    public boolean checkChauffeurAvailability(Long chauffeurId, String date) {
        if (chauffeurId == null || date == null)
            return false;
        try {
            String formattedDate = date.contains("T") ? date.split("T")[0] : date;

            WebTarget target = client.target(CHAUFFEUR_SERVICE_URL)
                    .path(String.valueOf(chauffeurId))
                    .path("availability")
                    .queryParam("date", formattedDate);

            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                String jsonResponse = response.readEntity(String.class);
                return jsonResponse.contains("\"available\":true");
            }

            LOGGER.warning("Chauffeur availability service returned " + response.getStatus() + " for chauffeur "
                    + chauffeurId);
            return false;

        } catch (Exception e) {
            LOGGER.severe("Error checking chauffeur availability: " + e.getMessage());
            return false; // Safer fallback: assume not available if service is down
        }
    }

    /**
     * Get chauffeur details by ID
     */
    public String getChauffeurDetails(Long chauffeurId) {
        if (chauffeurId == null)
            return null;
        try {
            WebTarget target = client.target(CHAUFFEUR_SERVICE_URL).path(String.valueOf(chauffeurId));
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            }

            LOGGER.warning(
                    "Chauffeur details service returned " + response.getStatus() + " for chauffeur " + chauffeurId);
            return null;

        } catch (Exception e) {
            LOGGER.severe("Error getting chauffeur details: " + e.getMessage());
            return null; // Return null so callers can handle "Indisponible"
        }
    }

    /**
     * Get all available chauffeurs
     */
    public String getAvailableChauffeurs() {
        try {
            WebTarget target = client.target(CHAUFFEUR_SERVICE_URL).queryParam("available", true);
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            }

            LOGGER.warning("Chauffeur list service returned " + response.getStatus());
            return "[]";

        } catch (Exception e) {
            LOGGER.severe("Error getting available chauffeurs: " + e.getMessage());
            return "[]";
        }
    }

    /**
     * DTO for Chauffeur details if needed for structured binding
     */
    public static class ChauffeurDTO {
        public Long id;
        public String name;
        public String license;
        // getters/setters can be added if needed, or use public fields for simplicity
        // with Jsonb
    }
}
