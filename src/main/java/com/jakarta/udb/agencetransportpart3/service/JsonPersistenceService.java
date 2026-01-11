package com.jakarta.udb.agencetransportpart3.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class JsonPersistenceService {

    private static final Logger LOGGER = Logger.getLogger(JsonPersistenceService.class.getName());

    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + "AgenceTransport_data";

    private final Jsonb jsonb = JsonbBuilder.create();

    public JsonPersistenceService() {
        createDataDirectory();
    }

    private void createDataDirectory() {
        try {
            Path path = Path.of(DATA_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot create data directory", e);
        }
    }

    private <T> File getFile(Class<T> clazz) {
        return new File(DATA_DIR,
                clazz.getSimpleName().toLowerCase() + "s.json");
    }

    // ✅ UNIQUE MÉTHODE DE LECTURE
    public <T> List<T> loadAll(Class<T> clazz) {
        File file = getFile(clazz);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            String json = Files.readString(file.toPath());
            if (json == null || json.isBlank()) {
                return new ArrayList<>();
            }

            @SuppressWarnings("unchecked")
            T[] array = (T[]) jsonb.fromJson(
                    json,
                    java.lang.reflect.Array.newInstance(clazz, 0).getClass());

            return new ArrayList<>(List.of(array));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading " + clazz.getSimpleName(), e);
            return new ArrayList<>();
        }
    }

    public <T> void saveAll(List<T> entities, Class<T> clazz) {
        File file = getFile(clazz);

        try {
            String json = jsonb.toJson(entities);
            Files.writeString(file.toPath(), json);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving " + clazz.getSimpleName(), e);
        }
    }
}
