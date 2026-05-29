package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.ToDoList;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Der StorageManager ist für die Persistenzschicht der Anwendung verantwortlich.
 * Er steuert das Laden und Speichern der polymorphen ToDo-Listen in einer lokalen JSON-Datei.
 * * @author Dein Name
 * @version 1.0
 */
public class StorageManager {

    /** Der Dateipfad, unter dem die JSON-Daten abgelegt werden. */
    private static final String FILE_PATH = "daten.json";

    /** Die konfigurierte Gson-Instanz inklusive des benutzerdefinierten Type-Adapters. */
    private final Gson gson;

    /**
     * Initialisiert den StorageManager und konfiguriert den Gson-Builder
     * mit dem notwendigen {@link ToDoListTypeAdapter} für polymorphen Datenstrukturen.
     */
    public StorageManager() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ToDoList.class, new ToDoListTypeAdapter())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Serialisiert die übergebene Liste von ToDo-Listen in das JSON-Format
     * und speichert diese dauerhaft in der Datei {@value #FILE_PATH}.
     *
     * @param lists Die Liste aller im UI vorhandenen {@link ToDoList}-Objekte.
     */
    public void save(List<ToDoList> lists) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            Type listType = new TypeToken<List<ToDoList>>() {}.getType();
            gson.toJson(lists, listType, writer);
        } catch (IOException ignored) {
            // Fehlerbehandlung für den studentischen Rahmen vereinfacht
        }
    }

    /**
     * Deserialisiert die Daten aus der lokalen JSON-Datei und stellt die
     * ursprünglichen ToDo-Listen-Objekte wieder her.
     * Falls keine Datei existiert, wird eine leere Liste zurückgegeben.
     *
     * @return Eine {@link List} von rekonstruierten {@link ToDoList}-Objekten.
     */
    public List<ToDoList> load() {
        if (!Files.exists(Paths.get(FILE_PATH))) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(FILE_PATH)) {
            return gson.fromJson(reader, new TypeToken<List<ToDoList>>() {}.getType());
        } catch (IOException ignored) {
            return new ArrayList<>();
        }
    }
}