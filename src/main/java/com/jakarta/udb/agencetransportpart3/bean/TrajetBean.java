package com.jakarta.udb.agencetransportpart3.bean;

import com.jakarta.udb.agencetransportpart3.entity.Trajet;
import com.jakarta.udb.agencetransportpart3.service.TrajetService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * JSF Backing Bean for Trajet management
 */
@Named
@SessionScoped
public class TrajetBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TrajetService trajetService;

    private List<Trajet> trajets;
    private Trajet selectedTrajet;

    // For assignment
    private Long busIdForAssignment;
    private Long chauffeurIdForAssignment;

    @PostConstruct
    public void init() {
        loadTrajets();
    }

    /**
     * Load all trajets
     */
    public void loadTrajets() {
        trajets = trajetService.findAll();
    }

    /**
     * Load trajets by status
     */
    public void loadTrajetsByStatus(String status) {
        trajets = trajetService.findByStatus(status);
    }

    /**
     * Select a trajet
     */
    public void selectTrajet(Trajet trajet) {
        this.selectedTrajet = trajet;
    }

    /**
     * Update selected trajet
     */
    public String updateTrajet() {
        try {
            trajetService.updateTrajet(selectedTrajet);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Trip updated successfully!");
            loadTrajets();
            selectedTrajet = null;
            return "trajets?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update trip: " + e.getMessage());
            return null;
        }
    }

    /**
     * Assign bus to trajet
     */
    public String assignBus(Long trajetId) {
        try {
            if (busIdForAssignment == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Warning", "Please select a bus");
                return null;
            }

            trajetService.assignBus(trajetId, busIdForAssignment);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Bus assigned successfully!");
            loadTrajets();
            busIdForAssignment = null;
            return "trajets?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to assign bus: " + e.getMessage());
            return null;
        }
    }

    /**
     * Assign chauffeur to trajet
     */
    public String assignChauffeur(Long trajetId) {
        try {
            if (chauffeurIdForAssignment == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Warning", "Please select a driver");
                return null;
            }

            trajetService.assignChauffeur(trajetId, chauffeurIdForAssignment);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Driver assigned successfully!");
            loadTrajets();
            chauffeurIdForAssignment = null;
            return "trajets?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to assign driver: " + e.getMessage());
            return null;
        }
    }

    /**
     * Start a trajet
     */
    public String startTrajet(Long id) {
        try {
            trajetService.startTrajet(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Trip started!");
            loadTrajets();
            return "trajets?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to start trip: " + e.getMessage());
            return null;
        }
    }

    /**
     * Complete a trajet
     */
    public String completeTrajet(Long id) {
        try {
            trajetService.completeTrajet(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Trip completed!");
            loadTrajets();
            return "trajets?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to complete trip: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cancel a trajet
     */
    public String cancelTrajet(Long id) {
        try {
            trajetService.cancelTrajet(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Trip cancelled!");
            loadTrajets();
            return "trajets?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to cancel trip: " + e.getMessage());
            return null;
        }
    }

    /**
     * Delete a trajet
     */
    public String deleteTrajet(Long id) {
        try {
            trajetService.deleteTrajet(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Trip deleted!");
            loadTrajets();
            return "trajets?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete trip: " + e.getMessage());
            return null;
        }
    }

    /**
     * Add FacesMessage
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<Trajet> getTrajets() {
        return trajets;
    }

    public void setTrajets(List<Trajet> trajets) {
        this.trajets = trajets;
    }

    public Trajet getSelectedTrajet() {
        return selectedTrajet;
    }

    public void setSelectedTrajet(Trajet selectedTrajet) {
        this.selectedTrajet = selectedTrajet;
    }

    public Long getBusIdForAssignment() {
        return busIdForAssignment;
    }

    public void setBusIdForAssignment(Long busIdForAssignment) {
        this.busIdForAssignment = busIdForAssignment;
    }

    public Long getChauffeurIdForAssignment() {
        return chauffeurIdForAssignment;
    }

    public void setChauffeurIdForAssignment(Long chauffeurIdForAssignment) {
        this.chauffeurIdForAssignment = chauffeurIdForAssignment;
    }
}
