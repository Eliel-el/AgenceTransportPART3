package com.jakarta.udb.agencetransportpart3.bean;

import com.jakarta.udb.agencetransportpart3.config.ServiceConfig;
import com.jakarta.udb.agencetransportpart3.entity.LocalChauffeur;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service Bean pour la gestion des chauffeurs
 * Communique avec le service de chauffeurs sur la machine distante
 */
@ApplicationScoped
public class ChauffeurBean {
    
    private static final Logger LOGGER = Logger.getLogger(ChauffeurBean.class.getName());
    
    @Inject
    private ServiceConfig serviceConfig;
    
    private Client client;
    private Jsonb jsonb;
    
    public ChauffeurBean() {
        this.client = ClientBuilder.newClient();
        this.jsonb = JsonbBuilder.create();
    }
    
    /**
     * Récupère la liste de tous les chauffeurs
     */
    public List<LocalChauffeur> getAllChauffeurs() {
        try {
            String url = serviceConfig.getChauffeurServiceUrl();
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String jsonResponse = response.readEntity(String.class);
                List<LocalChauffeur> chauffeurs = jsonb.fromJson(jsonResponse, 
                    new ArrayList<LocalChauffeur>(){}.getClass());
                LOGGER.info("Retrieved " + chauffeurs.size() + " chauffeurs from external service");
                return chauffeurs;
            } else {
                LOGGER.warning("Error retrieving chauffeurs: " + response.getStatus());
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            LOGGER.severe("Error communicating with chauffeur service: " + ex.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Récupère un chauffeur par ID
     */
    public LocalChauffeur getChauffeurById(Long id) {
        try {
            String url = serviceConfig.getChauffeurServiceUrl() + "/" + id;
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                LocalChauffeur chauffeur = response.readEntity(LocalChauffeur.class);
                LOGGER.info("Retrieved chauffeur with ID: " + id);
                return chauffeur;
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                LOGGER.warning("Chauffeur not found: " + id);
                return null;
            } else {
                LOGGER.warning("Error retrieving chauffeur: " + response.getStatus());
                return null;
            }
        } catch (Exception ex) {
            LOGGER.severe("Error communicating with chauffeur service: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Crée un nouveau chauffeur
     */
    public LocalChauffeur createChauffeur(LocalChauffeur chauffeur) {
        try {
            String url = serviceConfig.getChauffeurServiceUrl();
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(chauffeur));
            
            if (response.getStatus() == Response.Status.CREATED.getStatusCode() ||
                response.getStatus() == Response.Status.OK.getStatusCode()) {
                LocalChauffeur created = response.readEntity(LocalChauffeur.class);
                LOGGER.info("Created new chauffeur with ID: " + created.getId());
                return created;
            } else {
                LOGGER.warning("Error creating chauffeur: " + response.getStatus());
                return null;
            }
        } catch (Exception ex) {
            LOGGER.severe("Error communicating with chauffeur service: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Modifie un chauffeur existant
     */
    public LocalChauffeur updateChauffeur(Long id, LocalChauffeur chauffeur) {
        try {
            String url = serviceConfig.getChauffeurServiceUrl() + "/" + id;
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(chauffeur));
            
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                LocalChauffeur updated = response.readEntity(LocalChauffeur.class);
                LOGGER.info("Updated chauffeur with ID: " + id);
                return updated;
            } else {
                LOGGER.warning("Error updating chauffeur: " + response.getStatus());
                return null;
            }
        } catch (Exception ex) {
            LOGGER.severe("Error communicating with chauffeur service: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Supprime un chauffeur
     */
    public boolean deleteChauffeur(Long id) {
        try {
            String url = serviceConfig.getChauffeurServiceUrl() + "/" + id;
            Response response = client.target(url)
                    .request()
                    .delete();
            
            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode() ||
                response.getStatus() == Response.Status.OK.getStatusCode()) {
                LOGGER.info("Deleted chauffeur with ID: " + id);
                return true;
            } else {
                LOGGER.warning("Error deleting chauffeur: " + response.getStatus());
                return false;
            }
        } catch (Exception ex) {
            LOGGER.severe("Error communicating with chauffeur service: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère les chauffeurs disponibles
     */
    public List<LocalChauffeur> getAvailableChauffeurs() {
        try {
            String url = serviceConfig.getChauffeurServiceUrl() + "/disponibles";
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String jsonResponse = response.readEntity(String.class);
                List<LocalChauffeur> chauffeurs = jsonb.fromJson(jsonResponse, 
                    new ArrayList<LocalChauffeur>(){}.getClass());
                LOGGER.info("Retrieved " + chauffeurs.size() + " available chauffeurs");
                return chauffeurs;
            } else {
                LOGGER.warning("Error retrieving available chauffeurs: " + response.getStatus());
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            LOGGER.severe("Error communicating with chauffeur service: " + ex.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Assigne un chauffeur à un trajet
     */
    public boolean assignChauffeurToTrajet(Long chauffeurId, Long trajetId) {
        try {
            String url = serviceConfig.getChauffeurServiceUrl() + "/assigner";
            String jsonPayload = "{\"chauffeurId\":" + chauffeurId + ",\"trajetId\":" + trajetId + "}";
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(jsonPayload));
            
            if (response.getStatus() == Response.Status.OK.getStatusCode() ||
                response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                LOGGER.info("Assigned chauffeur " + chauffeurId + " to trajet " + trajetId);
                return true;
            } else {
                LOGGER.warning("Error assigning chauffeur: " + response.getStatus());
                return false;
            }
        } catch (Exception ex) {
            LOGGER.severe("Error communicating with chauffeur service: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Libère un chauffeur d'un trajet
     */
    public boolean releaseChauffeur(Long chauffeurId) {
        try {
            String url = serviceConfig.getChauffeurServiceUrl() + "/" + chauffeurId + "/liberer";
            Response response = client.target(url)
                    .request()
                    .post(null);
            
            if (response.getStatus() == Response.Status.OK.getStatusCode() ||
                response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                LOGGER.info("Released chauffeur with ID: " + chauffeurId);
                return true;
            } else {
                LOGGER.warning("Error releasing chauffeur: " + response.getStatus());
                return false;
            }
        } catch (Exception ex) {
            LOGGER.severe("Error communicating with chauffeur service: " + ex.getMessage());
            return false;
        }
    }
}
