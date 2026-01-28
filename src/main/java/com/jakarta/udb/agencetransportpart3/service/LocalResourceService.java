package com.jakarta.udb.agencetransportpart3.service;

import com.jakarta.udb.agencetransportpart3.entity.LocalBus;
import com.jakarta.udb.agencetransportpart3.entity.LocalChauffeur;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for managing local test resources (buses and chauffeurs)
 * These resources are always available, even when external services are down
 */
@ApplicationScoped
public class LocalResourceService {

    // Default test buses with negative IDs to avoid conflicts with external
    // services
    private static final List<LocalBus> DEFAULT_BUSES = Arrays.asList(
            new LocalBus(-1L, "BUS-TEST-001", 50, true),
            new LocalBus(-2L, "BUS-TEST-002", 30, true));

    // Default test chauffeurs with negative IDs
    private static final List<LocalChauffeur> DEFAULT_CHAUFFEURS = Arrays.asList(
            new LocalChauffeur(-1L, "Jean Dupont (Test)", "TEST-LIC-001", true),
            new LocalChauffeur(-2L, "Marie Martin (Test)", "TEST-LIC-002", true));

    /**
     * Get all default test buses
     */
    public List<LocalBus> getDefaultBuses() {
        return new ArrayList<>(DEFAULT_BUSES);
    }

    /**
     * Get all default test chauffeurs
     */
    public List<LocalChauffeur> getDefaultChauffeurs() {
        return new ArrayList<>(DEFAULT_CHAUFFEURS);
    }

    /**
     * Get a specific bus by ID
     */
    public LocalBus getBusById(Long id) {
        return DEFAULT_BUSES.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a specific chauffeur by ID
     */
    public LocalChauffeur getChauffeurById(Long id) {
        return DEFAULT_CHAUFFEURS.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if a bus ID corresponds to a test resource
     * Test resources have negative IDs
     */
    public boolean isTestBus(Long busId) {
        return busId != null && busId < 0;
    }

    /**
     * Check if a chauffeur ID corresponds to a test resource
     * Test resources have negative IDs
     */
    public boolean isTestChauffeur(Long chauffeurId) {
        return chauffeurId != null && chauffeurId < 0;
    }

    /**
     * Test buses are always available
     */
    public boolean isTestBusAvailable(Long busId, String date) {
        return isTestBus(busId);
    }

    /**
     * Test chauffeurs are always available
     */
    public boolean isTestChauffeurAvailable(Long chauffeurId, String date) {
        return isTestChauffeur(chauffeurId);
    }
}
