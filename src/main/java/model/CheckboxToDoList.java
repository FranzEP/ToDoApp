package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert eine listenbasierte ToDo-Liste, die aus mehreren interaktiven
 * {@link ToDoItem}-Objekten mit Checkboxen besteht.
 */
public class CheckboxToDoList extends ToDoList {
    /**die interne Liste, die die einzelnen Aufgabeneinträge speichert.*/
    private List<ToDoItem> items;

    public CheckboxToDoList(String title) {
        super(title);
        this.items = new ArrayList<>();
    }

    public List<ToDoItem> getItems() { return items; }
    /**
     * Fügt der Liste eine neue Aufgabe hinzu.
     * @param item Das hinzuzufügende {@link ToDoItem}.
     */
    public void addItem(ToDoItem item) {
        this.items.add(item);
    }

    /**
     * Sortiert die Einträge der Liste in Echtzeit.
     * Offene Aufgaben verbleiben oben, während bereits erledigte (abgehakte) Aufgaben
     * an das Ende der Liste verschoben werden.
     */
    public void sortItems() {
        List<ToDoItem> openItems = new ArrayList<>();
        List<ToDoItem> doneItems = new ArrayList<>();

        // Trenne offene und erledigte Einträge
        for (ToDoItem item : items) {
            if (item.isDone()) {
                doneItems.add(item);
            } else {
                openItems.add(item);
            }
        }

        // Leere die alte Liste und setze sie neu zusammen (erst offen, dann erledigt)
        this.items.clear();
        this.items.addAll(openItems);
        this.items.addAll(doneItems);
    }
}