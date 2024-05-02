package main.java.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DesignedField extends JPanel {
    private Color baseColor = new Color(255, 220, 150);
    private Color hoverColor = new Color(255, 180, 100);
    private Color borderColor = new Color(0, 0, 0); // Цвет границ

    public DesignedField() {
        setBackground(baseColor);
        setBorder(BorderFactory.createLineBorder(borderColor, 1)); // Граница ячейки
        setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Измените на fillRect, чтобы убрать скругленные углы
    }
}
