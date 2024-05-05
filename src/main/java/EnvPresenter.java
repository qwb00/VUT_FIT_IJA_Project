/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 * The EnvPresenter class acts as a presenter in the Model-View-Presenter (MVP) pattern,
 * managing the interaction between the simulation environment and the user interface.
 */

package main.java;

import main.java.common.Position;
import main.java.common.Environment;
import main.java.common.Robot;
import main.java.configuration.Configuration;
import main.java.design.DesignedUtils;
import main.java.design.DesignedWindow;
import main.java.environment.Room;
import main.java.simulation.SimulationManager;
import main.java.view.FieldView;
import main.java.view.RobotView;
import main.java.view.ControlView;
import main.java.common.Observable.Observer;
import main.java.common.Observable;
import main.java.robot.ControlledRobot;
import main.java.robot.AutonomousRobot;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class EnvPresenter implements Observer {
    private Environment env;
    private final Map<Position, FieldView> fields;
    private final List<RobotView> robots;
    private JFrame frame;
    private ControlView controlView;
    private Robot activeRobot;
    private SimulationManager simulationManager;

    /**
     * Initializes the EnvPresenter, setting up default values.
     */
    public EnvPresenter() {
        this.env = null;
        this.fields = new HashMap<>();
        this.robots = new ArrayList<>();
    }

    /**
     * Retrieves the current environment of the simulation.
     *
     * @return The current simulation environment.
     */
    public Environment getEnvironment() {
        return this.env;
    }

    /**
     * Sets a new environment and updates the presentation accordingly.
     *
     * @param newEnv The new environment to set.
     */
    public void setEnvironment(Environment newEnv) {
        if (this.env instanceof Room) {
            List<Robot> oldRobots = new ArrayList<>(env.getRobots());
            for (Robot robot : oldRobots) {
                if (robot instanceof AutonomousRobot) {
                    ((AutonomousRobot)robot).stopMovement();
                }
                env.removeRobot(robot);
            }
            (this.env).clearObstacles();
        }

        this.env = newEnv;
        this.fields.clear();
        this.robots.clear();

        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            initializeViews();
            refreshGui();
            setActiveFirstRobot();
        });

        this.simulationManager.setEnvironment(newEnv);
        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     * Sets the first robot in the environment as active.
     */
    private void setActiveFirstRobot() {
        List<Robot> robots = env.getRobots();
        if (!robots.isEmpty()) {
            Robot firstRobot = robots.get(0);
            setActiveRobot(firstRobot);
            if (controlView != null) {
                controlView.setActiveRobot(firstRobot);
            }
        }
    }

    /**
     * Clears the environment by removing all obstacles and robots.
     */
    public void clearEnvironment() {
        deleteSimulation();
        if (env instanceof Room) {
            (env).clearObstacles();
            (env).clearRobots();
        }

        fields.forEach((pos, field) -> {
            field.removeComponent();
            frame.getContentPane().remove(field);
        });
        fields.clear();

        robots.forEach(robotView -> robotView.getModel().removeObserver(this));
        robots.clear();

        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Stops the simulation and removes all components from the frame.
     */
    public void deleteSimulation() {
        for (RobotView robotView : robots) {
            Robot robot = robotView.getModel();
            if (robot instanceof AutonomousRobot) {
                ((AutonomousRobot) robot).stopMovement();
            }
        }

        fields.forEach((position, fieldView) -> fieldView.removeComponent());
        fields.clear();

        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();

        Logger.getLogger(EnvPresenter.class.getName()).log(Level.INFO, "Simulation stopped.");
    }

    /**
     * Initializes the views for the environment and its components.
     */
    public void initializeViews() {
        frame = new DesignedWindow();
        simulationManager = SimulationManager.getInstance(env);
        simulationManager.addObserver(this);

        GridLayout gridLayout = new GridLayout(env.getRows(), env.getCols());
        JPanel gridPanel = new JPanel(gridLayout);

        fields.clear();
        robots.clear();

        for (int row = 0; row < env.getRows(); ++row) {
            for (int col = 0; col < env.getCols(); ++col) {
                Position position = new Position(row, col);
                FieldView fieldView = new FieldView(env, position, this);
                fields.put(position, fieldView);
                gridPanel.add(fieldView);
            }
        }

        if (controlView == null) {
            controlView = new ControlView(this, null);
        }
        controlView.setRobots(env.getRobots());

        for (Robot robot : env.getRobots()) {
            RobotView robotView = new RobotView(this, robot);
            robots.add(robotView);
            FieldView field = fields.get(robot.getPosition());
            if (field != null) {
                field.addComponent(robotView);
            }
        }

        if (!robots.isEmpty()) {
            Robot firstRobot = robots.get(0).getModel();
            setActiveRobot(firstRobot);
            controlView.setActiveRobot(firstRobot);
        }

        frame.getContentPane().add(gridPanel, BorderLayout.CENTER);
        frame.getContentPane().add(controlView, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);

        frame.revalidate();
        frame.repaint();

        frame.setVisible(true);
    }


    /**
     * Opens the main application window.
     */
    public void open() {
        try {
            SwingUtilities.invokeAndWait(this::initialize);
        } catch (InvocationTargetException | InterruptedException e) {
            Logger.getLogger(EnvPresenter.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Retrieves the field view at the specified position.
     *
     * @param var1 The position to check.
     * @return The field view at the specified position.
     */
    public FieldView fieldAt(Position var1) {
        return this.fields.get(var1);
    }

    /**
     * Initializes the main application, providing options to load a configuration or create an empty map.
     */
    public void initialize() {
        String[] options = {"Load configuration", "Create empty map"};
        int response = DesignedUtils.showCustomConfirmDialog(frame, "How would you like to start?", "Configuration", options);

        if (response == 1) {
            createEmptyMap();
        } else {
            loadConfiguration();
        }

        this.frame.pack();
        this.frame.setVisible(true);
    }

    /**
     * Loads the environment configuration from a file and initializes the views.
     */
    private void loadConfiguration() {
        String configFilePath = "src/main/resources/config.txt";
        this.env = Configuration.loadConfiguration(configFilePath);
        initializeViews();
    }

    /**
     * Creates an empty map by asking the user for the number of rows and columns.
     */
    private void createEmptyMap() {
        JTextField rowsField = new JTextField();
        JTextField colsField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Enter number of rows:"));
        panel.add(rowsField);
        panel.add(new JLabel("Enter number of columns:"));
        panel.add(colsField);

        int result = DesignedUtils.showCustomInputDialog(frame, panel, "Create Empty Map");

        if (result == JOptionPane.OK_OPTION) {
            try {
                int numRows = Integer.parseInt(rowsField.getText());
                int numCols = Integer.parseInt(colsField.getText());
                this.env = new Room(numRows, numCols);
                initializeViews();
            } catch (NumberFormatException ex) {
                DesignedUtils.showCustomConfirmDialog(frame, "Invalid number format. Please enter valid integers.", "Error", new String[]{"OK"});
                createEmptyMap();
            }
        }
    }

    /**
     * Updates the graphical user interface when the simulation changes.
     *
     * @param o The observable object that changed.
     */
    public void update(Observable o) {
        if (o instanceof SimulationManager) {
            fields.clear();
            robots.clear();

            frame.getContentPane().removeAll();

            GridLayout gridLayout = new GridLayout(env.getRows(), env.getCols());
            JPanel gridPanel = new JPanel(gridLayout);

            for (int row = 0; row < env.getRows(); ++row) {
                for (int col = 0; col < env.getCols(); ++col) {
                    Position position = new Position(row, col);
                    FieldView fieldView = new FieldView(env, position, this);
                    fields.put(position, fieldView);
                    gridPanel.add(fieldView);
                }
            }

            env.getRobots().forEach(robot -> {
                RobotView robotView = new RobotView(this, robot);
                robots.add(robotView);
                FieldView field = fields.get(robot.getPosition());
                if (field != null) {
                    field.addComponent(robotView);
                }
            });

            if (frame instanceof DesignedWindow) {
                JPanel titleBar = ((DesignedWindow) frame).getTitleBar();
                frame.getContentPane().add(titleBar, BorderLayout.NORTH);
            }

            frame.getContentPane().add(gridPanel, BorderLayout.CENTER);
            frame.getContentPane().add(controlView, BorderLayout.SOUTH);

            // Set the active robot
            Robot activeRobot = simulationManager.getActiveRobot();
            if (activeRobot != null) {
                setActiveRobot(activeRobot);
                controlView.setActiveRobot(activeRobot);
            }

            frame.revalidate();
            frame.repaint();
        }
    }

    /**
     * Refreshes the graphical user interface for the environment.
     */
    public void refreshGui() {
        fields.values().forEach(FieldView::repaint);
        robots.forEach(RobotView::refreshView);
    }

    /**
     * Sets the specified robot as the active robot.
     *
     * @param robot The robot to set as active.
     */
    public void setActiveRobot(Robot robot) {
        if (robot instanceof ControlledRobot) {
            this.activeRobot = robot;
            ((ControlledRobot) robot).setActive(true);

            for (Robot otherRobot : env.getRobots()) {
                if (otherRobot != robot && otherRobot instanceof ControlledRobot) {
                    ((ControlledRobot) otherRobot).setActive(false);
                }
            }

            refreshGui();
        }
    }

    /**
     * Checks if the specified robot is the active robot.
     *
     * @param robot The robot to check.
     * @return true if the specified robot is active, false otherwise.
     */
    public boolean isActive(Robot robot) {
        return robot.equals(activeRobot);
    }

    /**
     * Sets the active robot based on its position.
     *
     * @param pos The position of the robot to set as active.
     */
    public void setActiveRobotByPosition(Position pos) {
        for (RobotView robotView : robots) {
            if (robotView.getModel().getPosition().equals(pos) && robotView.getModel() instanceof ControlledRobot) {
                setActiveRobot(robotView.getModel());
                controlView.setActiveRobot(robotView.getModel());
                break;
            }
        }
        refreshGui();
    }

    /**
     * Adds a robot view to the environment.
     *
     * @param robot The robot to add to the view.
     */
    public void addRobotView(Robot robot) {
        RobotView robotView = new RobotView(this, robot);
        this.robots.add(robotView);
        FieldView field = this.fields.get(robot.getPosition());
        if (field != null) {
            field.addComponent(robotView);
            field.repaint();
        }
        refreshGui();
    }


}
