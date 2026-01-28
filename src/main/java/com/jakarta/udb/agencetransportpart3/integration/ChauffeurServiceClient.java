package com.jakarta.udb.agencetransportpart3.integration;

import com.jakarta.udb.agencetransportpart3.entity.LocalChauffeur;
import com.jakarta.udb.agencetransportpart3.service.LocalResourceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Inject
    private LocalResourceService localResourceService;

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

        // Check if it's a test chauffeur - always available
        if (localResourceService.isTestChauffeurAvailable(chauffeurId, date)) {
            LOGGER.info("Test chauffeur " + chauffeurId + " is always available");
            return true;
        }

        // Otherwise, check with external service
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

        // Check if it's a test chauffeur - return local details
        if (localResourceService.isTestChauffeur(chauffeurId)) {
            LocalChauffeur chauffeur = localResourceService.getChauffeurById(chauffeurId);
            if (chauffeur != null) {
                LOGGER.info("Returning test chauffeur details for ID: " + chauffeurId);
                return jsonb.toJson(chauffeur);
            }
        }

        // Otherwise, get from external service
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
     * Get all available chauffeurs (test chauffeurs + external chauffeurs)
     */
    public String getAvailableChauffeurs() {
        List<Map<String, Object>> allChauffeurs = new ArrayList<>();

        // Always add test chauffeurs first
        for (LocalChauffeur chauffeur : localResourceService.getDefaultChauffeurs()) {
            Map<String, Object> chauffeurMap = new HashMap<>();
            chauffeurMap.put("id", chauffeur.getId());
            chauffeurMap.put("name", chauffeur.getName());
            chauffeurMap.put("license", chauffeur.getLicense());
            chauffeurMap.put("isTest", true);
            allChauffeurs.add(chauffeurMap);
        }

        // Try to add external chauffeurs
        try {
            WebTarget target = client.target(CHAUFFEUR_SERVICE_URL).queryParam("available", true);
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                String externalJson = response.readEntity(String.class);
                // Parse external chauffeurs and add them
                if (externalJson != null && externalJson.startsWith("[") && externalJson.length() > 2) {
                    // Simple parsing - in production use proper JSON parsing
                    String content = externalJson.substring(1, externalJson.length() - 1);
                    if (!content.trim().isEmpty()) {
                        String[] items = content.split("\\},\\{");
                        for (String item : items) {
                            Map<String, Object> chauffeurMap = new HashMap<>();
                            // Extract id, name, license from JSON string
                            String cleanItem = item.replace("{", "").replace("}", "");
                            String[] fields = cleanItem.split(",");
                            for (String field : fields) {
                                String[] keyValue = field.split(":");
                                if (keyValue.length == 2) {
                                    String key = keyValue[0].trim().replace("\"", "");
                                    String value = keyValue[1].trim().replace("\"", "");
                                    if (key.equals("id")) {
                                        chauffeurMap.put("id", Long.parseLong(value));
                                    } else if (key.equals("name")) {
                                        chauffeurMap.put("name", value);
                                    } else if (key.equals("license")) {
                                        chauffeurMap.put("license", value);
                                    }
                                }
                            }
                            chauffeurMap.put("isTest", false);
                            if (chauffeurMap.containsKey("id")) {
                                allChauffeurs.add(chauffeurMap);
                            }
                        }
                    }
                }
                LOGGER.info("Loaded " + (allChauffeurs.size() - localResourceService.getDefaultChauffeurs().size())
                        + " external chauffeurs");
            } else {
                LOGGER.warning(
                        "Chauffeur list service returned " + response.getStatus() + ", using test chauffeurs only");
            }

        } catch (Exception e) {
            LOGGER.warning("Error getting external chauffeurs: " + e.getMessage() + ", using test chauffeurs only");
        }

        return jsonb.toJson(allChauffeurs);
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
