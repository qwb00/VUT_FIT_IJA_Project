/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 * A custom-designed field for the user interface.
 * It changes color on hover and has a bordered appearance.
 */
package main.java.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A custom-designed field for the user interface.
 * It changes color on hover and has a bordered appearance.
 */
public class DesignedField extends JPanel {
    private final Color baseColor = new Color(255, 220, 150);
    private final Color hoverColor = new Color(255, 180, 100);

    /**
     * Constructs a DesignedField with predefined colors and interactions.
     */
    public DesignedField() {
        setBackground(baseColor);
        Color borderColor = new Color(0, 0, 0);
        setBorder(BorderFactory.createLineBorder(borderColor, 1)); // Граница ячейки
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add mouse listeners to handle hover color changes
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(baseColor);
            }
        });
    }

    /**
     * Paints the field with the current background color.
     *
     * @param g The Graphics object to protect.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
