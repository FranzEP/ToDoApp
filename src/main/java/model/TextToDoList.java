package model;

public class TextToDoList extends ToDoList {
    private String content;

    public TextToDoList(String title) {
        super(title);
        this.content = ""; // startet ohne text
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}