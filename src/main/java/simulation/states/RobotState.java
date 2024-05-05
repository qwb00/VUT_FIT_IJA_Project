/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xposte00 - Aleksander Postelga
 * Represents the state of a robot in the simulation.
 * This class is used to save and restore the state of a robot.
 */
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
    private final int angle;
    private final boolean isActive;

    private final int detectionRange;
    private final int turnAngle;
    private final boolean turnDirection;

    /**
     * Constructs a RobotState from the given robot.
     * Captures the position, speed, angle, and other properties of the robot.
     *
     * @param robot The robot to capture the state from.
     */
    public RobotState(Robot robot) {
        this.robot = robot;
        this.position = robot.getPosition();
        this.speed = robot.getSpeed();
        this.angle = robot.angle();
        this.isActive = robot instanceof ControlledRobot && ((ControlledRobot) robot).isActive();

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

    /**
     * Restores the robot state in the specified environment.
     * Creates a new robot in the environment at the stored position and restores its properties.
     *
     * @param environment The environment to restore the robot in.
     */
    @Override
    public void restore(Environment environment) {
        Robot restoredRobot = null;
        if (robot instanceof AutonomousRobot) {
            restoredRobot = AutonomousRobot.create(environment, position, speed, detectionRange, turnAngle, turnDirection, angle);
        } else if (robot instanceof ControlledRobot) {
            restoredRobot = ControlledRobot.create(environment, position, speed, angle);
        }

        if (restoredRobot instanceof ControlledRobot) {
            ((ControlledRobot) restoredRobot).setActive(isActive);
        }
    }

}
