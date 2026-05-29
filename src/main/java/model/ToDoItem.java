package model;

public class ToDoItem {
    private String text;
    private boolean isDone;

    public ToDoItem(String text) {
        this.text = text;
        this.isDone = false; // standartmäßig ist ein neuer Eintrag nicht erledigt
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }
}