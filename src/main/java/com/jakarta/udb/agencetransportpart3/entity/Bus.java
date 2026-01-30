package com.jakarta.udb.agencetransportpart3.entity;

/**
 * Simple Bus entity used by the local Bus API
 */
public class Bus {
    private Long id;
    private String number; // English-compatible field used by some clients
    private String modele;
    private Integer capacity;
    private String etat;

    public Bus() {
    }

    public Bus(Long id, String number, String modele, Integer capacity, String etat) {
        this.id = id;
        this.number = number;
        this.modele = modele;
        this.capacity = capacity;
        this.etat = etat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
}
