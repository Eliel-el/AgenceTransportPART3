package com.jakarta.udb.agencetransportpart3.api;

import com.jakarta.udb.agencetransportpart3.bean.ChauffeurBean;
import com.jakarta.udb.agencetransportpart3.entity.LocalChauffeur;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST API Resource pour la gestion des Chauffeurs
 * Endpoints pour communiquer avec le service de chauffeurs sur la machine distante
 */
@Path("/chauffeurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChauffeurResource {
    
    @Inject
    private ChauffeurBean chauffeurBean;
    
    /**
     * GET /api/chauffeurs - Liste tous les chauffeurs
     */
    @GET
    public Response getAllChauffeurs() {
        try {
            List<LocalChauffeur> chauffeurs = chauffeurBean.getAllChauffeurs();
            return Response.ok(chauffeurs).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * GET /api/chauffeurs/{id} - Détails d'un chauffeur
     */
    @GET
    @Path("/{id}")
    public Response getChauffeur(@PathParam("id") Long id) {
        try {
            LocalChauffeur chauffeur = chauffeurBean.getChauffeurById(id);
            if (chauffeur == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Chauffeur not found\"}")
                        .build();
            }
            return Response.ok(chauffeur).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * POST /api/chauffeurs - Créer un nouveau chauffeur
     */
    @POST
    public Response createChauffeur(LocalChauffeur chauffeur) {
        try {
            if (chauffeur == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Chauffeur data is required\"}")
                        .build();
            }
            
            LocalChauffeur created = chauffeurBean.createChauffeur(chauffeur);
            if (created == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"Failed to create chauffeur\"}")
                        .build();
            }
            
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * PUT /api/chauffeurs/{id} - Modifier un chauffeur
     */
    @PUT
    @Path("/{id}")
    public Response updateChauffeur(@PathParam("id") Long id, LocalChauffeur chauffeur) {
        try {
            if (chauffeur == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Chauffeur data is required\"}")
                        .build();
            }
            
            LocalChauffeur updated = chauffeurBean.updateChauffeur(id, chauffeur);
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Chauffeur not found or update failed\"}")
                        .build();
            }
            
            return Response.ok(updated).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * DELETE /api/chauffeurs/{id} - Supprimer un chauffeur
     */
    @DELETE
    @Path("/{id}")
    public Response deleteChauffeur(@PathParam("id") Long id) {
        try {
            boolean deleted = chauffeurBean.deleteChauffeur(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Chauffeur not found or deletion failed\"}")
                        .build();
            }
            
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * GET /api/chauffeurs/disponibles - Chauffeurs disponibles
     */
    @GET
    @Path("/disponibles")
    public Response getAvailableChauffeurs() {
        try {
            List<LocalChauffeur> chauffeurs = chauffeurBean.getAvailableChauffeurs();
            return Response.ok(chauffeurs).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * POST /api/chauffeurs/assigner - Assigner un chauffeur à un trajet
     * Body: {"chauffeurId": 1, "trajetId": 2}
     */
    @POST
    @Path("/assigner")
    public Response assignChauffeur(AssignmentRequest request) {
        try {
            if (request == null || request.getChauffeurId() == null || request.getTrajetId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"chauffeurId and trajetId are required\"}")
                        .build();
            }
            
            boolean assigned = chauffeurBean.assignChauffeurToTrajet(request.getChauffeurId(), request.getTrajetId());
            if (!assigned) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"Failed to assign chauffeur\"}")
                        .build();
            }
            
            return Response.ok("{\"message\": \"Chauffeur assigned successfully\"}").build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * POST /api/chauffeurs/{id}/liberer - Libérer un chauffeur d'un trajet
     */
    @POST
    @Path("/{id}/liberer")
    public Response releaseChauffeur(@PathParam("id") Long id) {
        try {
            boolean released = chauffeurBean.releaseChauffeur(id);
            if (!released) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"Failed to release chauffeur\"}")
                        .build();
            }
            
            return Response.ok("{\"message\": \"Chauffeur released successfully\"}").build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * Classe interne pour les requêtes d'assignation
     */
    public static class AssignmentRequest {
        private Long chauffeurId;
        private Long trajetId;
        
        public Long getChauffeurId() {
            return chauffeurId;
        }
        
        public void setChauffeurId(Long chauffeurId) {
            this.chauffeurId = chauffeurId;
        }
        
        public Long getTrajetId() {
            return trajetId;
        }
        
        public void setTrajetId(Long trajetId) {
            this.trajetId = trajetId;
        }
    }
}
