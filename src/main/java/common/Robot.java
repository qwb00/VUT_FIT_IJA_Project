package main.java.common;

public interface Robot extends Observable {
    /**
     * Turns the robot by 45 degrees clockwise
     */
    void turn();

    /**
     * Returns the current angle of the robot's orientation
     *
     * @return The angle of the robot
     */
    int angle();

    /**
     * Checks whether the robot can move in its current direction
     *
     * @return true if the adjacent tile exists within the environment and is empty, false otherwise
     */
    boolean canMove();

    /**
     * Moves the robot in its current direction
     *
     * @return true if the move is successful, false otherwise
     */
    boolean move();

    /**
     * Returns information about the robot's current position
     *
     * @return The current position of the robot
     */
    Position getPosition();

    public Position calculateNextPosition();
}

