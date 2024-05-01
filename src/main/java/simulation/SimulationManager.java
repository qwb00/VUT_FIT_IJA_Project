package main.java.simulation;

import main.java.EnvPresenter;
import main.java.common.Environment;
import main.java.common.Observable;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;
import main.java.simulation.states.EnvironmentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SimulationManager implements Observable {
    private Environment environment; // Specific to Room rather than Environment
    private boolean isRunning = false;
    private final Stack<EnvironmentState> historyStates = new Stack<>();
    private static final Logger logger = LogManager.getLogger(SimulationManager.class);
    private List<Observer> observers = new ArrayList<>();

    private EnvPresenter presenter;

    public SimulationManager(Environment environment) {
        this.environment = environment;
    }

    public void setEnvironment(Environment newEnvironment) {
        this.environment = newEnvironment;
    }

    public void pauseSimulation() {
        if (isRunning) {
            environment.getRobots().forEach(robot -> {
                if (robot instanceof AutonomousRobot) {
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
        logger.info("Simulation state saved.");
    }

    public void reverseSimulation() {
        pauseSimulation();
        if (!historyStates.isEmpty()) {
            EnvironmentState previousState = historyStates.pop();
            previousState.restore(environment);
            notifyObservers();
            logger.info("Simulation reversed to a previous state.");
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