/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 * The ControlView class manages the control panel of the application,
 * providing buttons to manipulate the simulation and interact with robots.
 */
package main.java.view;

import main.java.common.Environment;
import main.java.common.Robot;
import main.java.EnvPresenter;
import main.java.configuration.Configuration;
import main.java.robot.ControlledRobot;
import main.java.simulation.SimulationManager;
import main.java.design.DesignedButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ControlView extends JPanel implements ComponentView {
    private List<Robot> robots; // List of robots
    private int currentRobotIndex = 0; // Index of the current robot
    private Robot model;
    private final EnvPresenter presenter;
    private static final Logger logger = LogManager.getLogger(ControlView.class);
    private final SimulationManager simulationManager;

    /**
     * Constructs a ControlView for controlling the robots and the simulation.
     *
     * @param presenter The environment presenter.
     * @param model     The robot model being controlled.
     */
    public ControlView(EnvPresenter presenter, Robot model){
        this.presenter = presenter;
        this.model = model;
        this.simulationManager = SimulationManager.getInstance(presenter.getEnvironment());
        initializeUI();
    }

    /**
     * Sets the list of robots
     *
     */
    public void setRobots(List<Robot> robots) {
        this.robots = robots;
    }

    /**
     * Sets the active robot
     *
     */
    public void setActiveRobot(Robot robot) {
        if (robot instanceof ControlledRobot) {
            this.model = robot;
            int index = robots.indexOf(robot);
            if (index != -1) {
                currentRobotIndex = index;
                logger.info("Switched to Robot #{}", currentRobotIndex + 1);
            } else {
                logger.error("Attempted to switch to a robot not in the list");
            }
            repaint();
        } else {
            logger.error("Attempted to switch to a non-ControlledRobot");
        }
    }


    /**
     * Initializes the UI
     */
    private void initializeUI() {
        setLayout(new FlowLayout());

        Icon moveIcon = resizeIcon(new ImageIcon("src/main/resources/icons/move.png"));
        DesignedButton moveButton = new DesignedButton(moveIcon);
        moveButton.setPreferredSize(new Dimension(60, 45));
        moveButton.addActionListener(this::performMove);
        add(moveButton);

        Icon rotateCounterIcon = resizeIcon(new ImageIcon("src/main/resources/icons/left.png"));
        DesignedButton rotateCounterButton = new DesignedButton(rotateCounterIcon);
        rotateCounterButton.setPreferredSize(new Dimension(60, 45));
        rotateCounterButton.addActionListener(this::performCounterTurn);
        add(rotateCounterButton);

        Icon rotateIcon = resizeIcon(new ImageIcon("src/main/resources/icons/right.png"));
        DesignedButton rotateButton = new DesignedButton(rotateIcon);
        rotateButton.setPreferredSize(new Dimension(60, 45));
        rotateButton.addActionListener(this::performTurn);
        add(rotateButton);

        Icon loadConfigIcon = resizeIcon(new ImageIcon("src/main/resources/icons/load.png"));
        DesignedButton loadConfigButton = new DesignedButton(loadConfigIcon);
        loadConfigButton.setPreferredSize(new Dimension(60, 45));
        loadConfigButton.addActionListener(this::loadConfiguration);
        add(loadConfigButton);

        Icon saveConfigIcon = resizeIcon(new ImageIcon("src/main/resources/icons/save.png"));
        DesignedButton saveConfigButton = new DesignedButton(saveConfigIcon);
        saveConfigButton.setPreferredSize(new Dimension(60, 45));
        saveConfigButton.addActionListener(this::saveConfiguration);
        add(saveConfigButton);

        Icon startButtonIcon = resizeIcon(new ImageIcon("src/main/resources/icons/play.png"));
        DesignedButton startButton = new DesignedButton(startButtonIcon);
        startButton.setPreferredSize(new Dimension(60, 45));
        startButton.addActionListener(this::handleStart);
        add(startButton);

        Icon pauseButtonIcon = resizeIcon(new ImageIcon("src/main/resources/icons/pause.png"));
        DesignedButton pauseButton = new DesignedButton(pauseButtonIcon);
        pauseButton.setPreferredSize(new Dimension(60, 45));
        pauseButton.addActionListener(this::handlePause);
        add(pauseButton);

        Icon stopButtonIcon = resizeIcon(new ImageIcon("src/main/resources/icons/stop.png"));
        DesignedButton stopButton = new DesignedButton(stopButtonIcon);
        stopButton.setPreferredSize(new Dimension(60, 45));
        stopButton.addActionListener(this::handleStop);
        add(stopButton);

        Icon reverseButtonIcon = resizeIcon(new ImageIcon("src/main/resources/icons/reverse.png"));
        DesignedButton reverseButton = new DesignedButton(reverseButtonIcon);
        reverseButton.setPreferredSize(new Dimension(60, 45));
        reverseButton.addActionListener(this::handleReverse);
        add(reverseButton);

    }

    /**
     * Resizes the icon
     *
     * @param icon The icon to resize
     * @return The resized icon
     */
    private Icon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    /**
     * Performs the move action
     *
     * @param e The action event
     */
    private void performMove(ActionEvent e) {
        if (model != null) {
            model.move();
            repaint();
        }
    }

    /**
     * Performs the turn action
     *
     * @param e The action event
     */
    private void performTurn(ActionEvent e) {
        if (model != null) {
            model.turn();
            repaint();
        }
    }

    /**
     * Performs the counter turn action
     *
     * @param e The action event
     */
    private void performCounterTurn(ActionEvent e) {
        if (model != null && model instanceof ControlledRobot) {
            ControlledRobot robot = (ControlledRobot) model;
            robot.turnCounterClockwise();
            repaint();
        }
    }

    /**
     * Loads the configuration
     *
     * @param e The action event
     */
    private void loadConfiguration(ActionEvent e) {
        handleStop(null);
        String configFilePath = "src/main/resources/config.txt";
        try {
            presenter.clearEnvironment();
            Environment newEnv = Configuration.loadConfiguration(configFilePath);
            presenter.setEnvironment(newEnv);
            logger.info("Configuration loaded from {}", configFilePath);
        } catch (Exception ex) {
            logger.error("Failed to load configuration from {}: {}", configFilePath, ex.getMessage());
        }
    }

    /**
     * Saves the configuration
     *
     * @param e The action event
     */
    private void saveConfiguration(ActionEvent e) {
        Configuration.saveConfiguration(presenter.getEnvironment(), "src/main/resources/config.txt");
        repaint();
    }

    /**
     * Handles the start action
     *
     * @param e The action event
     */
    private void handleStart(ActionEvent e) {
        simulationManager.startSimulation();
    }

    /**
     * Handles the pause action
     *
     * @param e The action event
     */
    private void handlePause(ActionEvent e) {
        simulationManager.pauseSimulation();
    }

    /**
     * Handles the reverse action
     *
     * @param e The action event
     */
    private void handleReverse(ActionEvent e) {
        simulationManager.reverseSimulation();
    }

    /**
     * Handles the stop action
     *
     * @param e The action event
     */
    private void handleStop(ActionEvent e) {
        simulationManager.stopSimulation();
    }

    /**
     * Paints the component
     *
     * @param g The graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     * Returns the model
     *
     * @return The model
     */
    @Override
    public Robot getModel() {
        return robots.get(currentRobotIndex);
    }
}