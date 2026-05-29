package storage;

import com.google.gson.*;
import model.ToDoList;
import model.TextToDoList;
import model.CheckboxToDoList;
import java.lang.reflect.Type;

/**
 * Ein benutzerdefinierter GSON-Adapter, der die polymorphen Unterklassen von {@link ToDoList}
 * korrekt serialisiert und deserialisiert.
 * Er verhindert Datenverlust, indem er den Klassennamen als Meta-Attribut
 * in das JSON-Objekt injiziert.
 */
public class ToDoListTypeAdapter implements JsonSerializer<ToDoList>, JsonDeserializer<ToDoList> {

    /**
     * Konvertiert ein Java-Objekt vom Typ {@link ToDoList} in ein JSON-Element und fügt
     * den exakten Klassennamen im Feld "type" hinzu.
     * * @param src Das zu serialisierende Listen-Objekt.
     * @param typeOfSrc Der dynamische Typ der Quelle.
     * @param context Der Serialisierungs-Kontext von Gson.
     * @return Das angereicherte {@link JsonElement}.
     */
    @Override
    public JsonElement serialize(ToDoList src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("type", src.getClass().getSimpleName());
        result.add("properties", context.serialize(src));
        return result;
    }

    /**
     * Liest ein JSON-Element aus, prüft das Meta-Attribut "type" und instanziiert
     * über den Kontext die exakt passende Java-Unterklasse (Text oder Checkbox).
     * * @param json Das einzulesende JSON-Element.
     * @param typeOfT Datentyp des Zielobjekts.
     * @param context Der Deserialisierungs-Kontext von Gson.
     * @return Das instanziierte, konkrete {@link ToDoList}-Objekt oder {@code null} bei korrupten Daten.
     * @throws JsonParseException Falls das JSON nicht dem erwarteten Format entspricht.
     */
    @Override
    public ToDoList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has("type") || !jsonObject.has("properties")) {
            return null;
        }

        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        if ("TextToDoList".equals(type)) {
            return context.deserialize(element, TextToDoList.class);
        } else if ("CheckboxToDoList".equals(type)) {
            return context.deserialize(element, CheckboxToDoList.class);
        }
        return null;
    }
}