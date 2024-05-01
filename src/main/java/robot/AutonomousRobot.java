package main.java.robot;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.common.Robot;

import main.java.simulation.SimulationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

public class AutonomousRobot implements Robot {
    private final Environment env;
    private Position position;
    private int angle = 0;
    private final int detectionRange; // Distance to detect obstacles
    private final int turnAngle; // Angle to turn when an obstacle is detected
    private final boolean turnDirection;
    private final int speed;
    private final List<Observer> observers = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(AutonomousRobot.class);
    private Timer movementTimer;
    private SimulationManager simulationManager;
    public AutonomousRobot(Environment env, Position position, int speed, int detectionRange, int turnAngle, boolean turnDirection) {
        this.env = env;
        this.position = position;
        this.detectionRange = detectionRange;
        this.turnAngle = turnAngle;
        this.turnDirection = turnDirection;
        this.speed = speed;
        this.simulationManager = new SimulationManager(env);
    }

    public void initMovement() {
        movementTimer = new Timer();
        movementTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                move();  // move the robot
                notifyObservers();  // notify observers
            }
        }, 0, 1000);  // init movement with 1 second delay
    }

    public static AutonomousRobot create(Environment env, Position pos, int speed, int detectionRange, int turnAngle, boolean turnDirection) {
        if (env.containsPosition(pos) && !env.robotAt(pos)) {
            AutonomousRobot robot = new AutonomousRobot(env, pos, speed, detectionRange, turnAngle, turnDirection);
            if (env.addRobot(robot)) {
                logger.info("Added a new AutonomousRobot at position: col = {}, row = {}", pos.getCol(), pos.getRow());
                return robot;
            }
        }
        logger.warn("Failed to add a new AutonomousRobot at position: col = {} row = {}", pos.getCol(), pos.getRow());
        return null;
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

    public void stopMovement() {
        if (movementTimer != null) {
            movementTimer.cancel();  // Остановка таймера
            movementTimer.purge();   // Удаление всех задач
            movementTimer = null;
        }
    }

    @Override
    public void turn() {
        if (turnDirection) {
            angle = (angle + turnAngle) % 360;
            logger.info("Turned right to angle: {}", angle);
        } else {
            angle = (angle - turnAngle + 360) % 360;
            logger.info("Turned left to angle: {}", angle);
        }
    }

    @Override
    public int angle() {
        return angle;
    }

    @Override
    public boolean canMove() {
        return maxMovableSteps() > 0; // obstacle not detected
    }

    public int maxMovableSteps() {
        int steps = 0;
        for (int i = 1; i <= speed; i++) {
            Position nextPosition = calculateNextPosition(i);
            if (!env.containsPosition(nextPosition) || env.robotAt(nextPosition) || env.obstacleAt(nextPosition)) {
                break; // obstacle detected
            }
            steps = i;
        }
        return steps;
    }

    @Override
    public boolean move() {
        if (canMove()) {
            int movableSteps = maxMovableSteps();  // determine the maximum number of steps the robot can move
            if (movableSteps > 0) {
                this.position = calculateNextPosition(movableSteps);
                notifyObservers();
                logger.info("Moved to position: col = {}, row = {}", position.getCol(), position.getRow());
                return true;
            }
        } else {
            // obstacle detected
            turn();
            logger.info("Detected an obstacle within detection range, turned to angle: {}", angle);
            return false;
        }
        return false;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public AutonomousRobot clone() {
        try {
            AutonomousRobot cloned = (AutonomousRobot) super.clone();
            cloned.position = new Position(this.position.getRow(), this.position.getCol());
            return cloned;
        } catch (CloneNotSupportedException e) {
            logger.error("Failed to clone AutonomousRobot");
            throw new AssertionError();  // must not happen since we are Cloneable
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

    public int getSpeed() {
        return speed;
    }

    public int getDetectionRange() {
        return detectionRange;
    }

    public int getTurnAngle() {
        return turnAngle;
    }

    public boolean getTurnDirection() {
        return turnDirection;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public String toString() {
        return "AutonomousRobot\n"
                + "positionRow=" + position.getRow() + "\n"
                + "positionCol=" + position.getCol() + "\n"
                + "detectionRange=" + detectionRange + "\n"
                + "turnAngle=" + turnAngle + "\n"
                + "turnDirection=" + turnDirection + "\n"
                + "speed=" + speed + "\n";
    }
}
