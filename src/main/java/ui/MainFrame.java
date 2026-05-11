package ui;

import model.ToDoList;
import model.TextToDoList;
import model.CheckboxToDoList;
import model.ToDoItem;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class MainFrame extends JFrame {

    private DefaultListModel<ToDoList> listModel;
    private JList<ToDoList> navigationList;
    private JPanel rightPanel;

    // --- SCHRIFTARTEN ---
    private final Font MAIN_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);

    // --- NEU: DARK MODE FARBPALETTE ---
    // Ein leuchtendes Hellblau, das auf dunklem Grund gut zur Geltung kommt
    private final Color ACCENT_COLOR = new Color(88, 166, 255);
    // Sehr dunkles Grau für die linke Navigation
    private final Color BG_LEFT_COLOR = new Color(33, 33, 33);
    // Etwas helleres Dunkelgrau für die rechte Inhaltsseite (Tiefen-Effekt)
    private final Color BG_RIGHT_COLOR = new Color(43, 43, 43);
    // Helle Textfarbe (fast Weiß, strengt die Augen weniger an als reines Weiß)
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    // Rahmenfarbe für Scrollbars und Textfelder
    private final Color BORDER_COLOR = new Color(70, 70, 70);

    public MainFrame() {
        setTitle("Meine ToDo App (Dark Mode)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Sorgt dafür, dass der Hintergrund des Hauptfensters auch dunkel ist
        getContentPane().setBackground(BG_LEFT_COLOR);

        initUI();
    }

    private void initUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(2);
        // Hintergrundfarbe des SplitPanes anpassen
        splitPane.setBackground(BG_LEFT_COLOR);

        // --- LINKE SEITE (Navigation) ---
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
        // ScrollPane Hintergrund anpassen
        listScrollPane.getViewport().setBackground(BG_LEFT_COLOR);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.setBackground(BG_LEFT_COLOR);

        JButton newTextListBtn = createStyledButton("Neue Text-Liste");
        JButton newCheckboxListBtn = createStyledButton("Neue Checkbox-Liste");

        buttonPanel.add(newTextListBtn);
        buttonPanel.add(newCheckboxListBtn);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- AKTIONEN FÜR DIE BUTTONS ---
        newTextListBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(this, "Titel der Text-Liste:", "Neu", JOptionPane.PLAIN_MESSAGE);
            if (title != null && !title.trim().isEmpty()) {
                listModel.addElement(new TextToDoList(title));
            }
        });

        newCheckboxListBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(this, "Titel der Checkbox-Liste:", "Neu", JOptionPane.PLAIN_MESSAGE);
            if (title != null && !title.trim().isEmpty()) {
                listModel.addElement(new CheckboxToDoList(title));
            }
        });

        navigationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ToDoList selected = navigationList.getSelectedValue();
                if (selected != null) {
                    updateRightPanel(selected);
                }
            }
        });

        // --- RECHTE SEITE (Inhalt) ---
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(BG_RIGHT_COLOR); // Dunkler Hintergrund rechts

        JLabel hintLabel = new JLabel("Wähle links eine Liste aus oder erstelle eine neue.");
        hintLabel.setFont(MAIN_FONT);
        hintLabel.setForeground(new Color(150, 150, 150)); // Dunkleres Grau für den Hinweis
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(hintLabel, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(250);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Erstellt einen farblich angepassten Button.
     */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(MAIN_FONT);
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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
            textArea.setBackground(BG_RIGHT_COLOR); // Dunkler Hintergrund für Textfeld
            textArea.setCaretColor(Color.WHITE);    // WICHTIG: Weißer blinkender Cursor!
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setMargin(new Insets(15, 15, 15, 15));

            textArea.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { textList.setContent(textArea.getText()); }
                public void removeUpdate(DocumentEvent e) { textList.setContent(textArea.getText()); }
                public void changedUpdate(DocumentEvent e) { textList.setContent(textArea.getText()); }
            });

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            rightPanel.add(scrollPane, BorderLayout.CENTER);

        } else if (selectedList instanceof CheckboxToDoList) {
            CheckboxToDoList checkboxList = (CheckboxToDoList) selectedList;

            JPanel mainCheckboxView = new JPanel(new BorderLayout());
            mainCheckboxView.setBackground(BG_RIGHT_COLOR);

            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBackground(BG_RIGHT_COLOR);

            JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            inputPanel.setBackground(BG_RIGHT_COLOR);

            JTextField inputField = new JTextField();
            inputField.setFont(MAIN_FONT);
            inputField.setBackground(new Color(55, 55, 55)); // Etwas helleres Feld zur Abhebung
            inputField.setForeground(TEXT_COLOR);
            inputField.setCaretColor(Color.WHITE); // Weißer Cursor im Eingabefeld

            inputField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));

            JButton addBtn = createStyledButton("Hinzufügen");

            inputPanel.add(inputField, BorderLayout.CENTER);
            inputPanel.add(addBtn, BorderLayout.EAST);

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            scrollPane.getViewport().setBackground(BG_RIGHT_COLOR);

            mainCheckboxView.add(scrollPane, BorderLayout.CENTER);
            mainCheckboxView.add(inputPanel, BorderLayout.SOUTH);

            addBtn.addActionListener(e -> {
                String text = inputField.getText();
                if (text != null && !text.trim().isEmpty()) {
                    checkboxList.addItem(new ToDoItem(text));
                    inputField.setText("");
                    renderCheckboxes(checkboxList, listPanel);
                }
            });
            inputField.addActionListener(e -> addBtn.doClick());

            renderCheckboxes(checkboxList, listPanel);
            rightPanel.add(mainCheckboxView, BorderLayout.CENTER);
        }

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void renderCheckboxes(CheckboxToDoList list, JPanel listPanel) {
        listPanel.removeAll();
        list.sortItems();

        for (ToDoItem item : list.getItems()) {
            // BUGFIX: Wir zwingen Swing über HTML, die korrekte Farbe zu nutzen.
            // #E6E6E6 ist unser helles Grau für offene Einträge, #888888 für erledigte.
            String hexColor = item.isDone() ? "#888888" : "#E6E6E6";

            // Text zusammensetzen: Durchgestrichen, falls erledigt, sonst normal.
            String textContent = item.isDone() ? "<strike>" + item.getText() + "</strike>" : item.getText();

            // Alles in HTML verpacken und die Farbe hart setzen
            String finalHtmlText = "<html><font color='" + hexColor + "'>" + textContent + "</font></html>";

            JCheckBox cb = new JCheckBox(finalHtmlText);
            cb.setFont(MAIN_FONT);
            // Wir müssen die Textfarbe hier nicht mehr setzen, da HTML das übernimmt!
            cb.setBackground(BG_RIGHT_COLOR); // Dunkler Hintergrund anpassen
            cb.setFocusPainted(false);
            cb.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));

            cb.setSelected(item.isDone());

            cb.addItemListener(e -> {
                item.setDone(cb.isSelected());
                renderCheckboxes(list, listPanel);
            });

            listPanel.add(cb);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}