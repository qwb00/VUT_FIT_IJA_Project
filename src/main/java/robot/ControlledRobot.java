package main.java.robot;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.common.Robot;

public class ControlledRobot implements Robot {
    private final Environment env;
    private Position position;
    private int angle = 0;

    private ControlledRobot(Environment env, Position position) {
        this.env = env;
        this.position = position;
    }

    /**
     * Creates an instance of ControlledRobot and places it at the specified position within the given environment
     *
     * @param env The environment where the robot will be placed
     * @param pos The intended position for the robot within the environment
     * @return An instance of ControlledRobot placed at the given position or null if the position is invalid
     */
    public static ControlledRobot create(Environment env, Position pos) {
        if (env.containsPosition(pos) && !env.robotAt(pos)) {
            ControlledRobot robot = new ControlledRobot(env, pos);
            if (env.addRobot(robot)) {
                return robot;
            }
        }
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
            return true;
        }
        return false;
    }

    @Override
    public void turn() {
        angle = (angle + 45) % 360;
    }

    public void turnClockwise() {
        angle = (angle + 45) % 360;
    }

    public void turnCounterClockwise() {
        angle = (angle - 45 + 360) % 360;
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
        return new Position(position.getRow() + dy, position.getCol() + dx);
    }
}

