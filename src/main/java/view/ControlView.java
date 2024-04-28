package main.java.view;

import main.java.common.Robot;
import main.java.EnvPresenter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ControlView extends JPanel implements ComponentView {
    private List<Robot> robots; // Список роботов
    private int currentRobotIndex = 0; // Индекс текущего робота
    private Robot model;
    private int updatesCount = 0;
    private EnvPresenter presenter;
    private boolean isObstacleMode = false;

    public ControlView(EnvPresenter presenter, Robot model){
        this.presenter = presenter;
        this.model = model;
        initializeUI();
    }

    public void setRobots(List<Robot> robots) {
        this.robots = robots;
        if (!robots.isEmpty()) {
            this.model = robots.get(currentRobotIndex);
        }
    }

    public void setActiveRobot(Robot robot) {
        this.model = robot;
        //System.out.println("Active robot switched to: " + robot);
        repaint();
    }


    private void initializeUI() {
        setLayout(new FlowLayout()); // Используем FlowLayout для размещения кнопок

        // Добавление кнопки "Move"
        JButton moveButton = new JButton("Move");
        moveButton.addActionListener(this::performMove);
        add(moveButton);

        // Добавление кнопки "Rotate Clockwise"
        JButton rotateButton = new JButton("Turn");
        rotateButton.addActionListener(this::performTurn);
        add(rotateButton);

        // Добавление кнопки "Turn Clockwise"
        JButton rotateCounterButton = new JButton("Turn Clockwise");
        rotateCounterButton.addActionListener(this::performCounterTurn);
        add(rotateCounterButton);

        // Добавление кнопки "Switch Robot"
        JButton switchButton = new JButton("Switch Robot");
        switchButton.addActionListener(this::switchRobot);
        add(switchButton);

        // Добавление кнопки "Create/Remove Obstacle"
        JButton toggleObstacleButton = new JButton("Create/Remove Obstacle");
        toggleObstacleButton.addActionListener(this::toggleObstacleMode);
        add(toggleObstacleButton);
    }

    private void performMove(ActionEvent e) {
        if (model != null) {
            model.move(); // Вызываем метод move модели Robot
            updatesCount++; // Увеличиваем счетчик обновлений
            repaint(); // Перерисовать интерфейс, если это необходимо
        }
    }

    private void performTurn(ActionEvent e) {
        if (model != null) {
            model.turn(); // Вызываем метод rotateClockwise модели Robot
            updatesCount++; // Увеличиваем счетчик обновлений
            repaint(); // Перерисовать интерфейс, если это необходимо
        }
    }

    private void performCounterTurn(ActionEvent e) {
        if (model != null) {
            model.turnCounterClockwise(); // Вызываем метод rotateClockwise модели Robot
            updatesCount++; // Увеличиваем счетчик обновлений
            repaint(); // Перерисовать интерфейс, если это необходимо
        }
    }

    private void switchRobot(ActionEvent e) {
        if (robots == null || robots.isEmpty()) {
            //System.out.println("No robots available to switch.");
            return;
        }
        currentRobotIndex = (currentRobotIndex + 1) % robots.size();
        this.model = robots.get(currentRobotIndex);
        this.presenter.setActiveRobot(this.model); // Обеспечиваем обновление активного робота в EnvPresenter
        //System.out.println("Switched to robot " + (currentRobotIndex + 1));
        repaint();
    }

    private void toggleObstacleMode(ActionEvent e) {
        isObstacleMode = !isObstacleMode; // Переключаем режим
        //System.out.println("Obstacle mode: " + (isObstacleMode ? "ON" : "OFF"));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Рендеринг специфических элементов не требуется, так как все управляется кнопками
    }

    @Override
    public int numberUpdates() {
        return updatesCount;
    }

    @Override
    public Robot getModel() {
        return robots.get(currentRobotIndex);
    }

    @Override
    public void clearChanged() {
        updatesCount = 0;  // Сброс счетчика изменений
    }

    public boolean isObstacleMode() {
        return isObstacleMode;
    }
}