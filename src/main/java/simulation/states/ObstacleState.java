/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xposte00 - Aleksander Postelga
 * Represents the state of an obstacle.
 * This class is used to save and restore the state of an obstacle.
 */
package main.java.simulation.states;

import main.java.common.Environment;
import main.java.common.Obstacle;
import main.java.common.Position;

public class ObstacleState implements State {
    private final int row;
    private final int col;

    /**
     * Constructs an ObstacleState from the given obstacle.
     * Captures the position of the obstacle in the environment.
     *
     * @param obstacle The obstacle to capture the state from.
     */
    public ObstacleState(Obstacle obstacle) {
        this.row = obstacle.getPosition().getRow();
        this.col = obstacle.getPosition().getCol();
    }

    /**
     * Restores the obstacle state in the specified environment.
     * Creates a new obstacle in the environment at the stored position.
     *
     * @param environment The environment to restore the obstacle in.
     */
    @Override
    public void restore(Environment environment) {
        Obstacle obstacle = new Obstacle(new Position(row, col));
        environment.createObstacleAt(obstacle.getPosition().getRow(), obstacle.getPosition().getCol());
    }
}
