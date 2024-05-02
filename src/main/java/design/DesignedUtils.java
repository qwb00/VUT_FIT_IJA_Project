package main.java.design;

import javax.swing.*;
import java.awt.*;

public class DesignedUtils {
    public static int showCustomConfirmDialog(Component parent, String message, String title, String[] options) {
        // Создаем пользовательский диалог
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