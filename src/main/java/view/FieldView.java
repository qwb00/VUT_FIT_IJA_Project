package main.java.view;

import main.java.EnvPresenter;
import main.java.common.Position;
import main.java.common.Environment;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;

import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FieldView extends JPanel {
    private final Environment model;
    private final Position position;
    private EnvPresenter presenter;
    private ComponentView obj;
    private int changedModel = 0;
    private long lastClickTime = 0;

    public FieldView(Environment env, Position pos, EnvPresenter presenter) {
        this.model = env;
        this.position = pos;
        this.presenter = presenter;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick();
            }
        });
    }

    private void handleMouseClick() {
        if (model.obstacleAt(position)) {
            removeObstacle();
        } else if (!model.robotAt(position)) {
            handleAddElement();
        }
        updateFieldView();
    }

    private void removeObstacle() {
        model.removeObstacleAt(position.getRow(), position.getCol());
    }

    private void handleAddElement() {
        String[] options = {"Add obstacle", "Add robot"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose an action:",
                "Add element",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            model.createObstacleAt(position.getRow(), position.getCol());
        } else if (choice == 1) {
            handleRobotTypeSelection();
        }
    }

    private void handleRobotTypeSelection() {
        String[] options = {"Controlled robot", "Autonomous robot"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Select type of robot to add:",
                "Robot Type",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            handleRobotCreation();  // Existing method to create controlled robot
        } else if (choice == 1) {
            handleAutonomousRobotCreation();  // New method to create autonomous robot
        }
    }

    private void handleRobotCreation() {
        int speed = askForRobotSpeed();
        if (speed > 0) { // speed will be -1 if the user cancels the dialog
            ControlledRobot newRobot = ControlledRobot.create(model, position, speed);
            if (newRobot != null) {
                addNewControlledRobot(newRobot);
            }
        }
    }

    private int askForRobotSpeed() {
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        JSpinner speedSpinner = new JSpinner(spinnerModel);
        int result = JOptionPane.showOptionDialog(null, speedSpinner, "Set Robot Speed",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

        if (result == JOptionPane.OK_OPTION) {
            return (Integer) speedSpinner.getValue();
        } else {
            return -1; // Return -1 if user cancels
        }
    }

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

        // Проверяем, не существует ли робот уже на этой позиции
        if (!model.robotAt(position)) {
            AutonomousRobot newRobot = AutonomousRobot.create(model, position, speed, detectionRange, turnAngle, turnDirection);
            if (newRobot != null) {
                presenter.addRobotView(newRobot);
            }
        }
    }



    private int askForRobotParameter(String parameterName, int defaultValue, int min, int max, int step) {
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(defaultValue, min, max, step);
        JSpinner spinner = new JSpinner(spinnerModel);
        int result = JOptionPane.showOptionDialog(null, spinner, "Set " + parameterName,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

        if (result == JOptionPane.OK_OPTION) {
            return (Integer) spinner.getValue();
        } else {
            return -1;  // Return -1 if user cancels
        }
    }

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

    private void addNewAutonomousRobot(AutonomousRobot newRobot) {
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

    private void updateFieldView() {
        if (model.robotAt(position)) {
            presenter.setActiveRobotByPosition(position);
        }
        privUpdate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE); // Установим белый фон для клеток без препятствий
        setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Граница для клеток

        // Проверяем наличие препятствия
        if (model.obstacleAt(position)) {
            g.setColor(Color.DARK_GRAY); // Цвет препятствий
            g.fillRect(0, 0, getWidth(), getHeight()); // Заполняем всю площадь панели
        }

        // Если в клетке есть компонент (робот), вызываем его paintComponent
        if (obj != null) {
            obj.paintComponent(g);
        }
    }

    private void privUpdate() {
        if (this.model.obstacleAt(this.position)) {
            this.setBackground(Color.lightGray);
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

    }

    public void addComponent(ComponentView var1) {
        this.obj = var1;
    }

    public void removeComponent() {
        this.obj = null;
    }

    public ComponentView getComponent() {
        return this.obj;
    }

    public int numberUpdates() {
        return this.obj == null ? 0 : this.obj.numberUpdates();
    }

    public void clearChanged() {
        if (this.obj != null) {
            this.obj.clearChanged();
        }

    }
}
