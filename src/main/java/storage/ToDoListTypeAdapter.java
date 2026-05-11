package storage;

import com.google.gson.*;
import model.ToDoList;
import model.TextToDoList;
import model.CheckboxToDoList;
import java.lang.reflect.Type;

public class ToDoListTypeAdapter implements JsonSerializer<ToDoList>, JsonDeserializer<ToDoList> {
    @Override
    public JsonElement serialize(ToDoList src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("type", src.getClass().getSimpleName());
        result.add("properties", context.serialize(src));
        return result;
    }

    @Override
    public ToDoList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // --- NEUER SICHERHEITS-CHECK ---
        // Wenn die Datei kaputt ist und keinen "type" oder "properties" hat,
        // überspringen wir diesen Eintrag einfach, statt abzustürzen!
        if (!jsonObject.has("type") || !jsonObject.has("properties")) {
            return null;
        }
        // -------------------------------

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