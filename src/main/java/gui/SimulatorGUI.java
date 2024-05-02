package main.java.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.java.common.Obstacle;
import main.java.environment.Room;
import main.java.common.Robot;

public class SimulatorGUI extends JFrame {
    private Room environment; // Reference to your simulation environment
    private JPanel drawingPanel;

    public SimulatorGUI(int width, int height, Room environment) {
        super("Robot Simulator");
        this.environment = environment;
        initializeGUI(width, height);
    }

    private void initializeGUI(int width, int height) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);

        // Setup the drawing panel for simulation
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawEnvironment(g);
            }
        };
        drawingPanel.setPreferredSize(new Dimension(width, height - 100));
        add(drawingPanel, BorderLayout.CENTER);

        // Setup control panel with buttons
        JPanel controlPanel = setupControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private JPanel setupControlPanel() {
        JPanel controlPanel = new JPanel();

        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        // Additional buttons can be added here

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start simulation logic here
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pause simulation logic here
            }
        });

        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        // Add more buttons to controlPanel as needed

        return controlPanel;
    }

    private void drawEnvironment(Graphics g) {
        final int gridSize = 20;

        // Draw obstacles
        drawObstacles(g, gridSize);

        // Draw robots
        drawRobots(g, gridSize);
    }

    private void drawObstacles(Graphics g, int gridSize) {
        g.setColor(Color.GREEN); // Obstacles in green
        for (Obstacle obstacle : environment.getObstacles()) {
            int x = obstacle.getPosition().getCol() * gridSize;
            int y = obstacle.getPosition().getRow() * gridSize;
            g.fillRect(x, y, gridSize, gridSize);
        }
    }

    private void drawRobots(Graphics g, int gridSize) {
        g.setColor(Color.BLUE); // Robots in blue
        for (Robot robot : environment.getRobots()) {
            int x = robot.getPosition().getCol() * gridSize;
            int y = robot.getPosition().getRow() * gridSize;
            g.fillOval(x, y, gridSize, gridSize);
        }
    }

    // Method to refresh the drawing panel, call this whenever the simulation state changes and you need to update the visualization.
    public void refreshDisplay() {
        drawingPanel.repaint();
    }
}
