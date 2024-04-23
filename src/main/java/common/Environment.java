package main.java.common;

import java.util.List;

public interface Environment {
    /**
     * Adds a robot to its position
     *
     * @param robot The robot to be added
     * @return true if the operation is successful, false otherwise
     */
    boolean addRobot(Robot robot);

    /**
     * Creates an obstacle at the specified position
     *
     * @param row The row of the position
     * @param col The column of the position
     * @return true if the operation is successful, false otherwise
     */
    boolean createObstacleAt(int row, int col);

    /**
     * Checks if there is an obstacle at the specified row and column
     *
     * @param row The row to check
     * @param col The column to check
     * @return true if there is an obstacle at the specified position, false otherwise
     */
    boolean obstacleAt(int row, int col);

    /**
     * Checks if there is a robot at the specified position
     *
     * @param p The position to check
     * @return true if there is a robot at the specified position, false otherwise
     */
    boolean robotAt(Position p);

    /**
     * Checks if a given position is within the environment
     *
     * @param pos The position to check
     * @return true if the position is within the range of the environment, false otherwise
     */
    boolean containsPosition(Position pos);

    /**
     * Checks if there is an obstacle at the specified position
     *
     * @param p The position to check
     * @return true if there is an obstacle at the specified position, false otherwise
     */
    boolean obstacleAt(Position p);

    List<Robot> getRobots();

    List<Obstacle> getObstacles();
}
