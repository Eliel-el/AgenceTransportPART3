package com.jakarta.udb.agencetransportpart3.entity;

/**
 * Local Chauffeur entity for test resources that are always available
 */
public class LocalChauffeur {
    private Long id;
    private String name;
    private String license;
    private boolean isTestResource;

    public LocalChauffeur() {
    }

    public LocalChauffeur(Long id, String name, String license, boolean isTestResource) {
        this.id = id;
        this.name = name;
        this.license = license;
        this.isTestResource = isTestResource;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public boolean isTestResource() {
        return isTestResource;
    }

    public void setTestResource(boolean testResource) {
        isTestResource = testResource;
    }

    @Override
    public String toString() {
        return "LocalChauffeur{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", license='" + license + '\'' +
                ", isTestResource=" + isTestResource +
                '}';
    }
}
