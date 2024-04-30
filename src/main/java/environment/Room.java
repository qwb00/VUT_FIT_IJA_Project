package main.java.environment;

import main.java.common.Environment;
import main.java.common.Obstacle;
import main.java.common.Position;
import main.java.common.Robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Room implements Environment {
    private final int rows;
    private final int cols;
    private final List<Robot> robots;
    private final List<Obstacle> obstacles;
    private static final Logger logger = LogManager.getLogger(Room.class);

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
        logger.info("Creating a new Room with dimensions: cols = {}, rows = {}", rows, cols);
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
    public boolean removeRobot(Robot robot) {
        boolean removed = robots.remove(robot);
        if (removed) {
            logger.info("Robot removed from the environment at position: {}, {}", robot.getPosition().getCol(), robot.getPosition().getRow());
        } else {
            logger.warn("Failed to remove robot: Robot not found in the environment.");
        }
        return removed;
    }

    /**
     * Clears all robots from the room.
     */
    public void clearRobots() {
        robots.clear();
        logger.info("All robots have been removed from the room.");
    }

    /**
     * Clears all obstacles from the room.
     */
    public void clearObstacles() {
        obstacles.clear();
        logger.info("All obstacles have been removed from the room.");
    }

    /**
     * Resets the room by removing all robots and obstacles.
     */
    public void resetRoom() {
        clearRobots();
        clearObstacles();
        logger.info("Room has been reset.");
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
        logger.info("Created a new Obstacle at position: col = {}, row = {}", newObstacle.getPosition().getCol(), newObstacle.getPosition().getRow());
        return true;
    }

    public boolean removeObstacleAt(int row, int col) {
        if (row < 0 || row >= this.rows || col < 0 || col >= this.cols) {
            return false;
        }
        Obstacle toRemove = obstacles.stream()
                .filter(obstacle -> obstacle.getPosition().getRow() == row && obstacle.getPosition().getCol() == col)
                .findFirst()
                .orElse(null);
        if (toRemove != null) {
            obstacles.remove(toRemove);
            logger.info("Removed an Obstacle at position: col = {}, row = {}", toRemove.getPosition().getCol(), toRemove.getPosition().getRow());
            return true;
        }
        logger.error("No Obstacle found at position: ({}, {})", row, col);
        return false;
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
    public int getRows() {
        return this.rows;
    }

    @Override
    public int getCols() {
        return this.cols;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Room\n");
        sb.append("Rows=").append(rows).append("\n");
        sb.append("Cols=").append(cols).append("\n");
        sb.append("Obstacles=").append(obstacles.size()).append("\n");
        for (Obstacle obstacle : obstacles) {
            sb.append(obstacle.toString());
        }
        sb.append("Robots=").append(robots.size()).append("\n");
        for (Robot robot : robots) {
            sb.append(robot.toString());
        }
        return sb.toString();
    }
}

