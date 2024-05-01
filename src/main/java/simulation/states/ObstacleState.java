package main.java.simulation.states;

import main.java.common.Environment;
import main.java.common.Obstacle;
import main.java.common.Position;

public class ObstacleState implements State {
    private final int row;
    private final int col;

    public ObstacleState(Obstacle obstacle) {
        this.row = obstacle.getPosition().getRow();
        this.col = obstacle.getPosition().getCol();
    }

    @Override
    public void restore(Environment environment) {
        Obstacle obstacle = new Obstacle(environment, new Position(row, col));
        environment.createObstacleAt(obstacle.getPosition().getRow(), obstacle.getPosition().getCol());
    }
}
