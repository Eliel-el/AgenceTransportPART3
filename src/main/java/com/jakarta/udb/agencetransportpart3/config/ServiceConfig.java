package com.jakarta.udb.agencetransportpart3.config;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Service Configuration - Gère les URLs des services externes
 * Peut être modifié facilement pour changer les adresses IP
 */
@ApplicationScoped
public class ServiceConfig {
    
    private static final Logger LOGGER = Logger.getLogger(ServiceConfig.class.getName());
    private Properties properties;
    
    public ServiceConfig() {
        this.properties = new Properties();
        loadConfiguration();
    }
    
    /**
     * Charge la configuration depuis le fichier application.properties
     */
    private void loadConfiguration() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                LOGGER.info("Configuration loaded successfully");
            } else {
                LOGGER.warning("application.properties file not found");
                setDefaultValues();
            }
        } catch (IOException ex) {
            LOGGER.warning("Error loading configuration: " + ex.getMessage());
            setDefaultValues();
        }
    }
    
    /**
     * Définit les valeurs par défaut
     */
    private void setDefaultValues() {
        properties.setProperty("chauffeur.service.url", "http://localhost:8080/AgenceTransportPART2/api/chauffeurs");
        properties.setProperty("bus.service.url", "http://localhost:8080/servicegestionbus/api/bus");
        properties.setProperty("chauffeur.service.timeout", "5000");
        properties.setProperty("bus.service.timeout", "5000");
    }
    
    /**
     * Obtient l'URL du service de chauffeurs
     */
    public String getChauffeurServiceUrl() {
        return properties.getProperty("chauffeur.service.url", "http://localhost:8080/AgenceTransportPART2/api/chauffeurs");
    }
    
    /**
     * Obtient l'URL du service de bus
     */
    public String getBusServiceUrl() {
        return properties.getProperty("bus.service.url", "http://localhost:8080/servicegestionbus/api/bus");
    }
    
    /**
     * Obtient le timeout du service de chauffeurs (en ms)
     */
    public int getChauffeurServiceTimeout() {
        return Integer.parseInt(properties.getProperty("chauffeur.service.timeout", "5000"));
    }
    
    /**
     * Obtient le timeout du service de bus (en ms)
     */
    public int getBusServiceTimeout() {
        return Integer.parseInt(properties.getProperty("bus.service.timeout", "5000"));
    }
    
    /**
     * Modification dynamique de l'URL du service de chauffeurs
     */
    public void setChauffeurServiceUrl(String url) {
        properties.setProperty("chauffeur.service.url", url);
        LOGGER.info("Chauffeur service URL updated to: " + url);
    }
    
    /**
     * Modification dynamique de l'URL du service de bus
     */
    public void setBusServiceUrl(String url) {
        properties.setProperty("bus.service.url", url);
        LOGGER.info("Bus service URL updated to: " + url);
    }
}
