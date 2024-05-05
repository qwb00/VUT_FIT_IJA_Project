/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xposte00 - Aleksander Postelga
 * The SimulationManager class is responsible for managing the simulation state,
 * including starting, pausing, stopping, and reversing the simulation.
 * It follows the Observable pattern to notify observers of simulation state changes.
 */

package main.java.simulation;

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
    private Environment environment;
    private boolean isRunning = false;
    private final Stack<EnvironmentState> historyStates;
    private static final Logger logger = LogManager.getLogger(SimulationManager.class);
    private final List<Observer> observers = new ArrayList<>();
    private Robot activeRobot;

    public SimulationManager(Environment environment) {
        this.environment = environment;
        historyStates = new Stack<>();
    }

    /**
     * Sets the environment for the simulation manager
     *
     * @param newEnvironment The new environment to set
     */
    public void setEnvironment(Environment newEnvironment) {
        this.environment = newEnvironment;
    }

    /**
     * Returns the environment for the simulation manager
     *
     * @return The environment for the simulation manager
     */
    public static SimulationManager getInstance(Environment environment) {
        if (instance == null) {
            instance = new SimulationManager(environment);
        }
        return instance;
    }

    /**
     * Starts the simulation
     */
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

    /**
     * Pauses the simulation
     */
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

    /**
     * Stops the simulation
     */
    public void stopSimulation() {
        pauseSimulation();
        if(!historyStates.isEmpty()
        EnvironmentState previousState = null;
        for (int i = 0; i < historyStates.size(); i++) {
            previousState = historyStates.pop();
        }

    }

    /**
     * Saves the current state of the simulation
     */
    public void saveState() {
        historyStates.push(new EnvironmentState(environment));
        logger.info("Simulation state saved. Current stack size: {}", historyStates.size());
    }

    /**
     * Reverses the simulation to a previous state
     */
    public void reverseSimulation() {
        pauseSimulation();
        if (!historyStates.isEmpty()) {
            environment.getRobots().clear();

            EnvironmentState previousState = historyStates.pop();
            previousState.restore(environment);

            activeRobot = environment.getRobots().stream()
                    .filter(robot -> robot instanceof ControlledRobot && ((ControlledRobot) robot).isActive())
                    .findFirst()
                    .orElse(null);

            notifyObservers();
            logger.info("Simulation reversed to a previous state.");
        } else {
            logger.warn("Attempted to reverse simulation but no states were saved in the history stack.");
        }
    }

    /**
     * Returns the active robot in the simulation
     *
     * @return The active robot in the simulation
     */
    public Robot getActiveRobot() {
        return activeRobot;
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

    public boolean isRunning() {
        return isRunning;
    }
}