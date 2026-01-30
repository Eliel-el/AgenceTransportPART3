package com.jakarta.udb.agencetransportpart3.integration;

import com.jakarta.udb.agencetransportpart3.entity.LocalBus;
import com.jakarta.udb.agencetransportpart3.service.LocalResourceService;
import com.jakarta.udb.agencetransportpart3.config.ServiceConfig;
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
 * REST Client for communicating with Service 1 (Bus Management)
 */
@ApplicationScoped
public class BusServiceClient {

    private static final Logger LOGGER = Logger.getLogger(BusServiceClient.class.getName());
    // Use ServiceConfig to read target URL so it can point to the local API

    private final Client client;
    private final Jsonb jsonb;

    @Inject
    private LocalResourceService localResourceService;

    @Inject
    private ServiceConfig serviceConfig;

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

        // Check if it's a test bus - always available
        if (localResourceService.isTestBusAvailable(busId, date)) {
            LOGGER.info("Test bus " + busId + " is always available");
            return true;
        }

        // Otherwise, check with external service
        try {
            String formattedDate = date.contains("T") ? date.split("T")[0] : date;

                WebTarget target = client.target(serviceConfig.getBusServiceUrl())
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

        // Check if it's a test bus - return local details
        if (localResourceService.isTestBus(busId)) {
            LocalBus bus = localResourceService.getBusById(busId);
            if (bus != null) {
                LOGGER.info("Returning test bus details for ID: " + busId);
                return jsonb.toJson(bus);
            }
        }

        // Otherwise, get from external service
        try {
            WebTarget target = client.target(serviceConfig.getBusServiceUrl()).path(String.valueOf(busId));
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
     * Get all available buses (test buses + external buses)
     */
    public String getAvailableBuses() {
        List<Map<String, Object>> allBuses = new ArrayList<>();

        // Always add test buses first
        for (LocalBus bus : localResourceService.getDefaultBuses()) {
            Map<String, Object> busMap = new HashMap<>();
            busMap.put("id", bus.getId());
            busMap.put("number", bus.getNumber());
            busMap.put("capacity", bus.getCapacity());
            busMap.put("isTest", true);
            allBuses.add(busMap);
        }

        // Try to add external buses
        try {
            WebTarget target = client.target(serviceConfig.getBusServiceUrl()).queryParam("available", true);
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                String externalJson = response.readEntity(String.class);
                // Parse external buses and add them
                if (externalJson != null && externalJson.startsWith("[") && externalJson.length() > 2) {
                    // Simple parsing - in production use proper JSON parsing
                    String content = externalJson.substring(1, externalJson.length() - 1);
                    if (!content.trim().isEmpty()) {
                        String[] items = content.split("\\},\\{");
                        for (String item : items) {
                            Map<String, Object> busMap = new HashMap<>();
                            // Extract id, number, capacity from JSON string
                            String cleanItem = item.replace("{", "").replace("}", "");
                            String[] fields = cleanItem.split(",");
                            for (String field : fields) {
                                String[] keyValue = field.split(":");
                                if (keyValue.length == 2) {
                                    String key = keyValue[0].trim().replace("\"", "");
                                    String value = keyValue[1].trim().replace("\"", "");
                                    if (key.equals("id")) {
                                        busMap.put("id", Long.parseLong(value));
                                    } else if (key.equals("number")) {
                                        busMap.put("number", value);
                                    } else if (key.equals("capacity")) {
                                        busMap.put("capacity", Integer.parseInt(value));
                                    }
                                }
                            }
                            busMap.put("isTest", false);
                            if (busMap.containsKey("id")) {
                                allBuses.add(busMap);
                            }
                        }
                    }
                }
                LOGGER.info("Loaded " + (allBuses.size() - localResourceService.getDefaultBuses().size())
                        + " external buses");
            } else {
                LOGGER.warning("Bus list service returned " + response.getStatus() + ", using test buses only");
            }

        } catch (Exception e) {
            LOGGER.warning("Error getting external buses: " + e.getMessage() + ", using test buses only");
        }

        return jsonb.toJson(allBuses);
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
