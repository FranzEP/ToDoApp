package model;
/**
 * Die abstrakte Basisklasse für alle Arten von ToDo-Listen in der Anwendung.
 * Sie stellt sicher, dass jede Liste über einen Titel verfügt und erzwingt eine
 * einheitliche Textdarstellung für Benutzeroberflächen.
 */
public abstract class ToDoList {
    /** Der Titel der Liste. */
    private String title;
    /**
     * Erstellt eine neue ToDo-Liste mit dem angegebenen Titel.
     * * @param title Der Name bzw. die Überschrift der Liste.
     */
    public ToDoList(String title) {
        this.title = title;
    }

    /**
     * Gibt den Titel der Liste zurück.
     * @return der Titel als String
     */
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    /**
     * Überschreibt die Standart-toString-Methode, damit Swing-Kompnenten
     * das Objekt automatisch korrekt als Text-Titel auf dem Bildschirm darstellen.
     * @return Der Titel der Liste.
     */
    @Override
    public String toString() {
        return title;
    }
}