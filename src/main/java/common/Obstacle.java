package main.java.common;

import main.java.simulation.SimulationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Obstacle implements Cloneable {
    private Position pos;
    private final Environment env;
    private static final Logger logger = LogManager.getLogger(Obstacle.class.getName());

    /**
     * Constructor that sets the environment and position of the obstacle
     *
     * @param pos The position of the obstacle within the environment
     * @param env The environment to which the obstacle belongs
     */
    public Obstacle(Environment env, Position pos) {
        this.env = env;
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

    public String toString() {
        return "Obstacle " + pos.toString();
    }

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