package com.jakarta.udb.agencetransportpart3.api;

import com.jakarta.udb.agencetransportpart3.service.ReportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST API for Reports
 */
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

    @Inject
    private ReportService reportService;

    /**
     * Get summary report
     */
    @GET
    @Path("/summary")
    public Response getSummaryReport() {
        return Response.ok(reportService.generateSummaryReport()).build();
    }

    /**
     * Get report by bus
     */
    @GET
    @Path("/by-bus")
    public Response getReportByBus() {
        return Response.ok(reportService.generateReportByBus()).build();
    }

    /**
     * Get report by chauffeur
     */
    @GET
    @Path("/by-chauffeur")
    public Response getReportByChauffeur() {
        return Response.ok(reportService.generateReportByChauffeur()).build();
    }

    /**
     * Get reservations with trajets
     */
    @GET
    @Path("/reservations-with-trajets")
    public Response getReservationsWithTrajets() {
        return Response.ok(reportService.getReservationsWithTrajets()).build();
    }
}
