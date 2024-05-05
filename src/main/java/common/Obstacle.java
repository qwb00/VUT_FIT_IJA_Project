/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xposte00 - Aleksander Postelga
 */

package main.java.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents an obstacle within the simulation environment.
 * It maintains its position and provides functionality to clone itself.
 */
public class Obstacle implements Cloneable {
    private Position pos;
    private static final Logger logger = LogManager.getLogger(Obstacle.class.getName());

    /**
     * Constructor that sets the environment and position of the obstacle
     *
     * @param pos The position of the obstacle within the environment
     */
    public Obstacle(Position pos) {
        this.pos = pos;
    }

    /**
     * Returns the position of the obstacle in the environment
     *
     * @return The position of the obstacle
     */
    public Position getPosition() {
        return pos;
    }

    /**
     * Gets the string representation of the obstacle
     * @return The string representation of the obstacle
     */
    public String toString() {
        return "Obstacle " + pos.toString();
    }

    /**
     * Clones the obstacle
     * @return A clone of the obstacle
     */
    @Override
    public Obstacle clone() {
        try {
            Obstacle clone = (Obstacle) super.clone();
            clone.pos = new Position(this.pos.getRow(), this.pos.getCol());
            return clone;
        } catch (CloneNotSupportedException e) {
            logger.error("Failed to clone Obstacle");
            throw new AssertionError();
        }
    }
}