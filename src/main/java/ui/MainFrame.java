package ui;

// Importe unserer eigenen Klassen (das Datenmodell und der Speicher-Manager)
import model.ToDoList;
import model.TextToDoList;
import model.CheckboxToDoList;
import model.ToDoItem;
import storage.StorageManager;

// Importe für die grafische Oberfläche (Swing) und Event-Handling (AWT)
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * MainFrame erbt von JFrame. "JFrame" ist das Hauptfenster in Java.
 * Wenn wir davon erben, wird unsere Klasse automatisch zu einem Fenster,
 * das wir anzeigen, verschieben und schließen können.
 */
public class MainFrame extends JFrame {

    // --- KLASSENVARIABLEN (Speicherplätze für die ganze Klasse) ---

    // Das DefaultListModel ist der "Daten-Speicher" für die JList.
    // Die JList selbst malt nur die Pixel auf den Bildschirm, das Model hält die echten Objekte.
    private DefaultListModel<ToDoList> listModel;

    // Die visuelle Liste auf der linken Bildschirmseite
    private JList<ToDoList> navigationList;

    // Das rechte Panel, dessen Inhalt wir ständig austauschen (je nachdem, welche Liste geklickt wurde)
    private JPanel rightPanel;

    // Unser Helfer, der die daten.json Datei liest und schreibt
    private final StorageManager storageManager = new StorageManager();

    // --- DESIGN-KONSTANTEN ---
    // "final" bedeutet: Diese Werte können nicht mehr verändert werden.
    private final Font MAIN_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private final Color ACCENT_COLOR = new Color(88, 166, 255);
    private final Color BG_LEFT_COLOR = new Color(33, 33, 33);
    private final Color BG_RIGHT_COLOR = new Color(43, 43, 43);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color BORDER_COLOR = new Color(70, 70, 70);

