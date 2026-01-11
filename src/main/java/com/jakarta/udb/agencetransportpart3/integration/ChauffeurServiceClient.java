package com.jakarta.udb.agencetransportpart3.integration;

import jakarta.enterprise.context.ApplicationScoped;
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

    public ChauffeurServiceClient() {
        this.client = ClientBuilder.newClient();
    }

    /**
     * Check if a chauffeur is available for a specific date
     * 
     * @param chauffeurId The chauffeur ID
     * @param date        The date to check
     * @return true if available, false otherwise
     */
    public boolean checkChauffeurAvailability(Long chauffeurId, String date) {
        if (chauffeurId == null || date == null)
            return false;
        try {
            // Ensure date is in yyyy-MM-dd format if it's a full ISO string
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

            LOGGER.warning("Chauffeur service " + chauffeurId + " returned status: " + response.getStatus()
                    + " for date " + formattedDate);
            return false;

        } catch (Exception e) {
            LOGGER.severe("Error checking chauffeur availability: " + e.getMessage());
            return true; // Fallback for demo
        }
    }

    /**
     * Get chauffeur details by ID
     * 
     * @param chauffeurId The chauffeur ID
     * @return Chauffeur information as JSON string, or null if not found
     */
    public String getChauffeurDetails(Long chauffeurId) {
        try {
            WebTarget target = client.target(CHAUFFEUR_SERVICE_URL).path(String.valueOf(chauffeurId));
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            }

            LOGGER.warning("Chauffeur service returned status: " + response.getStatus());
            return null;

        } catch (Exception e) {
            LOGGER.severe("Error getting chauffeur details: " + e.getMessage());
            // Mock response for development/testing
            return "{\"id\":" + chauffeurId + ",\"name\":\"Driver-" + chauffeurId + "\",\"license\":\"LIC-"
                    + chauffeurId + "\"}";
        }
    }

    /**
     * Get all available chauffeurs
     * 
     * @return List of chauffeurs as JSON string
     */
    public String getAvailableChauffeurs() {
        try {
            WebTarget target = client.target(CHAUFFEUR_SERVICE_URL).queryParam("available", true);
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            }

            LOGGER.warning("Chauffeur service returned status: " + response.getStatus());
            return "[]";

        } catch (Exception e) {
            LOGGER.severe("Error getting available chauffeurs: " + e.getMessage());
            // Mock response for development/testing
            return "[{\"id\":1,\"name\":\"Jean Dupont\",\"license\":\"LIC-001\"},{\"id\":2,\"name\":\"Marie Martin\",\"license\":\"LIC-002\"}]";
        }
    }
}
