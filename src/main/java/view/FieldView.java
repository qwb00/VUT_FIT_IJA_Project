package main.java.view;

import main.java.EnvPresenter;
import main.java.common.Position;
import main.java.common.Environment;
import main.java.simulation.SimulationManager;
import main.java.design.DesignedUtils;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;
import main.java.design.DesignedField;

import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FieldView extends DesignedField {
    private final Environment model;
    private final Position position;
    private final EnvPresenter presenter;
    private ComponentView obj;

    private final SimulationManager simulationManager;

    public FieldView(Environment env, Position pos, EnvPresenter presenter) {
        super(); // calls the constructor of the DesignedField
        this.model = env;
        this.position = pos;
        this.presenter = presenter;
        this.simulationManager = SimulationManager.getInstance(env);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick();
            }
        });
    }

    /**
     * Handles the mouse click event
     */
    private void handleMouseClick() {
        if (model.obstacleAt(position)) {
            simulationManager.saveState();
            removeObstacle();
        } else if (model.robotAt(position)) {
            presenter.setActiveRobotByPosition(position);
        }
        else {
            handleAddElement();
        }
        updateFieldView();
    }

    /**
     * Removes the obstacle at the current position
     */
    private void removeObstacle() {
        model.removeObstacleAt(position.getRow(), position.getCol());
    }

    /**
     * Handles the addition of an element (obstacle or robot) to the field
     */
    private void handleAddElement() {
        String[] options = {"Add obstacle", "Add robot"};
        int choice = DesignedUtils.showCustomConfirmDialog(
                this,
                "Choose an action:",
                "Add element",
                options);

        if (choice == 0) {
            simulationManager.saveState();
            model.createObstacleAt(position.getRow(), position.getCol());
        } else if (choice == 1) {
            handleRobotTypeSelection();
        }
    }

    /**
     * Handles the selection of the robot type
     */
    private void handleRobotTypeSelection() {
        String[] options = {"Controlled robot", "Autonomous robot"};
        int choice = DesignedUtils.showCustomConfirmDialog(
                this,
                "Select type of robot to add:",
                "Robot Type",
                options);

        if (choice == 0) {
            handleRobotCreation(); // Existing method to create controlled robot
        } else if (choice == 1) {
            handleAutonomousRobotCreation(); // New method to create autonomous robot
        }
    }

    /**
     * Handles the creation of a controlled robot
     */
    private void handleRobotCreation() {
        int speed = askForRobotSpeed();
        if (speed > 0) { // speed will be -1 if the user cancels the dialog
            simulationManager.saveState();
            ControlledRobot newRobot = ControlledRobot.create(model, position, speed, 0);
            if (newRobot != null) {
                addNewControlledRobot(newRobot);
            }
        }
    }

    /**
     * Asks the user for the robot speed
     *
     * @return The speed of the robot or -1 if the user cancels the dialog
     */
    private int askForRobotSpeed() {
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner speedSpinner = new JSpinner(spinnerModel);

        int result = DesignedUtils.showCustomInputDialog(null, speedSpinner, "Set Robot Speed");

        if (result == JOptionPane.OK_OPTION) {
            return (Integer) speedSpinner.getValue();
        } else {
            return -1; // if user cancels the dialog
        }
    }

    /**
     * Handles the creation of an autonomous robot
     */
    private void handleAutonomousRobotCreation() {
        // Make sure the autonomous robot is created only once and added correctly
        int speed = askForRobotParameter("Speed", 1, 1, Integer.MAX_VALUE, 1);
        if (speed == -1) return;

        int detectionRange = askForRobotParameter("Detection Range", 1, 1, Integer.MAX_VALUE, 1);
        if (detectionRange == -1) return;

        int turnAngle = askForRobotParameter("Turn Angle", 90, 0, 360, 45);
        if (turnAngle == -1) return;

        String[] directions = {"Left", "Right"};
        int turnDirChoice = JOptionPane.showOptionDialog(null, "Select turn direction:",
                "Turn Direction", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, directions, directions[0]);
        boolean turnDirection = (turnDirChoice == 1);  // True if "Right", false if "Left"

        if (!model.robotAt(position)) {
            simulationManager.saveState();
            AutonomousRobot newRobot = AutonomousRobot.create(model, position, speed, detectionRange, turnAngle, turnDirection, 0);
            if (newRobot != null) {
                presenter.addRobotView(newRobot);
                if(simulationManager.isRunning()) {
                    newRobot.isMoveable = true;
                    newRobot.initMovement();
                }
            }
        }
    }

    /**
     * Asks the user for a robot parameter
     *
     * @param parameterName The name of the parameter
     * @param defaultValue  The default value of the parameter
     * @param min           The minimum value of the parameter
     * @param max           The maximum value of the parameter
     * @param step          The step value of the parameter
     * @return The value of the parameter or -1 if the user cancels the dialog
     */
    private int askForRobotParameter(String parameterName, int defaultValue, int min, int max, int step) {
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(defaultValue, min, max, step);
        JSpinner spinner = new JSpinner(spinnerModel);
        int result = JOptionPane.showOptionDialog(null, spinner, "Set " + parameterName,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

        if (result == JOptionPane.OK_OPTION) {
            return (Integer) spinner.getValue();
        } else {
            return -1;
        }
    }

    /**
     * Adds a new controlled robot to the field
     *
     * @param newRobot The new controlled robot
     */
    private void addNewControlledRobot(ControlledRobot newRobot) {
        FieldView fieldView = presenter.fieldAt(position);
        if (fieldView != null) {
            RobotView robotView = new RobotView(presenter, newRobot);
            model.addRobot(newRobot);
            presenter.addRobotView(newRobot);
            presenter.setActiveRobotByPosition(position);
            fieldView.addComponent(robotView);
            fieldView.repaint();
        }
    }

    /**
     * Updates the view of the field
     */
    private void updateFieldView() {
        privUpdate();
        repaint();
    }

    /**
     * Updates the view of the field
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the obstacle
        if (model.obstacleAt(position)) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // If there is a robot, calls his paintComponent method
        if (obj != null) {
            obj.paintComponent(g);
        }
    }

    /**
     * Updates the view of the field
     */
    private void privUpdate() {
        if (this.model.obstacleAt(this.position)) {
            this.setBackground(Color.lightGray);
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    /**
     * Adds a component to the field
     *
     * @param var1 The component to add
     */
    public void addComponent(ComponentView var1) {
        this.obj = var1;
    }

    /**
     * Removes the component from the field
     */
    public void removeComponent() {
        this.obj = null;
    }
}