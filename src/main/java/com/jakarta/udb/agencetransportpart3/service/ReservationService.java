package com.jakarta.udb.agencetransportpart3.service;

import com.jakarta.udb.agencetransportpart3.entity.Reservation;
import com.jakarta.udb.agencetransportpart3.entity.Trajet;
import com.jakarta.udb.agencetransportpart3.integration.BusServiceClient;
import com.jakarta.udb.agencetransportpart3.integration.ChauffeurServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReservationService {

    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());

    @Inject
    private JsonPersistenceService persistenceService;

    @Inject
    private BusServiceClient busServiceClient;

    @Inject
    private ChauffeurServiceClient chauffeurServiceClient;

    @Inject
    private TrajetService trajetService;

    // ==============================
    // FIND ALL
    // ==============================
    public List<Reservation> findAll() {
        return persistenceService.loadAll(Reservation.class);
    }

    // ==============================
    // CREATE
    // ==============================
    public Reservation createReservation(Reservation reservation) {

        List<Reservation> all = findAll();

        long nextId = all.stream()
                .mapToLong(Reservation::getId)
                .max()
                .orElse(0L) + 1;

        reservation.setId(nextId);
        reservation.setStatus("PENDING");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        all.add(reservation);
        persistenceService.saveAll(all, Reservation.class);

        LOGGER.info("Reservation created: " + nextId);
        return reservation;
    }

    // ==============================
    // FIND BY ID
    // ==============================
    public Reservation findById(Long id) {
        return findAll().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // ==============================
    // UPDATE
    // ==============================
    public Reservation updateReservation(Reservation reservation) {

        List<Reservation> all = findAll();
        reservation.setUpdatedAt(LocalDateTime.now());

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(reservation.getId())) {
                all.set(i, reservation);
                persistenceService.saveAll(all, Reservation.class);
                LOGGER.info("Reservation updated: " + reservation.getId());
                return reservation;
            }
        }

        LOGGER.warning("Reservation not found for update: " + reservation.getId());
        return null;
    }

    // ==============================
    // FIND BY STATUS
    // ==============================
    public List<Reservation> findByStatus(String status) {
        return findAll().stream()
                .filter(r -> r.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    // ==============================
    // CONFIRM
    // ==============================
    public boolean confirmReservation(Long reservationId,
            Long busId,
            Long chauffeurId) {

        Reservation reservation = findById(reservationId);
        if (reservation == null) {
            return false;
        }

        String dateStr = reservation.getDepartureDate().toString();

        // If reservation is already linked to a trajet, use that trajet's info or
        // update it
        if (reservation.getTrajetId() != null) {
            Trajet trajet = trajetService.findById(reservation.getTrajetId());
            if (trajet != null) {
                trajet.setBusId(busId);
                trajet.setChauffeurId(chauffeurId);
                trajetService.updateTrajet(trajet);
            }
        }

        if (!busServiceClient.checkBusAvailability(busId, dateStr)) {
            return false;
        }

        if (!chauffeurServiceClient.checkChauffeurAvailability(chauffeurId, dateStr)) {
            return false;
        }

        reservation.setStatus("CONFIRMED");
        updateReservation(reservation);

        LOGGER.info("Reservation confirmed: " + reservationId);
        return true;
    }

    // ==============================
    // CANCEL
    // ==============================
    public void cancelReservation(Long id) {
        Reservation reservation = findById(id);
        if (reservation != null) {
            reservation.setStatus("CANCELLED");
            updateReservation(reservation);
        }
    }

    // ==============================
    // DELETE
    // ==============================
    public void deleteReservation(Long id) {
        List<Reservation> all = findAll();
        boolean removed = all.removeIf(r -> r.getId().equals(id));

        if (removed) {
            persistenceService.saveAll(all, Reservation.class);
            LOGGER.info("Reservation deleted: " + id);
        }
    }

    // ==============================
    // STATS
    // ==============================
    public long getPendingReservationsCount() {
        return findAll().stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()))
                .count();
    }

    public long getConfirmedReservationsCount() {
        return findAll().stream()
                .filter(r -> "CONFIRMED".equalsIgnoreCase(r.getStatus()))
                .count();
    }
}
