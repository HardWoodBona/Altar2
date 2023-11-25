import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class GameAltarGUI {

    private final JFrame frame;
    private final JToggleButton[] btnsDragonSouls;
    private final JToggleButton[] btns48Souls;
    private final JToggleButton[] btns8Souls;
    private final JTextField dragonTimeField;
    private final JTextField eightHourTimeField;
    private final JLabel lblResults;

    public GameAltarGUI() {
        frame = new JFrame("Game Altar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagConstraints c = new GridBagConstraints();
        frame.setLayout(new GridBagLayout());
        c.insets = new Insets(10,20,10,20);

        c.gridy = 0;
        btnsDragonSouls = createButtons("Dragon Souls", 5, 0, c);
        c.gridy = 1;
        btns48Souls = createButtons("48-hour Souls", 5, 1, c);
        c.gridy = 2;
        btns8Souls = createButtons("8-hour Souls", 9, 2, c);
        c.gridy = 3;
        dragonTimeField = createTextField("Dragon soul time (minutes):", c);
        c.gridy = 4;
        eightHourTimeField = createTextField("8-hour soul time (minutes):", c);
        c.gridy = 5;
        lblResults = new JLabel("<html><center>Select an option<br>to begin...</center></html>");
        frame.add(lblResults, c);

        // c.gridy = 5;
        // frame.add(calculateButton, c);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JToggleButton[] createButtons(String label, int count, int type, GridBagConstraints gb) {
        JPanel panel = new JPanel();
        JLabel labelComponent = new JLabel(label + ":");
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(new GridBagLayout());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 2;
        panel.add(labelComponent, c);

        JToggleButton[] buttons = new JToggleButton[count];
        ButtonGroup buttonGroup = new ButtonGroup();

        c.gridheight = 1;

        for (int i = 0; i < count; i++) {
            c.gridx += 1;
            buttons[i] = new JToggleButton(Integer.toString(i));
            final int index = i;
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleSoulButtonClick(label, index, buttons);
                }
            });

            if (i == 4 && count > 5) {
                c.gridy += 1;
                c.gridx = 1;
            }

            if (i == 8) {
                c.gridy -= 1;
                c.gridheight = 2;
            }

            buttonGroup.add(buttons[i]);
            panel.add(buttons[i], c);
        }

        frame.add(panel, gb);
        return buttons;
    }

    private void handleSoulButtonClick(String label, int index, JToggleButton[] buttons) {
        // JOptionPane.showMessageDialog(frame, "Set " + label + " count to " + count);
        // Set background color when the button is selected
        for (JToggleButton button : buttons) {
            button.setOpaque(false);
            button.setBackground(UIManager.getColor("Panel.background"));
        } 

        if (buttons[index].isSelected()) {
            buttons[index].setOpaque(true);
            buttons[index].setBackground(Color.green);
        }

        updateTotal();
    }

    private JTextField createTextField(String label, GridBagConstraints gb) {
        JPanel panel = new JPanel();
        JLabel labelComponent = new JLabel(label);
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(50, 20));  // Set a preferred size

        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateTotal();
            }
            public void removeUpdate(DocumentEvent e) {
                updateTotal();
            }
            public void insertUpdate(DocumentEvent e) {
                updateTotal();
            }
        });

        panel.add(labelComponent);
        panel.add(textField);
        frame.add(panel, gb);

        return textField;
    }

    private void updateTotal() {
        double total = 0;

        int dragonTime = 0;
        try { dragonTime = Integer.parseInt(dragonTimeField.getText()); } catch(Exception e) {}
        int dragonCount = Integer.parseInt(Arrays.stream(btnsDragonSouls)
            .reduce((result, but) -> but.isSelected() ? but : result)
            .orElse(new JToggleButton("0")).getText());
        total += dragonCount * dragonTime;

        int eightHourTime = 0;
        try { eightHourTime = Integer.parseInt(eightHourTimeField.getText()); } catch(Exception e) {}
        int eightHourCount = Integer.parseInt(Arrays.stream(btns8Souls)
            .reduce((result, but) -> but.isSelected() ? but : result)
            .orElse(new JToggleButton("0")).getText());
        total += eightHourTime * eightHourCount;

        int fortyEightHourTime = Integer.parseInt(Arrays.stream(btns48Souls)
            .reduce((result, but) -> but.isSelected() ? but : result)
            .orElse(new JToggleButton("0")).getText());
        total += fortyEightHourTime * 48 * 60;

        int days = (int) (total / 24 / 60);
        int hours = (int) (total / 24 % 24);

        lblResults.setText("<html><center>Minutes needed: " + total +
            "<br>Days and Hours Before Event: " + days + " days, " + hours + " hours</center></html>");
    }

    private void resetButtons(JToggleButton[] buttons) {
        for (JToggleButton button : buttons) {
            button.setSelected(false);
            button.setBackground(UIManager.getColor("ToggleButton.background"));
        }
    }

    private void showResults(double reducedHours, int days, int hours) {
        JOptionPane.showMessageDialog(frame, "Hours needed: " + reducedHours +
                "\nDays and Hours Before Event: " + days + " days, " + hours + " hours");
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameAltarGUI());
    }
}
