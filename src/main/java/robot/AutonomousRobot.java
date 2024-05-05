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
    private int angle;
    private final int detectionRange; // Distance to detect obstacles
    private final int turnAngle; // Angle to turn when an obstacle is detected
    private final boolean turnDirection;
    private final int speed;
    private final List<Observer> observers = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(AutonomousRobot.class);
    private Timer movementTimer;
    private final SimulationManager simulationManager;
    public boolean isMoveable = false;
    public AutonomousRobot(Environment env, Position position, int speed, int detectionRange, int turnAngle, boolean turnDirection, int angle) {
        this.env = env;
        this.position = position;
        this.detectionRange = detectionRange;
        this.turnAngle = turnAngle;
        this.turnDirection = turnDirection;
        this.speed = speed;
        this.simulationManager = SimulationManager.getInstance(env);
        this.angle = angle;
    }

    /**
     * Initializes the movement of the robot
     */
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

    /**
     * Factory method for creating an AutonomousRobot instance
     *
     * @param env The environment in which the robot will operate
     * @param pos The initial position of the robot
     * @param speed The speed of the robot
     * @param detectionRange The distance to detect obstacles
     * @param turnAngle The angle to turn when an obstacle is detected
     * @param turnDirection The direction to turn when an obstacle is detected
     * @param startAngle The initial angle of the robot
     * @return A new AutonomousRobot instance
     */
    public static AutonomousRobot create(Environment env, Position pos, int speed, int detectionRange, int turnAngle, boolean turnDirection, int startAngle) {
        if (env.containsPosition(pos) && !env.robotAt(pos)) {
            AutonomousRobot robot = new AutonomousRobot(env, pos, speed, detectionRange, turnAngle, turnDirection, startAngle);
            if (env.addRobot(robot)) {
                //robot.initMovement();
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

    /**
     * Stops the movement of the robot
     */
    public void stopMovement() {
        if (movementTimer != null) {
            movementTimer.cancel();  // Остановка таймера
            movementTimer.purge();   // Удаление всех задач
            movementTimer = null;
        }
    }

    /**
     * Turns the robot
     */
    @Override
    public void turn() {
        simulationManager.saveState();
        if (turnDirection) {
            angle = (angle + turnAngle) % 360;
            logger.info("Turned right to angle: {}", angle);
        } else {
            angle = (angle - turnAngle + 360) % 360;
            logger.info("Turned left to angle: {}", angle);
        }
    }

    /**
     * Returns the current angle of the robot's orientation
     *
     * @return The angle of the robot
     */
    @Override
    public int angle() {
        return angle;
    }

    /**
     * Checks whether the robot can move in its current direction
     *
     * @return true if the adjacent tile exists within the environment and is empty, false otherwise
     */
    @Override
    public boolean canMove() {
        return maxMovableSteps() > 0; // obstacle not detected
    }

    /**
     * Returns the maximum number of steps the robot can move
     *
     * @return The maximum number of steps the robot can move
     */
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

    /**
     * Moves the robot
     */
    @Override
    public void move() {
        if (canMove()) {
            simulationManager.saveState();
            int movableSteps = maxMovableSteps();  // determine the maximum number of steps the robot can move
            if (movableSteps > 0) {
                this.position = calculateNextPosition(movableSteps);
                notifyObservers();
                logger.info("Moved to position: col = {}, row = {}", position.getCol(), position.getRow());
            }
        } else if (isMoveable) {
            // obstacle detected
            turn();
            logger.info("Detected an obstacle within detection range, turned to angle: {}", angle);
        }
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

    /**
     * Calculates the next position of the robot
     *
     * @param step The number of steps to move
     * @return The next position of the robot
     */
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
     * Returns the detection range of the robot
     *
     * @return The detection range of the robot
     */
    public int getDetectionRange() {
        return detectionRange;
    }

    /**
     * Returns the turn angle of the robot
     *
     * @return The turn angle of the robot
     */
    public int getTurnAngle() {
        return turnAngle;
    }

    /**
     * Returns the turn direction of the robot
     *
     * @return The turn direction of the robot
     */
    public boolean getTurnDirection() {
        return turnDirection;
    }

    /**
     * Returns the environment in which the robot operates
     *
     * @return The environment in which the robot operates
     */
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
