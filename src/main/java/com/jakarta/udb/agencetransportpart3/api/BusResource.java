package com.jakarta.udb.agencetransportpart3.api;

import com.jakarta.udb.agencetransportpart3.entity.Bus;
import com.jakarta.udb.agencetransportpart3.service.BusService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * REST endpoints for Bus management
 */
@Path("/bus")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BusResource {

    @Inject
    private BusService busService;

    @GET
    public Response getAll() {
        List<Bus> list = busService.findAll();
        return Response.ok(list).build();
    }

    @GET
    @Path("/disponibles")
    public Response getAvailable() {
        List<Bus> list = busService.findAvailable();
        return Response.ok(list).build();
    }

    @POST
    public Response create(Map<String, Object> payload) {
        // Accept both French and English field names
        Bus bus = new Bus();
        if (payload.containsKey("id")) {
            try { bus.setId(Long.parseLong(payload.get("id").toString())); } catch (Exception ignored) {}
        }
        if (payload.containsKey("numero")) {
            bus.setNumber(String.valueOf(payload.get("numero")));
        } else if (payload.containsKey("number")) {
            bus.setNumber(String.valueOf(payload.get("number")));
        }
        if (payload.containsKey("modele")) {
            bus.setModele(String.valueOf(payload.get("modele")));
        }
        if (payload.containsKey("capacite")) {
            try { bus.setCapacity(Integer.parseInt(payload.get("capacite").toString())); } catch (Exception ignored) {}
        } else if (payload.containsKey("capacity")) {
            try { bus.setCapacity(Integer.parseInt(payload.get("capacity").toString())); } catch (Exception ignored) {}
        }
        if (payload.containsKey("etat")) {
            bus.setEtat(String.valueOf(payload.get("etat")));
        }

        Bus created = busService.create(bus);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Map<String, Object> payload) {
        Bus existing = busService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (payload.containsKey("numero")) {
            existing.setNumber(String.valueOf(payload.get("numero")));
        } else if (payload.containsKey("number")) {
            existing.setNumber(String.valueOf(payload.get("number")));
        }
        if (payload.containsKey("modele")) {
            existing.setModele(String.valueOf(payload.get("modele")));
        }
        if (payload.containsKey("capacite")) {
            try { existing.setCapacity(Integer.parseInt(payload.get("capacite").toString())); } catch (Exception ignored) {}
        } else if (payload.containsKey("capacity")) {
            try { existing.setCapacity(Integer.parseInt(payload.get("capacity").toString())); } catch (Exception ignored) {}
        }
        if (payload.containsKey("etat")) {
            existing.setEtat(String.valueOf(payload.get("etat")));
        }

        Bus updated = busService.update(id, existing);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean removed = busService.delete(id);
        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(Map.of("message", "Bus deleted")).build();
    }
}
