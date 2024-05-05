/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 * Utility class providing custom dialog functionalities with a consistent design.
 */
package main.java.design;

import javax.swing.*;
import java.awt.*;

public class DesignedUtils {

    /**
     * Displays a custom confirmation dialog with the specified options and message.
     *
     * @param parent  The parent component for the dialog.
     * @param message The message to display in the dialog.
     * @param title   The title of the dialog.
     * @param options An array of options to show in the dialog.
     * @return The index of the selected option, or -1 if no option is selected.
     */
    public static int showCustomConfirmDialog(Component parent, String message, String title, String[] options) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        panel.add(label, BorderLayout.CENTER);

        return JOptionPane.showOptionDialog(parent, panel, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
    }

    /**
     * Displays a custom input dialog with the specified input component.
     *
     * @param parent        The parent component for the dialog.
     * @param inputComponent The input component to display in the dialog.
     * @param title         The title of the dialog.
     * @return The index of the selected option, or -1 if no option is selected.
     */
    public static int showCustomInputDialog(Component parent, JComponent inputComponent, String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(inputComponent, BorderLayout.CENTER);

        return JOptionPane.showOptionDialog(parent, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, null, null);
    }
}