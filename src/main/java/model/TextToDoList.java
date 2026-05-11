package model;

public class TextToDoList extends ToDoList {
    private String content;

    public TextToDoList(String title) {
        super(title);
        this.content = ""; // Startet mit leerem Text
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}