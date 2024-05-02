package main.java.simulation.states;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.common.Robot;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;

public class RobotState implements State {
    private final Robot robot;
    private final Position position;
    private final int speed;
    private final int angle;  // Для ControlledRobot

    // Для AutonomousRobot
    private final int detectionRange;
    private final int turnAngle;
    private final boolean turnDirection;

    public RobotState(Robot robot) {
        this.robot = robot;
        this.position = robot.getPosition();
        this.speed = robot.getSpeed();
        this.angle = robot.angle();

        if (robot instanceof AutonomousRobot) {
            AutonomousRobot ar = (AutonomousRobot) robot;
            this.detectionRange = ar.getDetectionRange();
            this.turnAngle = ar.getTurnAngle();
            this.turnDirection = ar.getTurnDirection();
        } else {
            this.detectionRange = 0;
            this.turnAngle = 0;
            this.turnDirection = false;
        }
    }

    @Override
    public void restore(Environment environment) {
        if (robot instanceof AutonomousRobot) {
            AutonomousRobot.create(environment, position, speed, detectionRange, turnAngle, turnDirection, angle);
        } else if (robot instanceof ControlledRobot) {
            ControlledRobot.create(environment, position, speed, angle);
        }
    }
}
