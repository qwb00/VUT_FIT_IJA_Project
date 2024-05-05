/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xposte00 - Aleksander Postelga
 */
package main.java.simulation.states;

import main.java.common.Environment;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents the state of an environment, including its robots and obstacles.
 * This class is used to save and restore the state of the environment.
 */
public class EnvironmentState implements State {
    private final List<State> states = new ArrayList<>();

    /**
     * Constructs an EnvironmentState from the given environment.
     * It captures the current state of all robots and obstacles in the environment.
     *
     * @param environment The environment to capture the state from.
     */
    public EnvironmentState(Environment environment) {
        environment.getRobots().forEach(robot -> states.add(new RobotState(robot)));
        environment.getObstacles().forEach(obstacle -> states.add(new ObstacleState(obstacle)));
    }

    /**
     * Restores the state of the environment to the saved state.
     * All existing robots and obstacles in the environment are removed and replaced
     * with those from the saved state.
     *
     * @param environment The environment to restore the state to.
     */
    @Override
    public void restore(Environment environment) {
        environment.clearRobots();
        environment.clearObstacles();
        states.forEach(state -> state.restore(environment));
    }
}
