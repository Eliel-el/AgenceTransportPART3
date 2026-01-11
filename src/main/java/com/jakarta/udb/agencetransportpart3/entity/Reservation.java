package com.jakarta.udb.agencetransportpart3.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a Reservation
 */
@Entity
@Table(name = "RESERVATION")
@NamedQueries({
        @NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r"),
        @NamedQuery(name = "Reservation.findById", query = "SELECT r FROM Reservation r WHERE r.id = :id"),
        @NamedQuery(name = "Reservation.findByStatus", query = "SELECT r FROM Reservation r WHERE r.status = :status")
})
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PASSENGER_NAME", nullable = false, length = 100)
    private String passengerName;

    @Column(name = "PASSENGER_EMAIL", length = 100)
    private String passengerEmail;

    @Column(name = "PASSENGER_PHONE", length = 20)
    private String passengerPhone;

    @Column(name = "DEPARTURE_LOCATION", nullable = false, length = 200)
    private String departureLocation;

    @Column(name = "DESTINATION_LOCATION", nullable = false, length = 200)
    private String destinationLocation;

    @Column(name = "DEPARTURE_DATE", nullable = false)
    private LocalDateTime departureDate;

    @Column(name = "NUMBER_OF_SEATS", nullable = false)
    private Integer numberOfSeats;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status; // PENDING, CONFIRMED, CANCELLED

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    // Constructors
    public Reservation() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Reservation(String passengerName, String departureLocation, String destinationLocation,
            LocalDateTime departureDate, Integer numberOfSeats) {
        this();
        this.passengerName = passengerName;
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.departureDate = departureDate;
        this.numberOfSeats = numberOfSeats;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", passengerName='" + passengerName + '\'' +
                ", departureLocation='" + departureLocation + '\'' +
                ", destinationLocation='" + destinationLocation + '\'' +
                ", departureDate=" + departureDate +
                ", status='" + status + '\'' +
                '}';
    }
}
