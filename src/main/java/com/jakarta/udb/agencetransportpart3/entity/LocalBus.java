package com.jakarta.udb.agencetransportpart3.entity;

/**
 * Local Bus entity for test resources that are always available
 */
public class LocalBus {
    private Long id;
    private String number;
    private Integer capacity;
    private boolean isTestResource;

    public LocalBus() {
    }

    public LocalBus(Long id, String number, Integer capacity, boolean isTestResource) {
        this.id = id;
        this.number = number;
        this.capacity = capacity;
        this.isTestResource = isTestResource;
    }

    // Getters and Setters
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public boolean isTestResource() {
        return isTestResource;
    }

    public void setTestResource(boolean testResource) {
        isTestResource = testResource;
    }

    @Override
    public String toString() {
        return "LocalBus{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", capacity=" + capacity +
                ", isTestResource=" + isTestResource +
                '}';
    }
}
