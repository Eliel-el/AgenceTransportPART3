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
 * REST Client for communicating with Service 1 (Bus Management)
 */
@ApplicationScoped
public class BusServiceClient {

    private static final Logger LOGGER = Logger.getLogger(BusServiceClient.class.getName());
    private static final String BUS_SERVICE_URL = "http://localhost:8080/AgenceTransportPART1/api/bus";

    private final Client client;
    private final Jsonb jsonb;

    public BusServiceClient() {
        this.client = ClientBuilder.newClient();
        this.jsonb = JsonbBuilder.create();
    }

    /**
     * Check if a bus is available for a specific date
     */
    public boolean checkBusAvailability(Long busId, String date) {
        if (busId == null || date == null)
            return false;
        try {
            String formattedDate = date.contains("T") ? date.split("T")[0] : date;

            WebTarget target = client.target(BUS_SERVICE_URL)
                    .path(String.valueOf(busId))
                    .path("availability")
                    .queryParam("date", formattedDate);

            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                String jsonResponse = response.readEntity(String.class);
                // Simple check for boolean field in JSON
                return jsonResponse.contains("\"available\":true");
            }

            LOGGER.warning("Bus availability service returned " + response.getStatus() + " for bus " + busId);
            return false;

        } catch (Exception e) {
            LOGGER.severe("Error checking bus availability: " + e.getMessage());
            return false; // Safer fallback: assume not available if service is down
        }
    }

    /**
     * Get bus details by ID
     */
    public String getBusDetails(Long busId) {
        if (busId == null)
            return null;
        try {
            WebTarget target = client.target(BUS_SERVICE_URL).path(String.valueOf(busId));
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            }

            LOGGER.warning("Bus details service returned " + response.getStatus() + " for bus " + busId);
            return null;

        } catch (Exception e) {
            LOGGER.severe("Error getting bus details: " + e.getMessage());
            return null; // Return null so callers can handle "Indisponible"
        }
    }

    /**
     * Get all available buses
     */
    public String getAvailableBuses() {
        try {
            WebTarget target = client.target(BUS_SERVICE_URL).queryParam("available", true);
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            }

            LOGGER.warning("Bus list service returned " + response.getStatus());
            return "[]";

        } catch (Exception e) {
            LOGGER.severe("Error getting available buses: " + e.getMessage());
            return "[]";
        }
    }

    /**
     * DTO for Bus details if needed for structured binding
     */
    public static class BusDTO {
        public Long id;
        public String number;
        public Integer capacity;
        // getters/setters can be added if needed, or use public fields for simplicity
        // with Jsonb
    }
}
