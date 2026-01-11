package com.jakarta.udb.agencetransportpart3.bean;

import com.jakarta.udb.agencetransportpart3.entity.Reservation;
import com.jakarta.udb.agencetransportpart3.entity.Trajet;
import com.jakarta.udb.agencetransportpart3.service.ReportService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.Map;

/**
 * JSF Backing Bean for Reports
 */
@Named
@RequestScoped
public class ReportBean {

    @Inject
    private ReportService reportService;

    private Map<String, Object> summaryReport;
    private Map<String, List<Trajet>> reportByBus;
    private Map<String, List<Trajet>> reportByChauffeur;
    private Map<Reservation, Trajet> reservationsWithTrajets;

    @PostConstruct
    public void init() {
        loadSummaryReport();
    }

    /**
     * Load summary report
     */
    public void loadSummaryReport() {
        summaryReport = reportService.generateSummaryReport();
    }

    /**
     * Load report by bus
     */
    public void loadReportByBus() {
        reportByBus = reportService.generateReportByBus();
    }

    /**
     * Load report by chauffeur
     */
    public void loadReportByChauffeur() {
        reportByChauffeur = reportService.generateReportByChauffeur();
    }

    /**
     * Load reservations with trajets
     */
    public void loadReservationsWithTrajets() {
        reservationsWithTrajets = reportService.getReservationsWithTrajets();
    }

    // Getters and Setters
    public Map<String, Object> getSummaryReport() {
        return summaryReport;
    }

    public void setSummaryReport(Map<String, Object> summaryReport) {
        this.summaryReport = summaryReport;
    }

    public Map<String, List<Trajet>> getReportByBus() {
        if (reportByBus == null) {
            loadReportByBus();
        }
        return reportByBus;
    }

    public void setReportByBus(Map<String, List<Trajet>> reportByBus) {
        this.reportByBus = reportByBus;
    }

    public Map<String, List<Trajet>> getReportByChauffeur() {
        if (reportByChauffeur == null) {
            loadReportByChauffeur();
        }
        return reportByChauffeur;
    }

    public void setReportByChauffeur(Map<String, List<Trajet>> reportByChauffeur) {
        this.reportByChauffeur = reportByChauffeur;
    }

    public Map<Reservation, Trajet> getReservationsWithTrajets() {
        if (reservationsWithTrajets == null) {
            loadReservationsWithTrajets();
        }
        return reservationsWithTrajets;
    }

    public void setReservationsWithTrajets(Map<Reservation, Trajet> reservationsWithTrajets) {
        this.reservationsWithTrajets = reservationsWithTrajets;
    }
}
