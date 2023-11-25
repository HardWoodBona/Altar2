import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class GameAltarGUI implements FocusListener {

    private final JFrame frame;
    private final JToggleButton[] btnsDragonSouls;
    private final JToggleButton[] btns48Souls;
    private final JToggleButton[] btns8Souls;
    private final JTextField percentageReductionField;
    private final JTextField eightHourTimeField;

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
        percentageReductionField = createTextField("Time Reduction Percentage:", c);
        c.gridy = 4;
        eightHourTimeField = createTextField("8-hour soul time (minutes):", c);

        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateAndDisplayResults();
            }
        });

        // frame.add(Box.createRigidArea(new Dimension(0, 10)));  // Add some spacing
        c.gridy = 5;
        frame.add(calculateButton, c);

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
    }

    private JTextField createTextField(String label, GridBagConstraints gb) {
        JPanel panel = new JPanel();
        JLabel labelComponent = new JLabel(label);
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(50, 20));  // Set a preferred size
        textField.getDocument().putProperty("owner", textField); //set the owner
        textField.addFocusListener(this);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                textChanged(e);
            }
            public void removeUpdate(DocumentEvent e) {
                textChanged(e);
            }
            public void insertUpdate(DocumentEvent e) {
                textChanged(e);
            }
        });

        panel.add(labelComponent);
        panel.add(textField);
        frame.add(panel, gb);
        return textField;
    }

    private Object modifiedField;

    public void focusGained(FocusEvent e) {
        modifiedField = e.getComponent();
    }
    
    public void focusLost(FocusEvent e) {}

    private void textChanged(DocumentEvent e) {
        Object owner = e.getDocument().getProperty("owner");

        if (modifiedField == null) {
            modifiedField = owner;
        }

        // ignoreNextTextEvent = true;

        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);

        if (owner == percentageReductionField && modifiedField == percentageReductionField) {
            modifiedField = owner;
            eightHourTimeField.setText("test");
        System.out.println("percent");
        } else if (owner == eightHourTimeField && modifiedField == eightHourTimeField) {
            modifiedField = owner;
            try {
                double eightHourTime = Double.parseDouble(eightHourTimeField.getText());
                double percentageEquivalent = (1 - eightHourTime / 480) * 100;
                String result = df.format(percentageEquivalent);
                System.out.println(eightHourTime / 480);
                percentageReductionField.setText(result);
            System.out.println("eight");
            } catch (Exception ex) {
                return;
            }
        }
    }

    private void calculateAndDisplayResults() {
        try {
            // Calculate total hours based on user input
            int total48Hours = calculateButtonTotal(btns48Souls, 48, false);
            int totalDragonHours = calculateButtonTotal(btnsDragonSouls, 48, true);
            int total8Hours = calculateButtonTotal(btns8Souls, 8, true);

            int reducedHours = total48Hours + total8Hours + totalDragonHours;

            int days = (int) (reducedHours / 24);
            int hours = (int) (reducedHours % 24);

            showResults(reducedHours, days, hours);
        } catch (NumberFormatException ex) {
            showErrorMessage("Please enter a valid number for percentage reduction.");
        }
    }

    private int calculateButtonTotal(JToggleButton[] buttons, int duration, boolean applyReduction) {
        double percentageReduction = Double.parseDouble(percentageReductionField.getText());
        int total = 0;
        for (JToggleButton button : buttons) {
            if (button.isSelected()) {
                // Parse the button label to get the value
                int value = Integer.parseInt(button.getText());
                total += value * duration * (1 - percentageReduction / 100);
            }
        }
        return total;
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
