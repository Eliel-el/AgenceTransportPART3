package com.jakarta.udb.agencetransportpart3.service;

import com.jakarta.udb.agencetransportpart3.entity.Trajet;
import com.jakarta.udb.agencetransportpart3.entity.Reservation;
import com.jakarta.udb.agencetransportpart3.integration.BusServiceClient;
import com.jakarta.udb.agencetransportpart3.integration.ChauffeurServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service for managing Trajets (Trips) using JSON persistence
 */
@ApplicationScoped
public class TrajetService {

    private static final Logger LOGGER = Logger.getLogger(TrajetService.class.getName());

    @Inject
    private JsonPersistenceService persistenceService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private BusServiceClient busServiceClient;

    @Inject
    private ChauffeurServiceClient chauffeurServiceClient;

    /**
     * Create a new trajet from a reservation
     */
    public Trajet createTrajet(Long reservationId, Long busId, Long chauffeurId) {
        // Get reservation details
        Reservation reservation = reservationService.findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        List<Trajet> all = findAll();

        // Manual ID generation
        long nextId = all.stream()
                .mapToLong(Trajet::getId)
                .max()
                .orElse(0L) + 1;

        Trajet trajet = new Trajet();
        trajet.setId(nextId);
        trajet.setReservationId(reservationId);
        trajet.setDepartureLocation(reservation.getDepartureLocation());
        trajet.setDestinationLocation(reservation.getDestinationLocation());
        trajet.setDepartureDate(reservation.getDepartureDate());
        trajet.setStatus("PLANNED");
        trajet.setCreatedAt(LocalDateTime.now());
        trajet.setUpdatedAt(LocalDateTime.now());

        // Get bus details from Service 1
        if (busId != null) {
            trajet.setBusId(busId);
            try {
                String busDetails = busServiceClient.getBusDetails(busId);
                if (busDetails != null && busDetails.contains("\"number\":\"")) {
                    String number = busDetails.split("\"number\":\"")[1].split("\"")[0];
                    trajet.setBusNumber(number);
                } else {
                    trajet.setBusNumber("Bus #" + busId + " (Indisponible)");
                }
            } catch (Exception e) {
                trajet.setBusNumber("Bus #" + busId + " (Indisponible)");
            }
        }

        // Get chauffeur details from Service 2
        if (chauffeurId != null) {
            trajet.setChauffeurId(chauffeurId);
            try {
                String chauffeurDetails = chauffeurServiceClient.getChauffeurDetails(chauffeurId);
                if (chauffeurDetails != null && chauffeurDetails.contains("\"name\":\"")) {
                    String name = chauffeurDetails.split("\"name\":\"")[1].split("\"")[0];
                    trajet.setChauffeurName(name);
                } else {
                    trajet.setChauffeurName("Chauffeur #" + chauffeurId + " (Indisponible)");
                }
            } catch (Exception e) {
                trajet.setChauffeurName("Chauffeur #" + chauffeurId + " (Indisponible)");
            }
        }

        all.add(trajet);
        persistenceService.saveAll(all, Trajet.class);

        LOGGER.info("Created trajet in JSON: " + trajet.getId());
        return trajet;
    }

