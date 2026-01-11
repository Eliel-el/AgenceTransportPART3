package com.jakarta.udb.agencetransportpart3.api;

import com.jakarta.udb.agencetransportpart3.entity.Reservation;
import com.jakarta.udb.agencetransportpart3.service.ReservationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST API for Reservation management
 */
@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    private ReservationService reservationService;

    /**
     * Get all reservations
     */
    @GET
    public Response getAllReservations() {
        List<Reservation> reservations = reservationService.findAll();
        return Response.ok(reservations).build();
    }

    /**
     * Get reservation by ID
     */
    @GET
    @Path("/{id}")
    public Response getReservation(@PathParam("id") Long id) {
        Reservation reservation = reservationService.findById(id);
        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reservation).build();
    }

    /**
     * Get reservations by status
     */
    @GET
    @Path("/status/{status}")
    public Response getReservationsByStatus(@PathParam("status") String status) {
        List<Reservation> reservations = reservationService.findByStatus(status);
        return Response.ok(reservations).build();
    }

    /**
     * Create a new reservation
     */
    @POST
    public Response createReservation(Reservation reservation) {
        try {
            Reservation created = reservationService.createReservation(reservation);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Update a reservation
     */
    @PUT
    @Path("/{id}")
    public Response updateReservation(@PathParam("id") Long id, Reservation reservation) {
        Reservation existing = reservationService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        reservation.setId(id);
        Reservation updated = reservationService.updateReservation(reservation);
        return Response.ok(updated).build();
    }

    /**
     * Confirm a reservation
     */
    @POST
    @Path("/{id}/confirm")
    public Response confirmReservation(@PathParam("id") Long id,
            @QueryParam("busId") Long busId,
            @QueryParam("chauffeurId") Long chauffeurId) {
        boolean confirmed = reservationService.confirmReservation(id, busId, chauffeurId);
        if (confirmed) {
            return Response.ok("{\"message\":\"Reservation confirmed\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Unable to confirm reservation\"}").build();
        }
    }

    /**
     * Cancel a reservation
     */
    @POST
    @Path("/{id}/cancel")
    public Response cancelReservation(@PathParam("id") Long id) {
        reservationService.cancelReservation(id);
        return Response.ok("{\"message\":\"Reservation cancelled\"}").build();
    }

    /**
     * Delete a reservation
     */
    @DELETE
    @Path("/{id}")
    public Response deleteReservation(@PathParam("id") Long id) {
        reservationService.deleteReservation(id);
        return Response.ok("{\"message\":\"Reservation deleted\"}").build();
    }
}
