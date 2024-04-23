package src.main.java;


import main.java.common.Environment;
import main.java.common.Position;
import main.java.environment.Room;
import main.java.robot.ControlledRobot;
import main.java.common.Robot;

public class Main {
    public static void main(String... args) {
        Environment room = Room.create(8, 8);
        room.createObstacleAt(1, 3);
        room.createObstacleAt(1, 4);
        room.createObstacleAt(2, 5);
        room.createObstacleAt(2, 4);
        System.out.println("Created room with obstacles at (1,3), (1,4), (2,5), and (2,4)");

        Position p1 = new Position(1,1);
        Robot r1 = ControlledRobot.create(room, p1);
        System.out.println("Created robot at (1,1)");

        Position p2 = new Position(1,2);
        Robot r2 = ControlledRobot.create(room, p2);
        System.out.println("Created robot at (1,2)");

        for(int i = 0; i < 8; i++) {
            if (r1.canMove()) {
                System.out.println("First robot move: " + r1.getPosition());
                r1.move();
                System.out.println("First robot move: " + r1.getPosition());
            }
            else {
                r1.turn();
            }

            if (r2.canMove()) {
                System.out.println("Second robot move: " + r2.getPosition());
                r2.move();
                System.out.println("Second robot move: " + r2.getPosition());
            }
            else {
                r2.turn();
            }
        }
    }
}
