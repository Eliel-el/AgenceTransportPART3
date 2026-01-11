package com.jakarta.udb.agencetransportpart3.service;

import com.jakarta.udb.agencetransportpart3.entity.Trajet;
import com.jakarta.udb.agencetransportpart3.entity.Reservation;
import com.jakarta.udb.agencetransportpart3.integration.BusServiceClient;
import com.jakarta.udb.agencetransportpart3.integration.ChauffeurServiceClient;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service for managing Trajets (Trips)
 */
@Stateless
public class TrajetService {

    private static final Logger LOGGER = Logger.getLogger(TrajetService.class.getName());

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Inject
    private BusServiceClient busServiceClient;

    @Inject
    private ChauffeurServiceClient chauffeurServiceClient;

    /**
     * Create a new trajet from a reservation
     */
    public Trajet createTrajet(Long reservationId, Long busId, Long chauffeurId) {
        // Get reservation details
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        Trajet trajet = new Trajet();
        trajet.setReservationId(reservationId);
        trajet.setDepartureLocation(reservation.getDepartureLocation());
        trajet.setDestinationLocation(reservation.getDestinationLocation());
        trajet.setDepartureDate(reservation.getDepartureDate());
        trajet.setStatus("PLANNED");

        // Get bus details from Service 1
        if (busId != null) {
            trajet.setBusId(busId);
            String busDetails = busServiceClient.getBusDetails(busId);
            if (busDetails != null && busDetails.contains("\"number\":\"")) {
                String number = busDetails.split("\"number\":\"")[1].split("\"")[0];
                trajet.setBusNumber(number);
            } else {
                trajet.setBusNumber("BUS-" + busId);
            }
        }

        // Get chauffeur details from Service 2
        if (chauffeurId != null) {
            trajet.setChauffeurId(chauffeurId);
            String chauffeurDetails = chauffeurServiceClient.getChauffeurDetails(chauffeurId);
            if (chauffeurDetails != null && chauffeurDetails.contains("\"name\":\"")) {
                String name = chauffeurDetails.split("\"name\":\"")[1].split("\"")[0];
                trajet.setChauffeurName(name);
            } else {
                trajet.setChauffeurName("Driver-" + chauffeurId);
            }
        }

        em.persist(trajet);
        LOGGER.info("Created trajet: " + trajet.getId());
        return trajet;
    }

    /**
     * Find trajet by ID
     */
    public Trajet findById(Long id) {
        return em.find(Trajet.class, id);
    }

    /**
     * Get all trajets
     */
    public List<Trajet> findAll() {
        return em.createNamedQuery("Trajet.findAll", Trajet.class).getResultList();
    }

    /**
     * Get trajets by status
     */
    public List<Trajet> findByStatus(String status) {
        return em.createNamedQuery("Trajet.findByStatus", Trajet.class)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * Get trajet by reservation ID
     */
    public Trajet findByReservationId(Long reservationId) {
        List<Trajet> trajets = em.createNamedQuery("Trajet.findByReservationId", Trajet.class)
                .setParameter("reservationId", reservationId)
                .getResultList();
        return trajets.isEmpty() ? null : trajets.get(0);
    }

    /**
     * Update trajet
     */
    public Trajet updateTrajet(Trajet trajet) {
        trajet.setUpdatedAt(LocalDateTime.now());
        Trajet updated = em.merge(trajet);
        LOGGER.info("Updated trajet: " + updated.getId());
        return updated;
    }

    /**
     * Assign bus to trajet
     */
    public void assignBus(Long trajetId, Long busId) {
        Trajet trajet = findById(trajetId);
        if (trajet != null) {
            trajet.setBusId(busId);
            String busDetails = busServiceClient.getBusDetails(busId);
            trajet.setBusNumber("BUS-" + busId);
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
            String chauffeurDetails = chauffeurServiceClient.getChauffeurDetails(chauffeurId);
            trajet.setChauffeurName("Driver-" + chauffeurId);
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
        Trajet trajet = findById(id);
        if (trajet != null) {
            em.remove(trajet);
            LOGGER.info("Deleted trajet: " + id);
        }
    }

    /**
     * Get planned trajets count
     */
    public long getPlannedTrajetsCount() {
        return em.createQuery("SELECT COUNT(t) FROM Trajet t WHERE t.status = 'PLANNED'", Long.class)
                .getSingleResult();
    }

    /**
     * Get completed trajets count
     */
    public long getCompletedTrajetsCount() {
        return em.createQuery("SELECT COUNT(t) FROM Trajet t WHERE t.status = 'COMPLETED'", Long.class)
                .getSingleResult();
    }
}
