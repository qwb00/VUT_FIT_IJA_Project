package main.java;

import main.java.common.Position;
import main.java.common.Environment;
import main.java.common.Robot;
import main.java.configuration.Configuration;
import main.java.environment.Room;
import main.java.view.FieldView;
import main.java.view.RobotView;
import main.java.view.ControlView;
import main.java.common.Observable.Observer;
import main.java.common.Observable;
import main.java.robot.ControlledRobot;
import main.java.robot.AutonomousRobot;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
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
    private Map<Position, FieldView> fields;
    private List<RobotView> robots;
    private JFrame frame;
    private ControlView controlView;
    private Robot activeRobot;


    public EnvPresenter() {
        this.env = null;
        this.fields = new HashMap();
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
                env.removeRobot(robot);  // Убедитесь, что таймеры остановлены и наблюдатели удалены
            }
            ((Room)this.env).clearObstacles();
        }

        this.env = newEnv;
        this.fields.clear();
        this.robots.clear();

        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            initializeViews();
            refreshGui();
            setActiveFirstRobot();  // Установить первого робота в списке как активного
        });
    }

    private void setActiveFirstRobot() {
        List<Robot> robots = env.getRobots(); // Получаем список роботов из окружения
        if (!robots.isEmpty()) {
            Robot firstRobot = robots.get(0);
            setActiveRobot(firstRobot);
            if (controlView != null) {
                controlView.setActiveRobot(firstRobot);
            }
        }
    }

    public void clearEnvironment() {
        stopSimulation();
        // Check if the environment can be cleared directly
        if (env instanceof Room) {
            ((Room) env).clearObstacles();
            ((Room) env).clearRobots();
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
    public void stopSimulation() {
        // Stop all autonomous robots' movements
        for (RobotView robotView : robots) {
            Robot robot = robotView.getModel();
            if (robot instanceof AutonomousRobot) {
                ((AutonomousRobot) robot).stopMovement();
            }
        }

        // Optionally, clear fields or reset other simulation-specific states
        fields.forEach((position, fieldView) -> fieldView.removeComponent());
        fields.clear();

        // Reset the UI components
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();

        // Log or notify about the simulation stop
        Logger.getLogger(EnvPresenter.class.getName()).log(Level.INFO, "Simulation stopped.");
    }


    public void initializeViews() {
        GridLayout gridLayout = new GridLayout(env.getRows(), env.getCols());
        JPanel gridPanel = new JPanel(gridLayout);

        // Create field views based on the new environment
        for (int row = 0; row < env.getRows(); ++row) {
            for (int col = 0; col < env.getCols(); ++col) {
                Position position = new Position(row, col);
                FieldView fieldView = new FieldView(env, position, this);
                fields.put(position, fieldView);
                gridPanel.add(fieldView);
            }
        }

        // Ensure ControlView is initialized
        if (controlView == null) {
            // Assume that ControlView constructor needs at least an initial Robot,
            // check if there are any robots and pass the first one or create a new instance without a robot
            if (!env.getRobots().isEmpty()) {
                controlView = new ControlView(this, env.getRobots().get(0));
            } else {
                controlView = new ControlView(this, null); // Modify ControlView to handle null if no robots exist
            }
            controlView.setRobots(env.getRobots()); // Pass all robots to control view
        }

        // Create robot views for each robot in the new environment
        env.getRobots().forEach(robot -> {
            RobotView robotView = new RobotView(this, robot);
            robots.add(robotView);
            FieldView field = fields.get(robot.getPosition());
            if (field != null) {
                field.addComponent(robotView);
            }
        });

        // Update the main frame to show the new layout
        // Update the main frame to show the new layout
        frame.getContentPane().add(gridPanel, BorderLayout.CENTER);
        frame.getContentPane().add(controlView, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
    }


    public void open() {
        try {
            SwingUtilities.invokeAndWait(this::initialize);
        } catch (InvocationTargetException | InterruptedException e) {
            Logger.getLogger(EnvPresenter.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    protected void init() {
        try {
            SwingUtilities.invokeAndWait(this::initialize);
        } catch (InvocationTargetException | InterruptedException var2) {
            Logger.getLogger(EnvPresenter.class.getName()).log(Level.SEVERE, (String)null, var2);
        }

    }

    public FieldView fieldAt(Position var1) {
        return (FieldView)this.fields.get(var1);
    }

    public void initialize() {
        this.frame = new JFrame("Robot Environment");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 600);
        this.frame.setMinimumSize(new Dimension(800, 600));
        this.frame.setResizable(false);

        // Диалоговое окно с выбором
        String[] options = {"Load configuration", "Create empty map"};
        int response = JOptionPane.showOptionDialog(null, "How would you like to start?", "Configuration",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        if (response == 1) {
            createEmptyMap(); // Создание пустой карты, если пользователь так выбрал
        } else {
            loadConfiguration(); // Загрузка конфигурации, если пользователь так выбрал
        }

        this.frame.pack();
        this.frame.setVisible(true);
    }

    private void loadConfiguration() {
        String configFilePath = "src/main/resources/config.txt";
        this.env = Configuration.loadConfiguration(configFilePath);
        initializeViews(); // Инициализация представлений с загруженной конфигурацией
    }

    private void createEmptyMap() {
        String rows = JOptionPane.showInputDialog("Enter number of rows:");
        String cols = JOptionPane.showInputDialog("Enter number of columns:");
        try {
            int numRows = Integer.parseInt(rows);
            int numCols = Integer.parseInt(cols);
            this.env = new Room(numRows, numCols); // Создание новой пустой комнаты
            initializeViews(); // Инициализация представлений с пустой конфигурацией
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid number format. Please enter valid integers.", "Error", JOptionPane.ERROR_MESSAGE);
            createEmptyMap(); // Рекурсивный вызов для повторного ввода
        }
    }

    protected List<FieldView> fields() {
        return new ArrayList(this.fields.values());
    }

    public void update(Observable o) {
        // Обновление GUI, когда состояние робота изменяется
        //System.out.println("Observable changed: ");
        SwingUtilities.invokeLater(this::refreshGui);
    }

    public void refreshGui() {
        fields.values().forEach(FieldView::repaint); // Перерисовка каждого поля
        robots.forEach(RobotView::refreshView); // Вызов обновления для каждого представления робота
    }

    public void setActiveRobot(Robot robot) {
        this.activeRobot = robot;
        refreshGui(); // Обновляем GUI после изменения активного робота
    }

    public boolean isActive(Robot robot) {
        return robot.equals(activeRobot);
    }

    public boolean isObstacleMode() {
        return controlView.isObstacleMode();
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
        RobotView robotView = new RobotView(this, robot); // Создание визуального представления
        this.robots.add(robotView); // Добавление в список роботов
        FieldView field = this.fields.get(robot.getPosition()); // Получение FieldView, где находится робот
        if (field != null) {
            field.addComponent(robotView); // Добавление компонента в FieldView
            field.repaint(); // Перерисовка FieldView
        }
        refreshGui(); // Обновление GUI
    }


}
