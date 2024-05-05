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

    public EnvPresenter() {
        this.env = null;
        this.fields = new HashMap<>();
        this.robots = new ArrayList<>();
    }

    public Environment getEnvironment() {
        return this.env;
    }

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

    public void clearEnvironment() {
        deleteSimulation();
        // Check if the environment can be cleared directly
        if (env instanceof Room) {
            (env).clearObstacles();
            (env).clearRobots();
        }

        // Clear all fields and robot views
        fields.forEach((pos, field) -> {
            field.removeComponent();
            frame.getContentPane().remove(field);
        });
        fields.clear();

        // Unsubscribe each RobotView from its model
        robots.forEach(robotView -> robotView.getModel().removeObserver(this));
        robots.clear();

        // Clear and reset the frame
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Stops any ongoing simulation processes, including robot movements and other timed actions.
     */
    public void deleteSimulation() {
        // Stop all autonomous robots' movements
        for (RobotView robotView : robots) {
            Robot robot = robotView.getModel();
            if (robot instanceof AutonomousRobot) {
                ((AutonomousRobot) robot).stopMovement();
            }
        }

        fields.forEach((position, fieldView) -> fieldView.removeComponent());
        fields.clear();

        // Reset the UI components
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();

        Logger.getLogger(EnvPresenter.class.getName()).log(Level.INFO, "Simulation stopped.");
    }


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



    public void open() {
        try {
            SwingUtilities.invokeAndWait(this::initialize);
        } catch (InvocationTargetException | InterruptedException e) {
            Logger.getLogger(EnvPresenter.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public FieldView fieldAt(Position var1) {
        return this.fields.get(var1);
    }

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

    private void loadConfiguration() {
        String configFilePath = "data/config.txt";
        this.env = Configuration.loadConfiguration(configFilePath);
        initializeViews();
    }

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






    public void refreshGui() {
        fields.values().forEach(FieldView::repaint);
        robots.forEach(RobotView::refreshView);
    }

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

    public boolean isActive(Robot robot) {
        return robot.equals(activeRobot);
    }

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
