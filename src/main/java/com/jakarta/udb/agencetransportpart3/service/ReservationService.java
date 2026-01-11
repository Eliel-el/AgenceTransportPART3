package com.jakarta.udb.agencetransportpart3.service;

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
 * Service for managing Reservations
 */
@Stateless
public class ReservationService {

    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Inject
    private BusServiceClient busServiceClient;

    @Inject
    private ChauffeurServiceClient chauffeurServiceClient;

    /**
     * Create a new reservation
     */
    public Reservation createReservation(Reservation reservation) {
        reservation.setStatus("PENDING");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        em.persist(reservation);
        LOGGER.info("Created reservation: " + reservation.getId());
        return reservation;
    }

    /**
     * Find reservation by ID
     */
    public Reservation findById(Long id) {
        return em.find(Reservation.class, id);
    }

    /**
     * Get all reservations
     */
    public List<Reservation> findAll() {
        return em.createNamedQuery("Reservation.findAll", Reservation.class).getResultList();
    }

    /**
     * Get reservations by status
     */
    public List<Reservation> findByStatus(String status) {
        return em.createNamedQuery("Reservation.findByStatus", Reservation.class)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * Update reservation
     */
    public Reservation updateReservation(Reservation reservation) {
        reservation.setUpdatedAt(LocalDateTime.now());
        Reservation updated = em.merge(reservation);
        LOGGER.info("Updated reservation: " + updated.getId());
        return updated;
    }

    /**
     * Confirm a reservation (check availability with external services)
     */
    public boolean confirmReservation(Long reservationId, Long busId, Long chauffeurId) {
        Reservation reservation = findById(reservationId);
        if (reservation == null) {
            LOGGER.warning("Reservation not found: " + reservationId);
            return false;
        }

        // Check bus availability
        String dateStr = reservation.getDepartureDate().toString();
        boolean busAvailable = busServiceClient.checkBusAvailability(busId, dateStr);

        if (!busAvailable) {
            LOGGER.warning("Bus not available: " + busId);
            return false;
        }

        // Check chauffeur availability
        boolean chauffeurAvailable = chauffeurServiceClient.checkChauffeurAvailability(chauffeurId, dateStr);

        if (!chauffeurAvailable) {
            LOGGER.warning("Chauffeur not available: " + chauffeurId);
            return false;
        }

        // Confirm reservation
        reservation.setStatus("CONFIRMED");
        updateReservation(reservation);

        LOGGER.info("Confirmed reservation: " + reservationId);
        return true;
    }

    /**
     * Cancel a reservation
     */
    public void cancelReservation(Long id) {
        Reservation reservation = findById(id);
        if (reservation != null) {
            reservation.setStatus("CANCELLED");
            updateReservation(reservation);
            LOGGER.info("Cancelled reservation: " + id);
        }
    }

    /**
     * Delete a reservation
     */
    public void deleteReservation(Long id) {
        Reservation reservation = findById(id);
        if (reservation != null) {
            em.remove(reservation);
            LOGGER.info("Deleted reservation: " + id);
        }
    }

    /**
     * Get pending reservations count
     */
    public long getPendingReservationsCount() {
        return em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.status = 'PENDING'", Long.class)
                .getSingleResult();
    }

    /**
     * Get confirmed reservations count
     */
    public long getConfirmedReservationsCount() {
        return em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.status = 'CONFIRMED'", Long.class)
                .getSingleResult();
    }
}
