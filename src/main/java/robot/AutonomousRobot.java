package main.java.robot;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.common.Robot;

import java.util.ArrayList;
import java.util.List;

public class AutonomousRobot implements Robot {
    private final Environment env;
    private Position position;
    private int angle = 0;
    private final int detectionRange; // Distance to detect obstacles
    private final int turnAngle; // Angle to turn when an obstacle is detected
    private final boolean turnDirection;
    private final List<Observer> observers = new ArrayList<>();
    public AutonomousRobot(Environment env, Position position, int detectionRange, int turnAngle, boolean turnDirection) {
        this.env = env;
        this.position = position;
        this.detectionRange = detectionRange;
        this.turnAngle = turnAngle;
        this.turnDirection = turnDirection;
    }

    public static AutonomousRobot create(Environment env, Position pos, int detectionRange, int turnAngle, boolean turnDirection) {
        if (env.containsPosition(pos) && !env.robotAt(pos)) {
            AutonomousRobot robot = new AutonomousRobot(env, pos, detectionRange, turnAngle, turnDirection);
            if (env.addRobot(robot)) {
                return robot;
            }
        }
        return null;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
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

    @Override
    public void turn() {
        if (turnDirection) {
            angle = (angle + turnAngle) % 360;
        } else {
            angle = (angle - turnAngle + 360) % 360;
        }
    }

    @Override
    public int angle() {
        return angle;
    }

    @Override
    public boolean canMove() {
        for (int i = 1; i <= detectionRange; i++) {
            Position checkingPosition = calculateNextPosition(i);
            if (!env.containsPosition(checkingPosition) || env.robotAt(checkingPosition) || env.obstacleAt(checkingPosition)) {
                return false; // Obstacle detected
            }
        }
        return true;
    }

    @Override
    public boolean move() {
        if (canMove()) {
            this.position = calculateNextPosition(1);
            return true;
        } else {
            turn();
            return false;
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public Position calculateNextPosition(int steps) {
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
        return new Position(position.getRow() + (dy * steps), position.getCol() + (dx * steps));
    }

    public String toString() {
        return "AutonomousRobot\n"
                + "positionRow=" + position.getRow() + "\n"
                + "positionCol=" + position.getCol() + "\n"
                + "detectionRange=" + detectionRange + "\n"
                + "turnAngle=" + turnAngle + "\n"
                + "turnDirection=" + turnDirection + "\n";
    }
}
