package com.jakarta.udb.agencetransportpart3.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a Trajet (Trip)
 */
@Entity
@Table(name = "TRAJET")
@NamedQueries({
        @NamedQuery(name = "Trajet.findAll", query = "SELECT t FROM Trajet t"),
        @NamedQuery(name = "Trajet.findById", query = "SELECT t FROM Trajet t WHERE t.id = :id"),
        @NamedQuery(name = "Trajet.findByStatus", query = "SELECT t FROM Trajet t WHERE t.status = :status"),
        @NamedQuery(name = "Trajet.findByReservationId", query = "SELECT t FROM Trajet t WHERE t.reservationId = :reservationId")
})
public class Trajet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RESERVATION_ID", nullable = true)
    private Long reservationId;

    @Column(name = "BUS_ID")
    private Long busId;

    @Column(name = "BUS_NUMBER", length = 50)
    private String busNumber;

    @Column(name = "CHAUFFEUR_ID")
    private Long chauffeurId;

    @Column(name = "CHAUFFEUR_NAME", length = 100)
    private String chauffeurName;

    @Column(name = "DEPARTURE_LOCATION", nullable = false, length = 200)
    private String departureLocation;

    @Column(name = "DESTINATION_LOCATION", nullable = false, length = 200)
    private String destinationLocation;

    @Column(name = "DEPARTURE_DATE", nullable = false)
    private LocalDateTime departureDate;

    @Column(name = "ARRIVAL_DATE")
    private LocalDateTime arrivalDate;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status; // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "DISTANCE_KM")
    private Double distanceKm;

    @Column(name = "ACTUAL_DEPARTURE_DATE")
    private LocalDateTime actualDepartureDate;

    @Column(name = "ACTUAL_ARRIVAL_DATE")
    private LocalDateTime actualArrivalDate;

    @Column(name = "NOTES", length = 500)
    private String notes;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    // Constructors
    public Trajet() {
        this.status = "PLANNED";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Trajet(Long reservationId, String departureLocation, String destinationLocation,
            LocalDateTime departureDate) {
        this();
        this.reservationId = reservationId;
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.departureDate = departureDate;
    }

    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public Long getChauffeurId() {
        return chauffeurId;
    }

    public void setChauffeurId(Long chauffeurId) {
        this.chauffeurId = chauffeurId;
    }

    public String getChauffeurName() {
        return chauffeurName;
    }

    public void setChauffeurName(String chauffeurName) {
        this.chauffeurName = chauffeurName;
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

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public LocalDateTime getActualDepartureDate() {
        return actualDepartureDate;
    }

    public void setActualDepartureDate(LocalDateTime actualDepartureDate) {
        this.actualDepartureDate = actualDepartureDate;
    }

    public LocalDateTime getActualArrivalDate() {
        return actualArrivalDate;
    }

    public void setActualArrivalDate(LocalDateTime actualArrivalDate) {
        this.actualArrivalDate = actualArrivalDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Trajet)) {
            return false;
        }
        Trajet other = (Trajet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Trajet{" +
                "id=" + id +
                ", reservationId=" + reservationId +
                ", busNumber='" + busNumber + '\'' +
                ", chauffeurName='" + chauffeurName + '\'' +
                ", departureLocation='" + departureLocation + '\'' +
                ", destinationLocation='" + destinationLocation + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
