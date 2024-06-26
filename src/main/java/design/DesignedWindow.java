/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 */
package main.java.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * A custom-designed window with a title bar that includes custom buttons for minimizing, maximizing, and closing the window.
 */
public class DesignedWindow extends JFrame {
    private Point initialClick;
    private boolean isFullscreen = false;
    private JPanel titleBar;

    /**
     * Constructs a new DesignedWindow, initializing its components.
     */
    public DesignedWindow() {
        initComponents();
    }

    /**
     * Initializes the components of the DesignedWindow.
     */
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setMinimumSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);
        setResizable(true);

        setUndecorated(true);

        titleBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        titleBar.setBackground(new Color(228, 228, 228));

        // Add minimize, maximize, and close buttons to the title bar
        JButton minimizeButton = createButton("lib/icons/minimize.png");
        minimizeButton.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton maximizeButton = createButton("lib/icons/fullscreen.png");
        maximizeButton.addActionListener(e -> toggleFullscreen());

        JButton closeButton = createButton("lib/icons/close.png");
        closeButton.addActionListener(e -> System.exit(0));

        titleBar.add(minimizeButton);
        titleBar.add(maximizeButton);
        titleBar.add(closeButton);

        add(titleBar, BorderLayout.NORTH);

        // Add listeners for moving the window by dragging the title bar
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });
    }

    /**
     * Toggles the window between fullscreen and normal modes.
     */
    private void toggleFullscreen() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        if (isFullscreen) {
            device.setFullScreenWindow(null);
            setExtendedState(Frame.NORMAL);
        } else {
            device.setFullScreenWindow(this);
        }
        isFullscreen = !isFullscreen;
    }

    /**
     * Creates a custom button with the specified icon.
     *
     * @param iconPath The path to the icon file.
     * @return A JButton with the specified icon.
     */
    private JButton createButton(String iconPath) {
        JButton button = new JButton();
        ImageIcon icon = new ImageIcon(iconPath);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage));
        button.setBackground(new Color(228, 228, 228));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(30, 30));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Returns the custom title bar panel.
     *
     * @return The title bar panel.
     */
    public JPanel getTitleBar() {
        return titleBar;
    }
}
