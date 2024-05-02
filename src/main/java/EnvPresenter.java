package main.java;

import main.java.common.Position;
import main.java.common.Environment;
import main.java.common.Robot;
import main.java.environment.Room;
import main.java.simulation.SimulationManager;
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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class EnvPresenter implements Observer {
    private Environment env;
    private Map<Position, FieldView> fields;
    private List<RobotView> robots;
    private JFrame frame;
    private ControlView controlView;
    private Robot activeRobot;
    private SimulationManager simulationManager;


    public EnvPresenter(Environment var1) {
        this.env = var1;
        this.fields = new HashMap();
        this.robots = new ArrayList<>();
        this.simulationManager = SimulationManager.getInstance(var1);
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
        this.simulationManager.setEnvironment(newEnv);
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
        frame.getContentPane().add(gridPanel, BorderLayout.CENTER);
        if (controlView != null) {
            frame.getContentPane().add(controlView, BorderLayout.SOUTH);
        }
        frame.revalidate();
        frame.repaint();
    }


    public void open() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                this.initialize();
                this.frame.setVisible(true);
            });
        } catch (InvocationTargetException | InterruptedException var2) {
            Logger.getLogger(EnvPresenter.class.getName()).log(Level.SEVERE, (String)null, var2);
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

    private void initialize() {
        this.frame = new JFrame("Robot Environment");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 600);
        this.frame.setMinimumSize(new Dimension(800, 600));
        this.frame.setResizable(false);
        GridLayout gridLayout = new GridLayout(this.env.getRows(), this.env.getCols());
        JPanel gridPanel = new JPanel(gridLayout);

        // Создание и распределение полей
        for (int row = 0; row < this.env.getRows(); ++row) {
            for (int col = 0; col < this.env.getCols(); ++col) {
                Position position = new Position(row, col);
                FieldView fieldView = new FieldView(this.env, position, this);
                gridPanel.add(fieldView);
                this.fields.put(position, fieldView);
            }
        }

        // Создание и добавление роботов
        List<Robot> robotModels = this.env.getRobots();
        robotModels.forEach(robot -> {
            RobotView robotView = new RobotView(this, robot);
            this.robots.add(robotView);
        });

        setActiveFirstRobot();

        // Настройка ControlView
        this.controlView = new ControlView(this, robotModels.get(0));
        this.controlView.setRobots(robotModels);
        this.frame.getContentPane().add(controlView, BorderLayout.SOUTH);
        this.frame.getContentPane().add(gridPanel, BorderLayout.CENTER);

        this.frame.pack();
    }

    protected List<FieldView> fields() {
        return new ArrayList(this.fields.values());
    }

    public void update(Observable o) {
        // Обновление GUI, когда состояние робота изменяется
        //System.out.println("Observable changed: ");
        SwingUtilities.invokeLater(this::refreshGui);
    }

    private void refreshGui() {
        frame.revalidate();
        frame.repaint();
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

}
