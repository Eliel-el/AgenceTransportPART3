package com.jakarta.udb.agencetransportpart3.service;

import com.jakarta.udb.agencetransportpart3.entity.Reservation;
import com.jakarta.udb.agencetransportpart3.entity.Trajet;
import com.jakarta.udb.agencetransportpart3.integration.BusServiceClient;
import com.jakarta.udb.agencetransportpart3.integration.ChauffeurServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service for generating reports (JSON-based persistence)
 */
@ApplicationScoped
public class ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    @Inject
    private ReservationService reservationService;

    @Inject
    private TrajetService trajetService;

    @Inject
    private BusServiceClient busServiceClient;

    @Inject
    private ChauffeurServiceClient chauffeurServiceClient;

    // ==============================
    // SUMMARY REPORT
    // ==============================
    public Map<String, Object> generateSummaryReport() {

        Map<String, Object> report = new HashMap<>();

        // Reservations
        Map<String, Long> reservationStats = new HashMap<>();
        reservationStats.put("total",
                (long) reservationService.findAll().size());
        reservationStats.put("pending",
                reservationService.getPendingReservationsCount());
        reservationStats.put("confirmed",
                reservationService.getConfirmedReservationsCount());

        report.put("reservations", reservationStats);

        // Trajets
        Map<String, Long> trajetStats = new HashMap<>();
        trajetStats.put("total",
                (long) trajetService.findAll().size());
        trajetStats.put("planned",
                trajetService.getPlannedTrajetsCount());
        trajetStats.put("completed",
                trajetService.getCompletedTrajetsCount());

        report.put("trajets", trajetStats);

        LOGGER.info("Summary report generated");
        return report;
    }

    // ==============================
    // REPORT BY BUS
    // ==============================
    public Map<String, List<Trajet>> generateReportByBus() {
        Map<String, List<Trajet>> report = new HashMap<>();
        Map<Long, String> busNames = new HashMap<>();

        for (Trajet trajet : trajetService.findAll()) {
            if (trajet.getBusId() != null) {
                String busIdentifier = busNames.get(trajet.getBusId());
                if (busIdentifier == null) {
                    try {
                        String details = busServiceClient.getBusDetails(trajet.getBusId());
                        if (details != null && details.contains("\"number\":\"")) {
                            busIdentifier = details.split("\"number\":\"")[1].split("\"")[0];
                        } else {
                            busIdentifier = "Bus #" + trajet.getBusId() + " (Indisponible)";
                        }
                    } catch (Exception e) {
                        busIdentifier = "Bus #" + trajet.getBusId() + " (Indisponible)";
                    }
                    busNames.put(trajet.getBusId(), busIdentifier);
                }

                report.computeIfAbsent(
                        busIdentifier,
                        k -> new java.util.ArrayList<>()).add(trajet);
            }
        }

        LOGGER.info("Report by bus generated");
        return report;
    }

    // ==============================
    // REPORT BY CHAUFFEUR
    // ==============================
    public Map<String, List<Trajet>> generateReportByChauffeur() {
        Map<String, List<Trajet>> report = new HashMap<>();
        Map<Long, String> chauffeurNames = new HashMap<>();

        for (Trajet trajet : trajetService.findAll()) {
            if (trajet.getChauffeurId() != null) {
                String chauffeurIdentifier = chauffeurNames.get(trajet.getChauffeurId());
                if (chauffeurIdentifier == null) {
                    try {
                        String details = chauffeurServiceClient.getChauffeurDetails(trajet.getChauffeurId());
                        if (details != null && details.contains("\"name\":\"")) {
                            chauffeurIdentifier = details.split("\"name\":\"")[1].split("\"")[0];
                        } else {
                            chauffeurIdentifier = "Chauffeur #" + trajet.getChauffeurId() + " (Indisponible)";
                        }
                    } catch (Exception e) {
                        chauffeurIdentifier = "Chauffeur #" + trajet.getChauffeurId() + " (Indisponible)";
                    }
                    chauffeurNames.put(trajet.getChauffeurId(), chauffeurIdentifier);
                }

                report.computeIfAbsent(
                        chauffeurIdentifier,
                        k -> new java.util.ArrayList<>()).add(trajet);
            }
        }

        LOGGER.info("Report by chauffeur generated");
        return report;
    }

    // ==============================
    // RESERVATIONS + TRAJETS
    // ==============================
    public Map<Reservation, Trajet> getReservationsWithTrajets() {

        Map<Reservation, Trajet> result = new HashMap<>();

        for (Reservation reservation : reservationService.findAll()) {
            Trajet trajet = trajetService.findByReservationId(reservation.getId());
            result.put(reservation, trajet);
        }

        LOGGER.info("Reservations with trajets loaded");
        return result;
    }
}
