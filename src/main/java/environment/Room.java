/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 * Represents a simulation environment in the form of a room.
 * This environment contains robots and obstacles, and it provides methods to interact with them.
 */
package main.java.environment;

import main.java.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Room implements Environment, Observable {
    private final int rows;
    private final int cols;
    private final List<Robot> robots;
    private final List<Obstacle> obstacles;
    private static final Logger logger = LogManager.getLogger(Room.class);
    private final List<Observer> observers = new ArrayList<>();

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

    /**
     * Adds a robot to the room at the specified position
     *
     * @param robot The robot to add
     * @return true if the robot was added successfully, false otherwise
     */
    @Override
    public boolean addRobot(Robot robot) {
        if (robot == null || !containsPosition(robot.getPosition()) ||
                obstacleAt(robot.getPosition()) || robotAt(robot.getPosition())) {
            return false;
        }
        robots.add(robot);
        return true;
    }

    /**
     * Removes the specified robot from the room
     *
     * @param robot The robot to remove
     */
    @Override
    public void removeRobot(Robot robot) {
        boolean removed = robots.remove(robot);
        if (removed) {
            logger.info("Robot removed from the environment at position: {}, {}", robot.getPosition().getCol(), robot.getPosition().getRow());
        } else {
            logger.warn("Failed to remove robot: Robot not found in the environment.");
        }
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
     * Checks whether the specified position is within the bounds of the room
     *
     * @param pos The position to check
     * @return true if the position is within the room, false otherwise
     */
    @Override
    public boolean containsPosition(Position pos) {
        return pos.getRow() >= 0 && pos.getRow() < this.rows &&
                pos.getCol() >= 0 && pos.getCol() < this.cols;
    }

    /**
     * Creates a new robot at the specified position
     *
     * @param row The row of the position
     * @param col The column of the position
     */
    @Override
    public void createObstacleAt(int row, int col) {
        if (row < 0 || row >= this.rows || col < 0 || col >= this.cols ||
                obstacleAt(row, col) || robotAt(new Position(row, col))) {
            return;
        }
        Obstacle newObstacle = new Obstacle(new Position(row, col));
        obstacles.add(newObstacle);
        logger.info("Created a new Obstacle at position: col = {}, row = {}", newObstacle.getPosition().getCol(), newObstacle.getPosition().getRow());
    }

    /**
     * Removes an obstacle at the specified position
     *
     * @param row The row of the position
     * @param col The column of the position
     */
    public void removeObstacleAt(int row, int col) {
        if (row < 0 || row >= this.rows || col < 0 || col >= this.cols) {
            return;
        }
        Obstacle toRemove = obstacles.stream()
                .filter(obstacle -> obstacle.getPosition().getRow() == row && obstacle.getPosition().getCol() == col)
                .findFirst()
                .orElse(null);
        if (toRemove != null) {
            obstacles.remove(toRemove);
            logger.info("Removed an Obstacle at position: col = {}, row = {}", toRemove.getPosition().getCol(), toRemove.getPosition().getRow());
            return;
        }
        logger.error("No Obstacle found at position: ({}, {})", row, col);
    }


    /**
     * Checks whether an obstacle exists at the specified position
     *
     * @param row The row of the position
     * @param col The column of the position
     * @return true if an obstacle exists at the specified position, false otherwise
     */
    @Override
    public boolean obstacleAt(int row, int col) {
        return obstacles.stream().anyMatch(obstacle ->
                obstacle.getPosition().getRow() == row && obstacle.getPosition().getCol() == col);
    }

    /**
     * Checks whether an obstacle exists at the specified position
     *
     * @param p The position to check
     * @return true if an obstacle exists at the specified position, false otherwise
     */
    @Override
    public boolean obstacleAt(Position p) {
        return obstacleAt(p.getRow(), p.getCol());
    }

    /**
     * Checks whether a robot exists at the specified position
     *
     * @param p The position to check
     * @return true if a robot exists at the specified position, false otherwise
     */
    @Override
    public boolean robotAt(Position p) {
        return robots.stream().anyMatch(robot ->
                robot.getPosition().equals(p));
    }

    /*
     * Returns the list of robots in the room
     *
     * @return The list of robots in the room
     */
    @Override
    public List<Robot> getRobots() {
        return robots;
    }

    /**
     * Returns the list of obstacles in the room
     *
     * @return The list of obstacles in the room
     */
    @Override
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    /**
     * Returns the number of rows in the room
     *
     * @return The number of rows in the room
     */
    @Override
    public int getRows() {
        return this.rows;
    }

    /**
     * Returns the number of columns in the room
     *
     * @return The number of columns in the room
     */
    @Override
    public int getCols() {
        return this.cols;
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
        observers.forEach(observer -> observer.update(this));
    }

    /**
     * Returns a string representation of the room
     *
     * @return A string representation of the room
     */
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

