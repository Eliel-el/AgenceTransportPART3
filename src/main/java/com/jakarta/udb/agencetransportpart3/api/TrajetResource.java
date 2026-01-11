package com.jakarta.udb.agencetransportpart3.api;

import com.jakarta.udb.agencetransportpart3.entity.Trajet;
import com.jakarta.udb.agencetransportpart3.service.TrajetService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST API for Trajet management
 */
@Path("/trajets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrajetResource {

    @Inject
    private TrajetService trajetService;

    /**
     * Get all trajets
     */
    @GET
    public Response getAllTrajets() {
        List<Trajet> trajets = trajetService.findAll();
        return Response.ok(trajets).build();
    }

    /**
     * Get trajet by ID
     */
    @GET
    @Path("/{id}")
    public Response getTrajet(@PathParam("id") Long id) {
        Trajet trajet = trajetService.findById(id);
        if (trajet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(trajet).build();
    }

    /**
     * Get trajets by status
     */
    @GET
    @Path("/status/{status}")
    public Response getTrajetsByStatus(@PathParam("status") String status) {
        List<Trajet> trajets = trajetService.findByStatus(status);
        return Response.ok(trajets).build();
    }

    /**
     * Get trajet by reservation ID
     */
    @GET
    @Path("/reservation/{reservationId}")
    public Response getTrajetByReservation(@PathParam("reservationId") Long reservationId) {
        Trajet trajet = trajetService.findByReservationId(reservationId);
        if (trajet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(trajet).build();
    }

    /**
     * Create a new trajet
     */
    @POST
    public Response createTrajet(@QueryParam("reservationId") Long reservationId,
            @QueryParam("busId") Long busId,
            @QueryParam("chauffeurId") Long chauffeurId) {
        try {
            Trajet created = trajetService.createTrajet(reservationId, busId, chauffeurId);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Update a trajet
     */
    @PUT
    @Path("/{id}")
    public Response updateTrajet(@PathParam("id") Long id, Trajet trajet) {
        Trajet existing = trajetService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        trajet.setId(id);
        Trajet updated = trajetService.updateTrajet(trajet);
        return Response.ok(updated).build();
    }

    /**
     * Assign bus to trajet
     */
    @POST
    @Path("/{id}/assign-bus")
    public Response assignBus(@PathParam("id") Long id, @QueryParam("busId") Long busId) {
        trajetService.assignBus(id, busId);
        return Response.ok("{\"message\":\"Bus assigned\"}").build();
    }

    /**
     * Assign chauffeur to trajet
     */
    @POST
    @Path("/{id}/assign-chauffeur")
    public Response assignChauffeur(@PathParam("id") Long id, @QueryParam("chauffeurId") Long chauffeurId) {
        trajetService.assignChauffeur(id, chauffeurId);
        return Response.ok("{\"message\":\"Chauffeur assigned\"}").build();
    }

    /**
     * Start a trajet
     */
    @POST
    @Path("/{id}/start")
    public Response startTrajet(@PathParam("id") Long id) {
        trajetService.startTrajet(id);
        return Response.ok("{\"message\":\"Trajet started\"}").build();
    }

    /**
     * Complete a trajet
     */
    @POST
    @Path("/{id}/complete")
    public Response completeTrajet(@PathParam("id") Long id) {
        trajetService.completeTrajet(id);
        return Response.ok("{\"message\":\"Trajet completed\"}").build();
    }

    /**
     * Cancel a trajet
     */
    @POST
    @Path("/{id}/cancel")
    public Response cancelTrajet(@PathParam("id") Long id) {
        trajetService.cancelTrajet(id);
        return Response.ok("{\"message\":\"Trajet cancelled\"}").build();
    }

    /**
     * Delete a trajet
     */
    @DELETE
    @Path("/{id}")
    public Response deleteTrajet(@PathParam("id") Long id) {
        trajetService.deleteTrajet(id);
        return Response.ok("{\"message\":\"Trajet deleted\"}").build();
    }
}
