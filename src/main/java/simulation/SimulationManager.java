package main.java.simulation;

import main.java.EnvPresenter;
import main.java.common.Environment;
import main.java.common.Observable;
import main.java.common.Robot;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;
import main.java.simulation.states.EnvironmentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SimulationManager implements Observable {
    private static SimulationManager instance;
    private Environment environment; // Specific to Room rather than Environment
    private boolean isRunning = false;
    private final Stack<EnvironmentState> historyStates;
    private static final Logger logger = LogManager.getLogger(SimulationManager.class);
    private List<Observer> observers = new ArrayList<>();

    public SimulationManager(Environment environment) {
        this.environment = environment;
        historyStates = new Stack<>();
    }

    public void setEnvironment(Environment newEnvironment) {
        this.environment = newEnvironment;
    }

    public static SimulationManager getInstance(Environment environment) {
        if (instance == null) {
            instance = new SimulationManager(environment);
        }
        return instance;
    }

    public void pauseSimulation() {
        if (isRunning) {
            environment.getRobots().forEach(robot -> {
                if (robot instanceof AutonomousRobot) {
                    ((AutonomousRobot) robot).isMoveable = false;
                    ((AutonomousRobot) robot).stopMovement();
                }
                if(robot instanceof ControlledRobot) {
                    ((ControlledRobot) robot).canControlled = false;
                }
            });
            isRunning = false;
            logger.info("Simulation paused.");
        }
    }

    public void startSimulation() {
        if (!isRunning) {
            environment.getRobots().forEach(robot -> {
                if (robot instanceof AutonomousRobot) {
                    ((AutonomousRobot) robot).isMoveable = true;
                    ((AutonomousRobot) robot).initMovement();
                }
                if(robot instanceof ControlledRobot) {
                    ((ControlledRobot) robot).canControlled = true;
                }
            });
            isRunning = true;
            logger.info("Simulation resumed.");
        }
    }

    public void stopSimulation() {
        if (isRunning) {
            environment.getRobots().forEach(robot -> {
                if (robot instanceof AutonomousRobot) {
                    ((AutonomousRobot) robot).isMoveable = false;
                    ((AutonomousRobot) robot).stopMovement();
                }
                if(robot instanceof ControlledRobot) {
                    ((ControlledRobot) robot).canControlled = false;
                }
            });
            historyStates.clear(); // Clear history upon stop to reset simulation to initial state
            isRunning = false;
            logger.info("Simulation stopped.");
        }
    }

    public void saveState() {
        historyStates.push(new EnvironmentState(environment));
        logger.info("Simulation state saved. Current stack size: " + historyStates.size());
    }

    public void reverseSimulation() {
        pauseSimulation();
        if (!historyStates.isEmpty()) {
            List<Robot> oldRobots = new ArrayList<>(environment.getRobots());
            for (Robot robot : oldRobots) {
                environment.removeRobot(robot);
            }

            EnvironmentState previousState = historyStates.pop();
            previousState.restore(environment);

            notifyObservers();
            logger.info("Simulation reversed to a previous state.");
        } else {
            logger.warn("Attempted to reverse simulation but no states were saved in the history stack.");
        }
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(observer -> observer.update(this));
    }
}