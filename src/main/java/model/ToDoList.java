package model;

public abstract class ToDoList {
    private String title;

    public ToDoList(String title) {
        this.title = title;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // Nützlich für die Anzeige in der Swing-Liste auf der linken Seite
    @Override
    public String toString() {
        return title;
    }
}