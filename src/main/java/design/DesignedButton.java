/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 */
package main.java.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom button class with a specific design and color transition effects.
 */
public class DesignedButton extends JButton {
    private final Color baseColor = new Color(228, 228, 228);
    private final Color hoverColor = new Color(200, 200, 200);
    private final Color pressedColor = new Color(255, 255, 255);
    private Timer colorTransitionTimer;
    private Color currentColor;

    /**
     * Constructs a DesignedButton with the specified icon.
     *
     * @param icon The icon to display on the button.
     */
    public DesignedButton(Icon icon) {
        super(icon);
        setBackground(baseColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        currentColor = baseColor;

        // Add mouse listeners to change button colors on hover and press
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

    /**
     * Starts the color transition animation towards the target color.
     *
     * @param targetColor The target color for the transition.
     */
    private void startColorTransition(Color targetColor) {
        if (colorTransitionTimer != null && colorTransitionTimer.isRunning()) {
            colorTransitionTimer.stop();
        }

        int animationSpeed = 20;
        colorTransitionTimer = new Timer(animationSpeed, new ActionListener() {
            private int step = 0;
            private final Color startColor = currentColor;

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

    /**
     * Stops the current color transition animation.
     */
    private void stopColorTransition() {
        if (colorTransitionTimer != null && colorTransitionTimer.isRunning()) {
            colorTransitionTimer.stop();
        }
    }

    /**
     * Paints the button with rounded corners and the current color.
     *
     * @param g The Graphics object to protect.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(currentColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

        super.paintComponent(g);
    }
}
