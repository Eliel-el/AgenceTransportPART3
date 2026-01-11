package com.jakarta.udb.agencetransportpart3.service;

import com.jakarta.udb.agencetransportpart3.entity.Reservation;
import com.jakarta.udb.agencetransportpart3.entity.Trajet;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service for generating reports
 */
@Stateless
public class ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    @Inject
    private ReservationService reservationService;

    @Inject
    private TrajetService trajetService;

    /**
     * Generate a summary report of all reservations and trajets
     */
    public Map<String, Object> generateSummaryReport() {
        Map<String, Object> report = new HashMap<>();

        // Reservation statistics
        long totalReservations = reservationService.findAll().size();
        long pendingReservations = reservationService.getPendingReservationsCount();
        long confirmedReservations = reservationService.getConfirmedReservationsCount();

        Map<String, Long> reservationStats = new HashMap<>();
        reservationStats.put("total", totalReservations);
        reservationStats.put("pending", pendingReservations);
        reservationStats.put("confirmed", confirmedReservations);

        report.put("reservations", reservationStats);

        // Trajet statistics
        long totalTrajets = trajetService.findAll().size();
        long plannedTrajets = trajetService.getPlannedTrajetsCount();
        long completedTrajets = trajetService.getCompletedTrajetsCount();

        Map<String, Long> trajetStats = new HashMap<>();
        trajetStats.put("total", totalTrajets);
        trajetStats.put("planned", plannedTrajets);
        trajetStats.put("completed", completedTrajets);

        report.put("trajets", trajetStats);

        LOGGER.info("Generated summary report");
        return report;
    }

    /**
     * Generate detailed report by bus
     */
    public Map<Long, List<Trajet>> generateReportByBus() {
        List<Trajet> allTrajets = trajetService.findAll();
        Map<Long, List<Trajet>> reportByBus = new HashMap<>();

        for (Trajet trajet : allTrajets) {
            if (trajet.getBusId() != null) {
                reportByBus.computeIfAbsent(trajet.getBusId(), k -> new java.util.ArrayList<>()).add(trajet);
            }
        }

        LOGGER.info("Generated report by bus");
        return reportByBus;
    }

    /**
     * Generate detailed report by chauffeur
     */
    public Map<Long, List<Trajet>> generateReportByChauffeur() {
        List<Trajet> allTrajets = trajetService.findAll();
        Map<Long, List<Trajet>> reportByChauffeur = new HashMap<>();

        for (Trajet trajet : allTrajets) {
            if (trajet.getChauffeurId() != null) {
                reportByChauffeur.computeIfAbsent(trajet.getChauffeurId(), k -> new java.util.ArrayList<>())
                        .add(trajet);
            }
        }

        LOGGER.info("Generated report by chauffeur");
        return reportByChauffeur;
    }

    /**
     * Get all reservations with their associated trajets
     */
    public Map<Reservation, Trajet> getReservationsWithTrajets() {
        List<Reservation> reservations = reservationService.findAll();
        Map<Reservation, Trajet> result = new HashMap<>();

        for (Reservation reservation : reservations) {
            Trajet trajet = trajetService.findByReservationId(reservation.getId());
            result.put(reservation, trajet);
        }

        LOGGER.info("Retrieved reservations with trajets");
        return result;
    }
}
