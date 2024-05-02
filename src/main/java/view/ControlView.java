package main.java.view;

import main.java.common.Environment;
import main.java.common.Robot;
import main.java.EnvPresenter;
import main.java.configuration.Configuration;
import main.java.robot.AutonomousRobot;
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
    private List<Robot> robots; // Список роботов
    private int currentRobotIndex = 0; // Индекс текущего робота
    private Robot model;
    private int updatesCount = 0;
    private EnvPresenter presenter;
    private boolean isObstacleMode = false;
    private static final Logger logger = LogManager.getLogger(ControlView.class);
    private SimulationManager simulationManager;

    public ControlView(EnvPresenter presenter, Robot model){
        this.presenter = presenter;
        this.model = model;
        this.simulationManager = SimulationManager.getInstance(presenter.getEnvironment());
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
        setLayout(new FlowLayout());

        // Изменяем размер иконки при загрузке
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

        // Start Button
        JButton startButton = new JButton("Start");
        startButton.addActionListener(this::handleStart);
        add(startButton);

        // Pause Button
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(this::handlePause);
        add(pauseButton);

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(this::handleStop);
        add(stopButton);

        // Reverse Button
        JButton reverseButton = new JButton("Reverse");
        reverseButton.addActionListener(this::handleReverse);
        add(reverseButton);
    }

    // Метод для изменения размера иконки
    private Icon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
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

    private void loadConfiguration(ActionEvent e) {
        String configFilePath = "src/main/resources/config.txt"; // Путь к файлу конфигурации
        try {
            presenter.clearEnvironment(); // Очистка текущего окружения и роботов
            Environment newEnv = Configuration.loadConfiguration(configFilePath); // Загрузка нового окружения
            presenter.setEnvironment(newEnv); // Обновляем окружение в presenter
            //presenter.initializeViews(); // Пусть `initializeViews` отвечает за обновление представлений
            logger.info("Configuration loaded from {}", configFilePath);
        } catch (Exception ex) {
            logger.error("Failed to load configuration from {}: {}", configFilePath, ex.getMessage());
        }
    }

    private void saveConfiguration(ActionEvent e) {
        Configuration.saveConfiguration(presenter.getEnvironment(), "src/main/resources/config.txt");  // Укажите правильный путь
        repaint();
    }

    private void handleStart(ActionEvent e) {
        simulationManager.startSimulation();
    }

    private void handlePause(ActionEvent e) {
        simulationManager.pauseSimulation();
    }

    private void handleReverse(ActionEvent e) {
        simulationManager.reverseSimulation();
    }

    private void handleStop(ActionEvent e) {
        simulationManager.stopSimulation();
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