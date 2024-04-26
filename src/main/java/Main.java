package main.java;


import main.java.common.Environment;
import main.java.common.Position;
import main.java.environment.Room;
import main.java.robot.ControlledRobot;
import main.java.common.Robot;

public class

Main {
    public static void main(String... args) {
        Environment env = new Room(10, 10);
        ControlledRobot robot = ControlledRobot.create(env, new Position(0, 0));
        if (robot != null) {
            System.out.println("Robot created at " + robot.getPosition());
            System.out.println("Robot angle: " + robot.angle());
            System.out.println("Robot can move: " + robot.canMove());
            System.out.println("Robot moved: " + robot.move());
            System.out.println("Robot position: " + robot.getPosition());
            System.out.println("Robot angle: " + robot.angle());
            System.out.println("Robot can move: " + robot.canMove());
            System.out.println("Robot moved: " + robot.move());
            System.out.println("Robot position: " + robot.getPosition());
            System.out.println("Robot angle: " + robot.angle());
        } else {
            System.out.println("Robot could not be created");
        }
    }
}
