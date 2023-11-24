import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameAltarGUI {

    private final JFrame frame;
    private final JToggleButton[] soulButtons;
    private final JToggleButton[] otherSoulButtons;
    private final JTextField percentageReductionField;

    public GameAltarGUI() {
        frame = new JFrame("Game Altar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        soulButtons = createButtons("48-hour Souls", 4);
        otherSoulButtons = createButtons("8-hour Souls", 5);
        percentageReductionField = createTextField("Time Reduction Percentage:");

        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateAndDisplayResults();
            }
        });

        frame.add(Box.createRigidArea(new Dimension(0, 10)));  // Add some spacing
        frame.add(calculateButton);
        frame.setVisible(true);
    }

    private JToggleButton[] createButtons(String label, int count) {
        JPanel panel = new JPanel();
        JLabel labelComponent = new JLabel(label + ":");
        panel.add(labelComponent);

        JToggleButton[] buttons = new JToggleButton[count];
        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < count; i++) {
            buttons[i] = new JToggleButton(Integer.toString(i + 1));
            final int index = i;
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleSoulButtonClick(label, index + 1, buttons[index]);
                }
            });
            buttonGroup.add(buttons[i]);
            panel.add(buttons[i]);
        }

        frame.add(panel);
        return buttons;
    }

    private void handleSoulButtonClick(String label, int count, JToggleButton button) {
        JOptionPane.showMessageDialog(frame, "Set " + label + " count to " + count);
        // Set background color when the button is selected
        if (button.isSelected()) {
            button.setBackground(Color.GREEN);  // You can choose a different color
        } else {
            button.setBackground(UIManager.getColor("ToggleButton.background"));
        }
    }

    private JTextField createTextField(String label) {
        JPanel panel = new JPanel();
        JLabel labelComponent = new JLabel(label);
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(50, 20));  // Set a preferred size
        panel.add(labelComponent);
        panel.add(textField);
        frame.add(panel);
        return textField;
    }

    private void calculateAndDisplayResults() {
        try {
            double percentageReduction = Double.parseDouble(percentageReductionField.getText());

            // Calculate total hours based on user input
            int total48Hours = calculateButtonTotal(soulButtons, 48);
            int totalOtherHours = calculateButtonTotal(otherSoulButtons, 8);

            int totalHours = total48Hours + totalOtherHours;
            double reducedHours = totalHours * (1 - percentageReduction / 100);

            int days = (int) (reducedHours / 24);
            int hours = (int) (reducedHours % 24);

            showResults(totalHours, reducedHours, days, hours);

            // Reset button states
            resetButtons(soulButtons);
            resetButtons(otherSoulButtons);
            percentageReductionField.setText("");
        } catch (NumberFormatException ex) {
            showErrorMessage("Please enter a valid number for percentage reduction.");
        }
    }

    private int calculateButtonTotal(JToggleButton[] buttons, int duration) {
        int total = 0;
        for (JToggleButton button : buttons) {
            if (button.isSelected()) {
                // Parse the button label to get the value
                int value = Integer.parseInt(button.getText());
                total += value * duration;
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

    private void showResults(int totalHours, double reducedHours, int days, int hours) {
        JOptionPane.showMessageDialog(frame, "Total Hours: " + totalHours +
                "\nReduced Hours: " + reducedHours +
                "\nDays and Hours Before Event: " + days + " days, " + hours + " hours");
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameAltarGUI());
    }
}
