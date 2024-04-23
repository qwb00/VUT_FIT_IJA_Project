package main.java.common;

public class Obstacle {
    private final Position pos;
    private final Environment env;

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
}