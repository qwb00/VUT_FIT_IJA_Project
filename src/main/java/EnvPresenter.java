package main.java;

import main.java.common.Position;
import main.java.common.Environment;
import main.java.common.Robot;
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


    public EnvPresenter(Environment var1) {
        this.env = var1;
        this.fields = new HashMap();
        this.robots = new ArrayList<>();
    }

    public Environment getEnvironment() {
        return this.env;
    }

    public void setEnvironment(Environment newEnv) {
        // Clear existing environment
        if (this.env instanceof Room) {
            ((Room) this.env).clearObstacles();
            ((Room) this.env).clearRobots();
        }

        // Update the environment reference
        this.env = newEnv;

        // Clear UI components associated with the old environment
        this.fields.clear();
        this.robots.forEach(robotView -> robotView.getModel().removeObserver(this));
        this.robots.clear();

        // Asynchronously update the UI to reflect the new environment
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            initializeViews();  // Reinitialize views for the new environment
            refreshGui();
        });
    }


    public void clearEnvironment() {
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

        if (!robotModels.isEmpty()) {
            setActiveRobot(robotModels.get(0)); // Устанавливаем первого робота в списке как активного
        }

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
