package com.jakarta.udb.agencetransportpart3.service;

import com.jakarta.udb.agencetransportpart3.entity.Bus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple in-app bus service using JSON persistence
 */
@ApplicationScoped
public class BusService {

    private static final Logger LOGGER = Logger.getLogger(BusService.class.getName());

    @Inject
    private JsonPersistenceService persistenceService;

    /**
     * Get all buses
     */
    public List<Bus> findAll() {
        return persistenceService.loadAll(Bus.class);
    }

    /**
     * Get available buses (etat == "DISPONIBLE")
     */
    public List<Bus> findAvailable() {
        List<Bus> all = findAll();
        List<Bus> result = new ArrayList<>();
        for (Bus b : all) {
            if (b.getEtat() != null && b.getEtat().equalsIgnoreCase("DISPONIBLE")) {
                result.add(b);
            }
        }
        return result;
    }

    public Bus findById(Long id) {
        return findAll().stream()
                .filter(b -> b.getId() != null && b.getId().equals(id))
                .findFirst().orElse(null);
    }

    public Bus create(Bus bus) {
        List<Bus> all = findAll();
        long nextId = all.stream().mapToLong(b -> b.getId() == null ? 0L : b.getId()).max().orElse(0L) + 1;
        bus.setId(nextId);
        if (bus.getNumber() == null && bus.getModele() != null) {
            bus.setNumber(bus.getModele());
        }
        all.add(bus);
        persistenceService.saveAll(all, Bus.class);
        LOGGER.info("Created bus id=" + bus.getId());
        return bus;
    }

    public Bus update(Long id, Bus bus) {
        List<Bus> all = findAll();
        bus.setId(id);
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() != null && all.get(i).getId().equals(id)) {
                all.set(i, bus);
                found = true;
                break;
            }
        }
        if (found) {
            persistenceService.saveAll(all, Bus.class);
            LOGGER.info("Updated bus id=" + id);
            return bus;
        }
        return null;
    }

    public boolean delete(Long id) {
        List<Bus> all = findAll();
        boolean removed = all.removeIf(b -> b.getId() != null && b.getId().equals(id));
        if (removed) {
            persistenceService.saveAll(all, Bus.class);
            LOGGER.info("Deleted bus id=" + id);
        }
        return removed;
    }
}
