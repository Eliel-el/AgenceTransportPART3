package com.jakarta.udb.agencetransportpart3.bean;

import com.jakarta.udb.agencetransportpart3.entity.Reservation;
import com.jakarta.udb.agencetransportpart3.integration.BusServiceClient;
import com.jakarta.udb.agencetransportpart3.integration.ChauffeurServiceClient;
import com.jakarta.udb.agencetransportpart3.service.ReservationService;
import com.jakarta.udb.agencetransportpart3.service.TrajetService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JSF Backing Bean for Reservation management
 */
@Named
@SessionScoped
public class ReservationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ReservationService reservationService;

    @Inject
    private TrajetService trajetService;

    @Inject
    private BusServiceClient busServiceClient;

    @Inject
    private ChauffeurServiceClient chauffeurServiceClient;

    private List<Reservation> reservations;
    private Reservation selectedReservation;
    private Reservation newReservation;

    // Available resources from external Service 1 and 2
    private List<java.util.Map<String, String>> availableBuses;
    private List<java.util.Map<String, String>> availableChauffeurs;

    // Form fields
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;
    private String departureLocation;
    private String destinationLocation;
    private LocalDateTime departureDate;
    private Integer numberOfSeats;

    // For confirmation
    private Long busIdForConfirmation;
    private Long chauffeurIdForConfirmation;

    @PostConstruct
    public void init() {
        loadReservations();
        newReservation = new Reservation();
    }

    /**
     * Load all reservations
     */
    public void loadReservations() {
        reservations = reservationService.findAll();
    }

    /**
     * Create a new reservation
     */
    public String createReservation() {
        try {
            Reservation reservation = new Reservation();
            reservation.setPassengerName(passengerName);
            reservation.setPassengerEmail(passengerEmail);
            reservation.setPassengerPhone(passengerPhone);
            reservation.setDepartureLocation(departureLocation);
            reservation.setDestinationLocation(destinationLocation);
            reservation.setDepartureDate(departureDate);
            reservation.setNumberOfSeats(numberOfSeats);

            reservationService.createReservation(reservation);

            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Reservation created successfully!");
            clearForm();
            loadReservations();

            return "reservations?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to create reservation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Select a reservation for editing
     */
    public void selectReservation(Reservation reservation) {
        this.selectedReservation = reservation;
    }

    /**
     * Update selected reservation
     */
    public String updateReservation() {
        try {
            reservationService.updateReservation(selectedReservation);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Reservation updated successfully!");
            loadReservations();
            selectedReservation = null;
            return "reservations?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update reservation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Prepare confirmation by loading available resources
     */
    public void prepareConfirm(Reservation reservation) {
        this.selectedReservation = reservation;
        loadAvailableBuses();
        loadAvailableChauffeurs();
    }

    private void loadAvailableBuses() {
        availableBuses = new java.util.ArrayList<>();
        try {
            String json = busServiceClient.getAvailableBuses();
            // Simple manual parsing for demo purposes
            // In production, use JSON-B
            if (json != null && json.startsWith("[")) {
                String[] items = json.substring(1, json.length() - 1).split("\\},\\{");
                for (String item : items) {
                    java.util.Map<String, String> bus = new java.util.HashMap<>();
                    String id = extractValue(item, "id");
                    String number = extractValue(item, "number");
                    bus.put("id", id);
                    bus.put("label", number + " (ID: " + id + ")");
                    availableBuses.add(bus);
                }
            }
        } catch (Exception e) {
            // Log error
        }
    }

    private void loadAvailableChauffeurs() {
        availableChauffeurs = new java.util.ArrayList<>();
        try {
            String json = chauffeurServiceClient.getAvailableChauffeurs();
            if (json != null && json.startsWith("[")) {
                String[] items = json.substring(1, json.length() - 1).split("\\},\\{");
                for (String item : items) {
                    java.util.Map<String, String> chauffeur = new java.util.HashMap<>();
                    String id = extractValue(item, "id");
                    String name = extractValue(item, "name");
                    chauffeur.put("id", id);
                    chauffeur.put("label", name + " (ID: " + id + ")");
                    availableChauffeurs.add(chauffeur);
                }
            }
        } catch (Exception e) {
            // Log error
        }
    }

    private String extractValue(String item, String key) {
        try {
            String search = "\"" + key + "\":";
            int start = item.indexOf(search);
            if (start == -1)
                return "";
            start += search.length();

            if (item.charAt(start) == '"') {
                start++;
                int end = item.indexOf("\"", start);
                return item.substring(start, end);
            } else {
                int end = item.indexOf(",", start);
                if (end == -1)
                    end = item.indexOf("}", start);
                if (end == -1)
                    end = item.length();
                return item.substring(start, end).trim();
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Confirm a reservation
     */
    public String confirmReservation() {
        try {
            if (selectedReservation == null)
                return null;

            Long reservationId = selectedReservation.getId();
            boolean confirmed = reservationService.confirmReservation(reservationId,
                    busIdForConfirmation,
                    chauffeurIdForConfirmation);

            if (confirmed) {
                // Create trajet for this reservation
                trajetService.createTrajet(reservationId,
                        busIdForConfirmation,
                        chauffeurIdForConfirmation);

                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Reservation confirmed and trip created!");
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "Warning",
                        "Unable to confirm reservation - resources not available");
            }

            loadReservations();
            selectedReservation = null;
            return "reservations?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to confirm reservation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cancel a reservation
     */
    public String cancelReservation(Long id) {
        try {
            reservationService.cancelReservation(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Reservation cancelled!");
            loadReservations();
            return "reservations?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to cancel reservation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Delete a reservation
     */
    public String deleteReservation(Long id) {
        try {
            reservationService.deleteReservation(id);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Reservation deleted!");
            loadReservations();
            return "reservations?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete reservation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Clear form fields
     */
    private void clearForm() {
        passengerName = null;
        passengerEmail = null;
        passengerPhone = null;
        departureLocation = null;
        destinationLocation = null;
        departureDate = null;
        numberOfSeats = null;
    }

    /**
     * Add FacesMessage
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Reservation getSelectedReservation() {
        return selectedReservation;
    }

    public void setSelectedReservation(Reservation selectedReservation) {
        this.selectedReservation = selectedReservation;
    }

    public Reservation getNewReservation() {
        return newReservation;
    }

    public void setNewReservation(Reservation newReservation) {
        this.newReservation = newReservation;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Long getBusIdForConfirmation() {
        return busIdForConfirmation;
    }

    public void setBusIdForConfirmation(Long busIdForConfirmation) {
        this.busIdForConfirmation = busIdForConfirmation;
    }

    public Long getChauffeurIdForConfirmation() {
        return chauffeurIdForConfirmation;
    }

    public void setChauffeurIdForConfirmation(Long chauffeurIdForConfirmation) {
        this.chauffeurIdForConfirmation = chauffeurIdForConfirmation;
    }

    public List<java.util.Map<String, String>> getAvailableBuses() {
        return availableBuses;
    }

    public void setAvailableBuses(List<java.util.Map<String, String>> availableBuses) {
        this.availableBuses = availableBuses;
    }

    public List<java.util.Map<String, String>> getAvailableChauffeurs() {
        return availableChauffeurs;
    }

    public void setAvailableChauffeurs(List<java.util.Map<String, String>> availableChauffeurs) {
        this.availableChauffeurs = availableChauffeurs;
    }
}
