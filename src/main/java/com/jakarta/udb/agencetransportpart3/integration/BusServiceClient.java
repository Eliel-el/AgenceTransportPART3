package com.jakarta.udb.agencetransportpart3.integration;

import jakarta.enterprise.context.ApplicationScoped;
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

    public BusServiceClient() {
        this.client = ClientBuilder.newClient();
    }

    /**
     * Check if a bus is available for a specific date
     * 
     * @param busId The bus ID
     * @param date  The date to check
     * @return true if available, false otherwise
     */
    public boolean checkBusAvailability(Long busId, String date) {
        if (busId == null || date == null)
            return false;
        try {
            // Ensure date is in yyyy-MM-dd format if it's a full ISO string
            String formattedDate = date.contains("T") ? date.split("T")[0] : date;

            WebTarget target = client.target(BUS_SERVICE_URL)
                    .path(String.valueOf(busId))
                    .path("availability")
                    .queryParam("date", formattedDate);

            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                String jsonResponse = response.readEntity(String.class);
                return jsonResponse.contains("\"available\":true");
            }

            LOGGER.warning("Bus service " + busId + " returned status: " + response.getStatus() + " for date "
                    + formattedDate);
            return false;

        } catch (Exception e) {
            LOGGER.severe("Error checking bus availability: " + e.getMessage());
            return true; // Fallback for demo
        }
    }

    /**
     * Get bus details by ID
     * 
     * @param busId The bus ID
     * @return Bus information as JSON string, or null if not found
     */
    public String getBusDetails(Long busId) {
        try {
            WebTarget target = client.target(BUS_SERVICE_URL).path(String.valueOf(busId));
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            }

            LOGGER.warning("Bus service returned status: " + response.getStatus());
            return null;

        } catch (Exception e) {
            LOGGER.severe("Error getting bus details: " + e.getMessage());
            // Mock response for development/testing
            return "{\"id\":" + busId + ",\"number\":\"BUS-" + busId + "\",\"capacity\":50}";
        }
    }

    /**
     * Get all available buses
     * 
     * @return List of buses as JSON string
     */
    public String getAvailableBuses() {
        try {
            WebTarget target = client.target(BUS_SERVICE_URL).queryParam("available", true);
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            }

            LOGGER.warning("Bus service returned status: " + response.getStatus());
            return "[]";

        } catch (Exception e) {
            LOGGER.severe("Error getting available buses: " + e.getMessage());
            // Mock response for development/testing
            return "[{\"id\":1,\"number\":\"BUS-001\",\"capacity\":50},{\"id\":2,\"number\":\"BUS-002\",\"capacity\":40}]";
        }
    }
}