    /**
     * Find trajet by ID
     */
    public Trajet findById(Long id) {
        return findAll().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get all trajets
     */
    public List<Trajet> findAll() {
        return persistenceService.loadAll(Trajet.class);
    }

    /**
     * Get trajets by status
     */
    public List<Trajet> findByStatus(String status) {
        return findAll().stream()
                .filter(t -> t.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    /**
     * Get trajet by reservation ID
     */
    public Trajet findByReservationId(Long reservationId) {
        return findAll().stream()
                .filter(t -> t.getReservationId().equals(reservationId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Update trajet
     */
    public Trajet updateTrajet(Trajet trajet) {
        List<Trajet> all = findAll();
        trajet.setUpdatedAt(LocalDateTime.now());

        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(trajet.getId())) {
                all.set(i, trajet);
                updated = true;
                break;
            }
        }

        if (updated) {
            persistenceService.saveAll(all, Trajet.class);
            LOGGER.info("Updated trajet in JSON: " + trajet.getId());
        }

        return trajet;
    }

    /**
     * Assign bus to trajet
     */
    public void assignBus(Long trajetId, Long busId) {
        Trajet trajet = findById(trajetId);
        if (trajet != null) {
            trajet.setBusId(busId);
            try {
                String busDetails = busServiceClient.getBusDetails(busId);
                if (busDetails != null && busDetails.contains("\"number\":\"")) {
                    String number = busDetails.split("\"number\":\"")[1].split("\"")[0];
                    trajet.setBusNumber(number);
                } else {
                    trajet.setBusNumber("Bus #" + busId + " (Indisponible)");
                }
            } catch (Exception e) {
                trajet.setBusNumber("Bus #" + busId + " (Indisponible)");
            }
            updateTrajet(trajet);
            LOGGER.info("Assigned bus " + busId + " to trajet " + trajetId);
        }
    }

    /**
     * Assign chauffeur to trajet
     */
    public void assignChauffeur(Long trajetId, Long chauffeurId) {
        Trajet trajet = findById(trajetId);
        if (trajet != null) {
            trajet.setChauffeurId(chauffeurId);
            try {
                String chauffeurDetails = chauffeurServiceClient.getChauffeurDetails(chauffeurId);
                if (chauffeurDetails != null && chauffeurDetails.contains("\"name\":\"")) {
                    String name = chauffeurDetails.split("\"name\":\"")[1].split("\"")[0];
                    trajet.setChauffeurName(name);
                } else {
                    trajet.setChauffeurName("Chauffeur #" + chauffeurId + " (Indisponible)");
                }
            } catch (Exception e) {
                trajet.setChauffeurName("Chauffeur #" + chauffeurId + " (Indisponible)");
            }
            updateTrajet(trajet);
            LOGGER.info("Assigned chauffeur " + chauffeurId + " to trajet " + trajetId);
        }
    }

    /**
     * Start a trajet (change status to IN_PROGRESS)
     */
    public void startTrajet(Long id) {
        Trajet trajet = findById(id);
        if (trajet != null) {
            trajet.setStatus("IN_PROGRESS");
            updateTrajet(trajet);
            LOGGER.info("Started trajet: " + id);
        }
    }

    /**
     * Complete a trajet
     */
    public void completeTrajet(Long id) {
        Trajet trajet = findById(id);
        if (trajet != null) {
            trajet.setStatus("COMPLETED");
            trajet.setArrivalDate(LocalDateTime.now());
            updateTrajet(trajet);
            LOGGER.info("Completed trajet: " + id);
        }
    }

    /**
     * Cancel a trajet
     */
    public void cancelTrajet(Long id) {
        Trajet trajet = findById(id);
        if (trajet != null) {
            trajet.setStatus("CANCELLED");
            updateTrajet(trajet);
            LOGGER.info("Cancelled trajet: " + id);
        }
    }

    /**
     * Delete a trajet
     */
    public void deleteTrajet(Long id) {
        List<Trajet> all = findAll();
        boolean removed = all.removeIf(t -> t.getId().equals(id));

        if (removed) {
            persistenceService.saveAll(all, Trajet.class);
            LOGGER.info("Deleted trajet from JSON: " + id);
        }
    }

    /**
     * Get planned trajets count
     */
    public long getPlannedTrajetsCount() {
        return findAll().stream()
                .filter(t -> "PLANNED".equalsIgnoreCase(t.getStatus()))
                .count();
    }

    /**
     * Get completed trajets count
     */
    public long getCompletedTrajetsCount() {
        return findAll().stream()
                .filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus()))
                .count();
    }
}
