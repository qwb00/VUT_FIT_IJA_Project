package main.java.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DesignedField extends JPanel {
    // Color constants
    private final Color baseColor = new Color(255, 220, 150);
    private final Color hoverColor = new Color(255, 180, 100);

    public DesignedField() {
        setBackground(baseColor);
        Color borderColor = new Color(0, 0, 0);
        setBorder(BorderFactory.createLineBorder(borderColor, 1)); // Граница ячейки
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add listeners for color transitions
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

    // Paint the background of the field
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Измените на fillRect, чтобы убрать скругленные углы
    }
}
