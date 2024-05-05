package main.java.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DesignedButton extends JButton {
    // Color constants
    private final Color baseColor = new Color(228, 228, 228);
    private final Color hoverColor = new Color(200, 200, 200);
    private final Color pressedColor = new Color(255, 255, 255);
    private Timer colorTransitionTimer; // Timer for color transitions
    private Color currentColor; // Current color of the button

    public DesignedButton(Icon icon) {
        super(icon);
        setBackground(baseColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        currentColor = baseColor;

        // Add listeners for color transitions
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                startColorTransition(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                startColorTransition(baseColor);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                setBackground(pressedColor);
                stopColorTransition();
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                startColorTransition(hoverColor);
            }
        });
    }

    // Start a color transition to the target color
    private void startColorTransition(Color targetColor) {
        if (colorTransitionTimer != null && colorTransitionTimer.isRunning()) {
            colorTransitionTimer.stop();
        }

        // Speed of the color transition
        int animationSpeed = 20;
        colorTransitionTimer = new Timer(animationSpeed, new ActionListener() {
            private int step = 0;
            private final Color startColor = currentColor;

            // Calculate the next color in the transition
            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                int steps = 20;
                float ratio = (float) step / steps;
                currentColor = new Color(
                        (int) (startColor.getRed() + ratio * (targetColor.getRed() - startColor.getRed())),
                        (int) (startColor.getGreen() + ratio * (targetColor.getGreen() - startColor.getGreen())),
                        (int) (startColor.getBlue() + ratio * (targetColor.getBlue() - startColor.getBlue()))
                );
                setBackground(currentColor);
                repaint();

                if (step >= steps) {
                    colorTransitionTimer.stop();
                    currentColor = targetColor;
                }
            }
        });
        colorTransitionTimer.start();
    }

    // Stop the color transition
    private void stopColorTransition() {
        if (colorTransitionTimer != null && colorTransitionTimer.isRunning()) {
            colorTransitionTimer.stop();
        }
    }

    // Paint the button with the current color
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Устанавливаем цвет с использованием currentColor
        g2d.setColor(currentColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

        super.paintComponent(g);
    }
}
