package com.jakarta.udb.agencetransportpart3.bean;

import com.jakarta.udb.agencetransportpart3.entity.Reservation;
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

    private List<Reservation> reservations;
    private Reservation selectedReservation;
    private Reservation newReservation;

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
     * Confirm a reservation
     */
    public String confirmReservation(Long reservationId) {
        try {
            // For now, use default IDs (1 for bus, 1 for chauffeur)
            // In a real app, these would be selected from dropdowns
            boolean confirmed = reservationService.confirmReservation(reservationId,
                    busIdForConfirmation != null ? busIdForConfirmation : 1L,
                    chauffeurIdForConfirmation != null ? chauffeurIdForConfirmation : 1L);

            if (confirmed) {
                // Create trajet for this reservation
                trajetService.createTrajet(reservationId,
                        busIdForConfirmation != null ? busIdForConfirmation : 1L,
                        chauffeurIdForConfirmation != null ? chauffeurIdForConfirmation : 1L);

                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Reservation confirmed and trip created!");
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "Warning",
                        "Unable to confirm reservation - resources not available");
            }

            loadReservations();
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
}
