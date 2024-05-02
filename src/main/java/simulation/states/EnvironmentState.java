package main.java.simulation.states;

import main.java.common.Environment;
import java.util.List;
import java.util.ArrayList;

public class EnvironmentState implements State {
    private final List<State> states = new ArrayList<>();

    public EnvironmentState(Environment environment) {
        environment.getRobots().forEach(robot -> states.add(new RobotState(robot)));
        environment.getObstacles().forEach(obstacle -> states.add(new ObstacleState(obstacle)));
    }

    @Override
    public void restore(Environment environment) {
        environment.clearRobots();
        environment.clearObstacles();
        states.forEach(state -> state.restore(environment));
    }
}
