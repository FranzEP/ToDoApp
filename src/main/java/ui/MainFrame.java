package ui;

import model.ToDoList;
import model.TextToDoList;
import model.CheckboxToDoList;
import model.ToDoItem;
import storage.StorageManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private DefaultListModel<ToDoList> listModel;
    private JList<ToDoList> navigationList;
    private JPanel rightPanel;
    private final StorageManager storageManager = new StorageManager();

    // Konstanten für das allgemeine Design
    private final Font MAIN_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private final Color ACCENT_COLOR = new Color(88, 166, 255);
    private final Color DANGER_COLOR = new Color(231, 76, 60); // NEU: Ein warnendes Rot fürs Löschen
    private final Color BG_LEFT_COLOR = new Color(33, 33, 33);
    private final Color BG_RIGHT_COLOR = new Color(43, 43, 43);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color BORDER_COLOR = new Color(70, 70, 70);

    public MainFrame() {
        setTitle("Meine ToDo App ");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initUI();
        loadData();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ignored) {
                saveData();
                System.exit(0);
            }
        });
    }

    private void initUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(2);
        splitPane.setBackground(BG_LEFT_COLOR);

        // linke Seite
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setBackground(BG_LEFT_COLOR);

        listModel = new DefaultListModel<>();
        navigationList = new JList<>(listModel);
        navigationList.setFont(MAIN_FONT);
        navigationList.setFixedCellHeight(35);
        navigationList.setForeground(TEXT_COLOR);
        navigationList.setBackground(BG_LEFT_COLOR);
        navigationList.setSelectionBackground(ACCENT_COLOR);
        navigationList.setSelectionForeground(Color.WHITE);

        JScrollPane listScrollPane = new JScrollPane(navigationList);
        listScrollPane.setBorder(null);
        listScrollPane.getViewport().setBackground(BG_LEFT_COLOR);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        // der Platz für drei buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.setBackground(BG_LEFT_COLOR);

        JButton newTextListBtn = createStyledButton("Neue Text-Liste", ACCENT_COLOR);
        JButton newCheckboxListBtn = createStyledButton("Neue Checkbox-Liste", ACCENT_COLOR);
        JButton deleteListBtn = createStyledButton("Ausgewählte Liste löschen", DANGER_COLOR);

        buttonPanel.add(newTextListBtn);
        buttonPanel.add(newCheckboxListBtn);
        buttonPanel.add(deleteListBtn); // NEU: Der rote Löschen-Button
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Aktionen für die Buttons auf der linken Seite
        newTextListBtn.addActionListener(ignored -> {
            String title = JOptionPane.showInputDialog(this, "Titel der Text-Liste:", "Neu", JOptionPane.PLAIN_MESSAGE);
            if (title != null && !title.trim().isEmpty()) {
                listModel.addElement(new TextToDoList(title));
            }
        });

        newCheckboxListBtn.addActionListener(ignored -> {
            String title = JOptionPane.showInputDialog(this, "Titel der Checkbox-Liste:", "Neu", JOptionPane.PLAIN_MESSAGE);
            if (title != null && !title.trim().isEmpty()) {
                listModel.addElement(new CheckboxToDoList(title));
            }
        });

        // Logik um ganze Listen zu löschen
        deleteListBtn.addActionListener(ignored -> {
            ToDoList selected = navigationList.getSelectedValue();
            if (selected != null) {
                // Sicherheitsabfrage, damit man nichts aus Versehen löscht
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Willst du die Liste '" + selected.getTitle() + "' wirklich löschen?",
                        "Löschen bestätigen",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    listModel.removeElement(selected); // aus den Daten entfernen
                    resetRightPanel(); // rechte Seite wieder leer machen
                }
            }
        });

        navigationList.addListSelectionListener(ignored -> {
            ToDoList selected = navigationList.getSelectedValue();
            if (selected != null) updateRightPanel(selected);
        });

        //rechte Seite
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(BG_RIGHT_COLOR);
        resetRightPanel(); // Setzt den initialen Start-Text

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);
    }

    // Hilfsmethode, die aufgerufen wird, wenn noch keine Liste ausgewählt wurde
    // oder wenn eine Liste gerade gelöscht wurde
    private void resetRightPanel() {
        rightPanel.removeAll();
        JLabel hintLabel = new JLabel("Wähle eine Liste aus.");
        hintLabel.setFont(MAIN_FONT);
        hintLabel.setForeground(new Color(150, 150, 150));
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(hintLabel, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void updateRightPanel(ToDoList selectedList) {
        rightPanel.removeAll();
        JLabel titleLabel = new JLabel(selectedList.getTitle());
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        rightPanel.add(titleLabel, BorderLayout.NORTH);

        if (selectedList instanceof TextToDoList) {
            TextToDoList textList = (TextToDoList) selectedList;
            JTextArea textArea = new JTextArea(textList.getContent());
            textArea.setFont(MAIN_FONT);
            textArea.setForeground(TEXT_COLOR);
            textArea.setBackground(BG_RIGHT_COLOR);
            textArea.setCaretColor(Color.WHITE);
            textArea.setLineWrap(true);
            textArea.setMargin(new Insets(15, 15, 15, 15));

            textArea.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent ignored) { textList.setContent(textArea.getText()); }
                public void removeUpdate(DocumentEvent ignored) { textList.setContent(textArea.getText()); }
                public void changedUpdate(DocumentEvent ignored) { textList.setContent(textArea.getText()); }
            });

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            rightPanel.add(scrollPane, BorderLayout.CENTER);

        } else if (selectedList instanceof CheckboxToDoList) {
            CheckboxToDoList checkboxList = (CheckboxToDoList) selectedList;
            JPanel mainView = new JPanel(new BorderLayout());
            mainView.setBackground(BG_RIGHT_COLOR);

            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBackground(BG_RIGHT_COLOR);

            JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            inputPanel.setBackground(BG_RIGHT_COLOR);

            JTextField inputField = new JTextField();
            inputField.setFont(MAIN_FONT);
            inputField.setBackground(new Color(55, 55, 55));
            inputField.setForeground(TEXT_COLOR);
            inputField.setCaretColor(Color.WHITE);
            inputField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

            JButton addBtn = createStyledButton("Hinzufügen", ACCENT_COLOR);
            inputPanel.add(inputField, BorderLayout.CENTER);
            inputPanel.add(addBtn, BorderLayout.EAST);

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            scrollPane.getViewport().setBackground(BG_RIGHT_COLOR);

            mainView.add(scrollPane, BorderLayout.CENTER);
            mainView.add(inputPanel, BorderLayout.SOUTH);

            addBtn.addActionListener(ignored -> {
                if (!inputField.getText().trim().isEmpty()) {
                    checkboxList.addItem(new ToDoItem(inputField.getText()));
                    inputField.setText("");
                    renderCheckboxes(checkboxList, listPanel);
                }
            });
            inputField.addActionListener(ignored -> addBtn.doClick());

            renderCheckboxes(checkboxList, listPanel);
            rightPanel.add(mainView, BorderLayout.CENTER);
        }
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void renderCheckboxes(CheckboxToDoList list, JPanel listPanel) {
        listPanel.removeAll();
        list.sortItems();

        for (ToDoItem item : list.getItems()) {
            //ein Row-Panel, um Checkbox (links) und Löschen-Button (rechts) nebeneinander zu setzen
            JPanel rowPanel = new JPanel(new BorderLayout());
            rowPanel.setBackground(BG_RIGHT_COLOR);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Verhindert, dass Zeilen zu hoch werden

            String color = item.isDone() ? "#888888" : "#E6E6E6";
            String text = item.isDone() ? "<strike>" + item.getText() + "</strike>" : item.getText();
            JCheckBox cb = new JCheckBox("<html><font color='" + color + "'>" + text + "</font></html>");
            cb.setFont(MAIN_FONT);
            cb.setBackground(BG_RIGHT_COLOR);
            cb.setFocusPainted(false);
            cb.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
            cb.setSelected(item.isDone());

            cb.addItemListener(ignored -> {
                item.setDone(cb.isSelected());
                renderCheckboxes(list, listPanel);
            });

            // der kleine Löschen-Button für einzelne Einträge
            JButton deleteItemBtn = new JButton("✖");
            deleteItemBtn.setFont(MAIN_FONT);
            deleteItemBtn.setForeground(DANGER_COLOR); // Rotes Kreuz
            deleteItemBtn.setBackground(BG_RIGHT_COLOR);
            deleteItemBtn.setBorderPainted(false);
            deleteItemBtn.setContentAreaFilled(false);
            deleteItemBtn.setFocusPainted(false);
            deleteItemBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            deleteItemBtn.addActionListener(ignored -> {
                list.getItems().remove(item); // Direkt aus der Daten-Liste entfernen
                renderCheckboxes(list, listPanel); // Neu zeichnen
            });

            rowPanel.add(cb, BorderLayout.CENTER);
            rowPanel.add(deleteItemBtn, BorderLayout.EAST);
            listPanel.add(rowPanel);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    //die angepasste Hilfsmethode nimmt jetzt eine Farbe als Parameter, damit wir blaue UND rote Buttons machen können
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(MAIN_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }

    private void loadData() {
        for (ToDoList l : storageManager.load()) listModel.addElement(l);
    }

    private void saveData() {
        List<ToDoList> lists = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) lists.add(listModel.getElementAt(i));
        storageManager.save(lists);
    }
}
