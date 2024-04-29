package main.java.robot;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.common.Robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class ControlledRobot implements Robot {
    private final Environment env;
    private Position position;
    private int angle = 0;
    private final int speed;
    private final List<Observer> observers = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(ControlledRobot.class);

    private ControlledRobot(Environment env, Position position, int speed) {
        this.env = env;
        this.position = position;
        this.speed = speed;
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
    public static ControlledRobot create(Environment env, Position pos, int speed) {
        if (env.containsPosition(pos) && !env.robotAt(pos)) {
            ControlledRobot robot = new ControlledRobot(env, pos, speed);
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
        Position nextPosition = calculateNextPosition();
        return env.containsPosition(nextPosition) && !env.robotAt(nextPosition) && !env.obstacleAt(nextPosition);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public boolean move() {
        if (canMove()) {
            this.position = calculateNextPosition();
            notifyObservers();
            logger.info("Controlled robot moved to position: col = {}, row = {}", position.getCol(), position.getRow());
            return true;
        }
        return false;
    }

    @Override
    public void turn() {
        angle = (angle + 45) % 360;
        notifyObservers();
        logger.info("ControlledRobot turned clockwise to angle: {}", angle);
    }

    public void turnCounterClockwise() {
        angle = (angle - 45 + 360) % 360;
        notifyObservers();
        logger.info("ControlledRobot turned counterclockwise to angle: {}", angle);
    }

    public Position calculateNextPosition() {
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
        return new Position(position.getRow() + (dy * speed), position.getCol() + (dx * speed));
    }

    @Override
    public String toString() {
        return "ControlledRobot\n"
                + "positionRow=" + position.getRow() + "\n"
                + "positionCol=" + position.getCol() + "\n"
                + "speed=" + speed + "\n";
    }
}

