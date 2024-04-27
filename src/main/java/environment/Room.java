package main.java.environment;

import main.java.common.Environment;
import main.java.common.Obstacle;
import main.java.common.Position;
import main.java.common.Robot;

import java.util.ArrayList;
import java.util.List;

public class Room implements Environment {
    private final int rows;
    private final int cols;
    private final List<Robot> robots;
    private final List<Obstacle> obstacles;

    public Room(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.robots = new ArrayList<>();
        this.obstacles = new ArrayList<>();
    }

    /**
     * Factory method for creating a Room instance with specified dimensions
     *
     * @param rows The number of rows in the grid
     * @param cols The number of columns in the grid
     * @return A new Room instance
     * @throws IllegalArgumentException If the specified dimensions are invalid
     */
    public static Room create(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("The number of rows and columns must be positive.");
        }
        return new Room(rows, cols);
    }

    @Override
    public boolean addRobot(Robot robot) {
        if (robot == null || !containsPosition(robot.getPosition()) ||
                obstacleAt(robot.getPosition()) || robotAt(robot.getPosition())) {
            return false;
        }
        robots.add(robot);
        return true;
    }

    @Override
    public boolean containsPosition(Position pos) {
        return pos.getRow() >= 0 && pos.getRow() < this.rows &&
                pos.getCol() >= 0 && pos.getCol() < this.cols;
    }

    @Override
    public boolean createObstacleAt(int row, int col) {
        if (row < 0 || row >= this.rows || col < 0 || col >= this.cols ||
                obstacleAt(row, col) || robotAt(new Position(row, col))) {
            return false;
        }
        Obstacle newObstacle = new Obstacle(this, new Position(row, col));
        obstacles.add(newObstacle);
        return true;
    }

    @Override
    public boolean obstacleAt(int row, int col) {
        return obstacles.stream().anyMatch(obstacle ->
                obstacle.getPosition().getRow() == row && obstacle.getPosition().getCol() == col);
    }

    @Override
    public boolean obstacleAt(Position p) {
        return obstacleAt(p.getRow(), p.getCol());
    }

    @Override
    public boolean robotAt(Position p) {
        return robots.stream().anyMatch(robot ->
                robot.getPosition().equals(p));
    }

    @Override
    public List<Robot> getRobots() {
        return robots;
    }

    @Override
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    @Override
    public int rows() {
        return this.rows;
    }

    @Override
    public int cols() {
        return this.cols;
    }
}