    /**
     * Der Konstruktor: Wird genau einmal aufgerufen, wenn wir "new MainFrame()" sagen.
     * Hier stellen wir das Fenster ein.
     */
    public MainFrame() {
        setTitle("Meine ToDo App (Final Version)");
        setSize(900, 600); // Breite x Höhe in Pixeln
        setLocationRelativeTo(null); // NULL zentriert das Fenster genau in der Bildschirmmitte

        // WICHTIG: Standardmäßig beendet Java das Programm sofort, wenn man auf das X drückt.
        // Wir sagen hier: "Mach gar nichts! Wir kümmern uns selbst darum, weil wir vorher speichern wollen."
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // 1. Die grafische Oberfläche aufbauen
        initUI();

        // 2. Die alten Daten aus der Datei in die linke Liste laden
        loadData();

        // 3. Dem Fenster einen "Zuhörer" (Listener) geben, der darauf wartet, dass jemand auf das rote X klickt.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ignored) {
                saveData();     // Bevor das Fenster schließt, rufen wir unsere Speichern-Methode auf
                System.exit(0); // DANN beenden wir das Java-Programm sauber (0 heißt "ohne Fehler")
            }
        });
    }

    /**
     * Baut alle Container, Buttons und Listen zusammen.
     */
    private void initUI() {
        // Ein SplitPane ist ein Container, der den Bildschirm in zwei Hälften teilt,
        // mit einem Balken in der Mitte, den der User verschieben kann.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(2); // Macht den Trennbalken dünn und modern
        splitPane.setBackground(BG_LEFT_COLOR);

        // --- LINKE SEITE (Navigation) ---
        // Das BorderLayout teilt eine Fläche in 5 Zonen: NORTH, SOUTH, EAST, WEST und CENTER.
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Randabstand (Padding)
        leftPanel.setBackground(BG_LEFT_COLOR);

        // Liste initialisieren und einfärben
        listModel = new DefaultListModel<>();
        navigationList = new JList<>(listModel);
        navigationList.setFont(MAIN_FONT);
        navigationList.setFixedCellHeight(35);
        navigationList.setForeground(TEXT_COLOR);
        navigationList.setBackground(BG_LEFT_COLOR);
        navigationList.setSelectionBackground(ACCENT_COLOR); // Farbe beim Anklicken
        navigationList.setSelectionForeground(Color.WHITE);

        // Ein JScrollPane sorgt dafür, dass man scrollen kann, wenn es zu viele Listen werden.
        JScrollPane listScrollPane = new JScrollPane(navigationList);
        listScrollPane.setBorder(null);
        listScrollPane.getViewport().setBackground(BG_LEFT_COLOR);

        // Die Liste in die Mitte (CENTER) der linken Seite packen
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        // Panel für die zwei Buttons. Ein GridLayout ordnet Dinge wie in einer Tabelle an.
        // Hier: 2 Zeilen, 1 Spalte, 10 Pixel Abstand dazwischen.
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.setBackground(BG_LEFT_COLOR);

        JButton newTextListBtn = createStyledButton("Neue Text-Liste");
        JButton newCheckboxListBtn = createStyledButton("Neue Checkbox-Liste");

        buttonPanel.add(newTextListBtn);
        buttonPanel.add(newCheckboxListBtn);

        // Die Buttons ganz unten (SOUTH) in die linke Seite packen
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- AKTIONEN FÜR DIE BUTTONS ---
        // "ignored ->" ist eine Lambda-Funktion. Sie sagt kurz und knapp:
        // "Wenn geklickt wird, führe den folgenden Block aus."
        newTextListBtn.addActionListener(ignored -> {
            // Zeigt ein kleines Pop-up an, in dem der Nutzer tippen kann
            String title = JOptionPane.showInputDialog(this, "Titel der Text-Liste:", "Neu", JOptionPane.PLAIN_MESSAGE);
            // Nur wenn er nicht "Abbrechen" gedrückt hat und Text drin steht...
            if (title != null && !title.trim().isEmpty()) {
                // ... erstellen wir ein neues Daten-Objekt und packen es in die Liste links
                listModel.addElement(new TextToDoList(title));
            }
        });

        newCheckboxListBtn.addActionListener(ignored -> {
            String title = JOptionPane.showInputDialog(this, "Titel der Checkbox-Liste:", "Neu", JOptionPane.PLAIN_MESSAGE);
            if (title != null && !title.trim().isEmpty()) {
                listModel.addElement(new CheckboxToDoList(title));
            }
        });

        // Wenn jemand auf einen Eintrag in der linken Liste klickt...
        navigationList.addListSelectionListener(ignored -> {
            ToDoList selected = navigationList.getSelectedValue(); // Welcher wurde geklickt?
            if (selected != null) {
                updateRightPanel(selected); // Baue die rechte Bildschirmseite neu auf!
            }
        });

        // --- RECHTE SEITE (Inhalt) ---
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(BG_RIGHT_COLOR);

        // Start-Bildschirm, wenn noch nichts ausgewählt ist
        JLabel hintLabel = new JLabel("Wähle eine Liste aus.");
        hintLabel.setFont(MAIN_FONT);
        hintLabel.setForeground(new Color(150, 150, 150));
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER); // Text mittig ausrichten
        rightPanel.add(hintLabel, BorderLayout.CENTER);

        // Wir fügen die linke und rechte Seite in unser SplitPane (die Trennwand) ein
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(250); // Der Trennbalken ist standardmäßig 250 Pixel von links

        // Letzter Schritt: Das fertige SplitPane direkt ins Hauptfenster packen
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Diese Methode wird immer aufgerufen, wenn links eine Liste angeklickt wird.
     * Sie schmeißt alles auf der rechten Seite weg und baut es passend zum Typ neu auf.
     */
    private void updateRightPanel(ToDoList selectedList) {
        rightPanel.removeAll(); // Erstmal alles Alte wegwerfen

        // Titel oben auf die rechte Seite setzen (NORTH)
        JLabel titleLabel = new JLabel(selectedList.getTitle());
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        rightPanel.add(titleLabel, BorderLayout.NORTH);

        // "instanceof" prüft: Um was für eine Klasse handelt es sich bei der ausgewählten Liste genau?

        if (selectedList instanceof TextToDoList) {
            // --- FALL A: Es ist eine Fließtext-Liste ---

            // Wir "casten" (umwandeln) das allgemeine ToDoList Objekt zu einem TextToDoList Objekt,
            // damit wir Zugriff auf die Methode "getContent()" haben.
            TextToDoList textList = (TextToDoList) selectedList;

            JTextArea textArea = new JTextArea(textList.getContent());
            textArea.setFont(MAIN_FONT);
            textArea.setForeground(TEXT_COLOR);
            textArea.setBackground(BG_RIGHT_COLOR);
            textArea.setCaretColor(Color.WHITE); // Farbe des blinkenden Text-Cursors
            textArea.setLineWrap(true); // Automatischer Zeilenumbruch am Fensterrand
            textArea.setMargin(new Insets(15, 15, 15, 15));

            // Dieser Listener überwacht das Textfeld in Echtzeit.
            // Egal ob getippt (insert), gelöscht (remove) oder markiert ersetzt (changed) wird:
            // Wir speichern den aktuellen Text sofort in unserem Datenmodell!
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent ignored) { textList.setContent(textArea.getText()); }
                public void removeUpdate(DocumentEvent ignored) { textList.setContent(textArea.getText()); }
                public void changedUpdate(DocumentEvent ignored) { textList.setContent(textArea.getText()); }
            });

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

            // Textfeld in die Mitte der rechten Seite packen
            rightPanel.add(scrollPane, BorderLayout.CENTER);

        } else if (selectedList instanceof CheckboxToDoList) {
            // --- FALL B: Es ist eine Checkbox-Liste ---

            CheckboxToDoList checkboxList = (CheckboxToDoList) selectedList;

            JPanel mainView = new JPanel(new BorderLayout());
            mainView.setBackground(BG_RIGHT_COLOR);

            // Hier landen unsere Checkboxen.
            // Ein BoxLayout mit Y_AXIS ordnet alle neuen Elemente strikt von oben nach unten an.
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBackground(BG_RIGHT_COLOR);

            // Der untere Bereich mit Texteingabe und "Hinzufügen"-Button
            JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            inputPanel.setBackground(BG_RIGHT_COLOR);

            JTextField inputField = new JTextField();
            inputField.setFont(MAIN_FONT);
            inputField.setBackground(new Color(55, 55, 55));
            inputField.setForeground(TEXT_COLOR);
            inputField.setCaretColor(Color.WHITE);
            inputField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

            JButton addBtn = createStyledButton("Hinzufügen");

            inputPanel.add(inputField, BorderLayout.CENTER); // Textfeld in die Mitte
            inputPanel.add(addBtn, BorderLayout.EAST);       // Button rechts daneben

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            scrollPane.getViewport().setBackground(BG_RIGHT_COLOR);

            mainView.add(scrollPane, BorderLayout.CENTER);
            mainView.add(inputPanel, BorderLayout.SOUTH);

            // Was passiert, wenn man "Hinzufügen" klickt?
            addBtn.addActionListener(ignored -> {
                // Wenn das Feld nicht leer ist...
                if (!inputField.getText().trim().isEmpty()) {
                    // Neues Item in unser Datenmodell packen
                    checkboxList.addItem(new ToDoItem(inputField.getText()));
                    // Textfeld wieder leeren
                    inputField.setText("");
                    // Unsere Hilfsmethode aufrufen, um die Checkboxen neu zu malen
                    renderCheckboxes(checkboxList, listPanel);
                }
            });
            // Enter-Taste im Textfeld bewirkt dasselbe wie ein Button-Klick
            inputField.addActionListener(ignored -> addBtn.doClick());

            // Vorhandene Checkboxen einmalig zeichnen
            renderCheckboxes(checkboxList, listPanel);

            rightPanel.add(mainView, BorderLayout.CENTER);
        }

        // Da wir gerade im laufenden Betrieb Container gelöscht und neue hinzugefügt haben,
        // müssen wir Swing zwingen, das Layout neu zu berechnen und den Bildschirm neu zu zeichnen.
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    /**
     * Zeichnet die einzelnen Checkbox-Zeilen in das Panel.
     */
    private void renderCheckboxes(CheckboxToDoList list, JPanel listPanel) {
        listPanel.removeAll(); // Erst alles Alte löschen
        list.sortItems();      // Wunschkriterium 1: Erledigte Einträge ans Ende der Liste schieben

        // Für jedes ToDoItem-Objekt aus unserem Datenmodell...
        for (ToDoItem item : list.getItems()) {

            // ...basteln wir einen HTML-Text. Grau, wenn erledigt, Weiß, wenn noch offen.
            // HTML erlaubt uns das Durchstreichen (<strike>) direkt im Text der Checkbox.
            String color = item.isDone() ? "#888888" : "#E6E6E6";
            String text = item.isDone() ? "<strike>" + item.getText() + "</strike>" : item.getText();

            JCheckBox cb = new JCheckBox("<html><font color='" + color + "'>" + text + "</font></html>");
            cb.setFont(MAIN_FONT);
            cb.setBackground(BG_RIGHT_COLOR);
            cb.setFocusPainted(false);
            cb.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));

            // Den Haken setzen, wenn das Objekt im Datenmodell auf "done = true" steht
            cb.setSelected(item.isDone());

            // Was passiert, wenn der Nutzer HIER einen Haken rein macht?
            cb.addItemListener(ignored -> {
                // 1. Das Datenmodell aktualisieren
                item.setDone(cb.isSelected());
                // 2. Diese Methode "renderCheckboxes" ruft sich selbst einfach nochmal auf!
                // Dadurch wird die Liste sofort neu sortiert und neu gezeichnet (Rekursion/Refresh).
                renderCheckboxes(list, listPanel);
            });

            listPanel.add(cb); // Checkbox dem Panel hinzufügen
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    /**
     * Hilfsmethode, um nicht jedes Mal 10 Zeilen Code für einen farbigen Button tippen zu müssen.
     */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(MAIN_FONT);
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false); // Verhindert, dass Windows/Mac ihren grauen 3D-Rahmen drübermalen
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand-Symbol beim Drüberfahren
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }

    /**
     * Lädt die Daten aus der JSON-Datei und packt sie in die linke Liste.
     */
    private void loadData() {
        for (ToDoList l : storageManager.load()) {
            listModel.addElement(l);
        }
    }

    /**
     * Zieht alle Listen aus der Oberfläche heraus und übergibt sie dem StorageManager zum Speichern.
     */
    private void saveData() {
        List<ToDoList> lists = new ArrayList<>();
        // Geht alle Elemente des UI-Listen-Modells durch und packt sie in eine Standard Java-ArrayList
        for (int i = 0; i < listModel.size(); i++) {
            lists.add(listModel.getElementAt(i));
        }
        storageManager.save(lists);
    }
}