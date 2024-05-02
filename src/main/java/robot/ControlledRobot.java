package main.java.robot;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.common.Robot;

import main.java.simulation.SimulationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import main.java.common.Observable.Observer;
import main.java.common.Observable;

import java.util.ArrayList;
import java.util.List;

public class ControlledRobot implements Robot {
    private final Environment env;
    private Position position;
    private int angle = 0;
    private final int speed;
    private final List<Observer> observers = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(ControlledRobot.class);

    public boolean canControlled = false;
    private SimulationManager simulationManager;

    public ControlledRobot(Environment env, Position position, int speed, int angle) {
        this.env = env;
        this.position = position;
        this.speed = speed;
        this.simulationManager = SimulationManager.getInstance(env);
        this.angle = angle;
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

    @Override
    public int angle() {
        return angle;
    }

    @Override
    public boolean canMove() {
        return maxMovableSteps() > 0 && canControlled;
    }

    @Override
    public Position getPosition() {
        return position;
    }

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

    @Override
    public boolean move() {
        if (canMove()) {
            simulationManager.saveState();
            this.position = calculateNextPosition(maxMovableSteps());
            notifyObservers();
            logger.info("Moved to position: col = {}, row = {}", position.getCol(), position.getRow());
            return true;
        }
        return false;
    }

    @Override
    public void turn() {
        if (canControlled) {
            simulationManager.saveState();
            angle = (angle + 45) % 360;
            notifyObservers();
            logger.info("Turned clockwise to angle: {}", angle);
        }
    }

    public void turnCounterClockwise() {
        if (canControlled) {
            simulationManager.saveState();
            angle = (angle - 45 + 360) % 360;
            notifyObservers();
            logger.info("Turned counterclockwise to angle: {}", angle);
        }
    }

    public Position calculateNextPosition(int step) {
        int dx = 0, dy = 0;
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

    @Override
    public int getSpeed() {
        return speed;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return "ControlledRobot\n"
                + "positionRow=" + position.getRow() + "\n"
                + "positionCol=" + position.getCol() + "\n"
                + "speed=" + speed + "\n";
    }
}

