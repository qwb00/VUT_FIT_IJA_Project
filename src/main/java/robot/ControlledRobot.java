/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xposte00 - Aleksander Postelga
 * Represents a controlled robot that can move and turn based on user input.
 * This robot interacts with the environment and maintains its position and state.
 */
package main.java.robot;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.common.Robot;

import main.java.simulation.SimulationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ControlledRobot implements Robot {
    private final Environment env;
    private Position position;
    private int angle;
    private final int speed;
    private final List<Observer> observers = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(ControlledRobot.class);
    private boolean active;

    public boolean canControlled = false;
    private final SimulationManager simulationManager;

    public ControlledRobot(Environment env, Position position, int speed, int angle) {
        this.env = env;
        this.position = position;
        this.speed = speed;
        this.simulationManager = SimulationManager.getInstance(env);
        this.angle = angle;
        this.active = false;
    }

    /**
     * Sets the robot active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns whether the robot is active
     *
     * @return true if the robot is active, false otherwise
     */
    public boolean isActive() {
        return active;
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
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    /**
     * Creates an instance of ControlledRobot and places it at the specified position within the given environment
     *
     * @param env The environment where the robot will be placed
     * @param pos The intended position for the robot within the environment
     * @return An instance of ControlledRobot placed at the given position or null if the position is invalid
     */
    public static ControlledRobot create(Environment env, Position pos, int speed, int startAngle) {
        if (env.containsPosition(pos) && !env.robotAt(pos)) {
            ControlledRobot robot = new ControlledRobot(env, pos, speed, startAngle);
            if (env.addRobot(robot)) {
                logger.info("Added a new ControlledRobot at position: col = {}, row = {}", pos.getCol(), pos.getRow());
                return robot;
            }
        }
        logger.warn("Failed to add a new ControlledRobot at position: col = {}, row = {}", pos.getCol(), pos.getRow());
        return null;
    }

    /**
     * Returns the angle of the robot
     *
     * @return The angle of the robot
     */
    @Override
    public int angle() {
        return angle;
    }

    /**
     * Checks whether the robot can move
     *
     * @return true if the robot can move, false otherwise
     */
    @Override
    public boolean canMove() {
        return maxMovableSteps() > 0 && canControlled;
    }

    /**
     * Returns the current position of the robot
     *
     * @return The current position of the robot
     */
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * Returns a deep copy of the robot
     *
     * @return A deep copy of the robot
     */
    @Override
    public ControlledRobot clone() {
        try {
            ControlledRobot cloned = (ControlledRobot) super.clone();
            cloned.position = new Position(this.position.getRow(), this.position.getCol());
            return cloned;
        } catch (CloneNotSupportedException e) {
            logger.error("Failed to clone the ControlledRobot object.");
            throw new AssertionError();  // must not happen since we are Cloneable
        }
    }

    /**
     * Returns the maximum number of steps the robot can move
     *
     * @return The maximum number of steps the robot can move
     */
    public int maxMovableSteps() {
        int steps = 0;
        for (int step = 1; step <= speed; step++) {
            Position nextPosition = calculateNextPosition(step);
            if (!env.containsPosition(nextPosition) || env.obstacleAt(nextPosition) || env.robotAt(nextPosition)) {
                break;
            }
            steps = step;  // Update the number of steps if the current step is valid
        }
        return steps;
    }

    /**
     * Moves the robot
     */
    @Override
    public void move() {
        if (canMove()) {
            simulationManager.saveState();
            this.position = calculateNextPosition(maxMovableSteps());
            notifyObservers();
            logger.info("Moved to position: col = {}, row = {}", position.getCol(), position.getRow());
        }
    }

    /**
     * Turns the robot by 45 degrees clockwise
     */
    @Override
    public void turn() {
        if (canControlled) {
            simulationManager.saveState();
            angle = (angle + 45) % 360;
            notifyObservers();
            logger.info("Turned clockwise to angle: {}", angle);
        }
    }

    /**
     * Turns the robot by 45 degrees counterclockwise
     */
    public void turnCounterClockwise() {
        if (canControlled) {
            simulationManager.saveState();
            angle = (angle - 45 + 360) % 360;
            notifyObservers();
            logger.info("Turned counterclockwise to angle: {}", angle);
        }
    }

    /**
     * Calculates the next position of the robot after moving a specified number of steps
     *
     * @param step The number of steps to move
     * @return The next position of the robot
     */
    public Position calculateNextPosition(int step) {
        int dx = 0;
        int dy = 0;
        switch (angle) {
            case 0:
                dy = -1;
                break;
            case 45:
                dx = 1; dy = -1;
                break;
            case 90:
                dx = 1;
                break;
            case 135:
                dx = 1; dy = 1;
                break;
            case 180:
                dy = 1;
                break;
            case 225:
                dx = -1; dy = 1;
                break;
            case 270:
                dx = -1;
                break;
            case 315:
                dx = -1; dy = -1;
                break;
        }
        return new Position(position.getRow() + (dy * step), position.getCol() + (dx * step));
    }

    /**
     * Returns the speed of the robot
     *
     * @return The speed of the robot
     */
    @Override
    public int getSpeed() {
        return speed;
    }

    /**
     * Return the string representation of the ControlledRobot
     *
     * @return The string representation of the ControlledRobot
     */
    @Override
    public String toString() {
        return "ControlledRobot\n"
                + "positionRow=" + position.getRow() + "\n"
                + "positionCol=" + position.getCol() + "\n"
                + "speed=" + speed + "\n";
    }
}

