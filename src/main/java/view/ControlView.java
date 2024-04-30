package main.java.view;

import main.java.common.Environment;
import main.java.common.Robot;
import main.java.EnvPresenter;
import main.java.configuration.Configuration;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger logger = LogManager.getLogger(ControlView.class);

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


    private void initializeUI() {
        setLayout(new FlowLayout()); // Используем FlowLayout для размещения кнопок

        // Добавление кнопки "Move"
        JButton moveButton = new JButton("Move");
        moveButton.addActionListener(this::performMove);
        add(moveButton);

        // Добавление кнопки "Turn Clockwise"
        JButton rotateButton = new JButton("Turn Clockwise");
        rotateButton.addActionListener(this::performTurn);
        add(rotateButton);

        // Добавление кнопки "Turn Counter Clockwise"
        JButton rotateCounterButton = new JButton("Turn Counter Clockwise");
        rotateCounterButton.addActionListener(this::performCounterTurn);
        add(rotateCounterButton);

        // Добавление кнопки "Switch Robot"
        JButton switchButton = new JButton("Switch Robot");
        switchButton.addActionListener(this::switchRobot);
        add(switchButton);

        // Добавление новых кнопок
        JButton loadConfigButton = new JButton("Load Config");
        loadConfigButton.addActionListener(this::loadConfiguration);
        add(loadConfigButton);

        JButton saveConfigButton = new JButton("Save Config");
        saveConfigButton.addActionListener(this::saveConfiguration);
        add(saveConfigButton);
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
        if (model != null && model instanceof ControlledRobot) {
            ControlledRobot robot = (ControlledRobot) model;
            robot.turnCounterClockwise(); // Вызываем метод rotateClockwise модели Robot
            updatesCount++; // Увеличиваем счетчик обновлений
            repaint(); // Перерисовать интерфейс, если это необходимо
        }
    }

    private void switchRobot(ActionEvent e) {
        if (robots == null || robots.isEmpty()) {
            return;
        }
        int startIndex = currentRobotIndex;
        do {
            currentRobotIndex = (currentRobotIndex + 1) % robots.size();
            if (robots.get(currentRobotIndex) instanceof ControlledRobot) {
                this.model = robots.get(currentRobotIndex);
                this.presenter.setActiveRobot(this.model);
                logger.info("Switched to Robot #{}", currentRobotIndex + 1);
                repaint();
                return;
            }
        } while (currentRobotIndex != startIndex);
        logger.info("No ControlledRobot available to switch.");
    }

    // Методы для обработки нажатий на кнопки конфигурации
    private void loadConfiguration(ActionEvent e) {
        String configFilePath = "src/main/resources/config.txt";  // Путь к файлу конфигурации
        try {
            presenter.clearEnvironment();  // Очистка текущего окружения и роботов
            Environment newEnv = Configuration.loadConfiguration(configFilePath);  // Загрузка нового окружения
            presenter.setEnvironment(newEnv);  // Обновляем окружение в presenter
            presenter.initializeViews();  // Инициализируем представления с новым окружением
            logger.info("Configuration loaded from {}", configFilePath);
        } catch (Exception ex) {
            logger.error("Failed to load configuration from {}: {}", configFilePath, ex.getMessage());
        }
    }

    private void saveConfiguration(ActionEvent e) {
        Configuration.saveConfiguration(presenter.getEnvironment(), "src/main/resources/config.txt");  // Укажите правильный путь
        repaint();
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