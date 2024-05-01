package main.java.simulation.states;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.common.Robot;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;

public class RobotState implements State{
    private final Robot robot;

    public RobotState(Robot robot) {
        this.robot = robot;
    }

    @Override
    public void restore(Environment environment) {
        if (this.robot instanceof AutonomousRobot) {
            AutonomousRobot autonomousRobot = (AutonomousRobot) this.robot;
            AutonomousRobot.create(environment, autonomousRobot.getPosition(), autonomousRobot.getSpeed(), autonomousRobot.getDetectionRange(), autonomousRobot.getTurnAngle(), autonomousRobot.getTurnDirection());
        } else if (this.robot instanceof ControlledRobot) {
            ControlledRobot controlledRobot = (ControlledRobot) this.robot;
            ControlledRobot.create(environment, controlledRobot.getPosition(), controlledRobot.getSpeed());
        }
    }
}
