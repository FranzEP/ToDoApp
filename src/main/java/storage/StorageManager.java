package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.ToDoList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

public class StorageManager {
    private static final String FILE_PATH = "daten.json";
    private final Gson gson;

    public StorageManager() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ToDoList.class, new ToDoListTypeAdapter())
                .setPrettyPrinting()
                .create();
    }

    // Speichert alle Listen in eine Datei
    public void save(List<ToDoList> lists) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            // WICHTIG: Wir zwingen Gson hier ausdrücklich dazu, die Objekte als "ToDoList"
            // zu behandeln, damit unser eigener TypeAdapter aktiviert wird!
            java.lang.reflect.Type listType = new TypeToken<List<ToDoList>>() {}.getType();
            gson.toJson(lists, listType, writer);
        } catch (IOException ignored) {
            // Fehler beim Speichern ignorieren wir der Einfachheit halber
        }
    }

    public List<ToDoList> load() {
        if (!Files.exists(Paths.get(FILE_PATH))) return new ArrayList<>();
        try (Reader reader = new FileReader(FILE_PATH)) {
            return gson.fromJson(reader, new TypeToken<List<ToDoList>>() {}.getType());
        } catch (IOException ignored) {
            return new ArrayList<>();
        }
    }
}