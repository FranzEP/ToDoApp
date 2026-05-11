package model;

import java.util.ArrayList;
import java.util.List;

public class CheckboxToDoList extends ToDoList {
    private List<ToDoItem> items;

    public CheckboxToDoList(String title) {
        super(title);
        this.items = new ArrayList<>();
    }

    public List<ToDoItem> getItems() { return items; }

    public void addItem(ToDoItem item) {
        this.items.add(item);
    }

    // Wunschkriterium 1: Sortiert erledigte Einträge ans Ende
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