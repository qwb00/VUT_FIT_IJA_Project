package main.java;


import main.java.common.Environment;
import main.java.common.Position;
import main.java.environment.Room;
import main.java.robot.ControlledRobot;
import main.java.common.Robot;
import main.java.EnvPresenter;
import main.java.view.ControlView;

import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class

Main {
    public static void main(String... args) {
        Environment room = Room.create(5, 8);

        room.createObstacleAt(1, 2);
        room.createObstacleAt(1, 4);
        room.createObstacleAt(1, 5);
        room.createObstacleAt(2, 5);

        Position p1 = new Position(4,2);
        Robot r1 = ControlledRobot.create(room, p1);
        Position p2 = new Position(4,7);
        Robot r2 = ControlledRobot.create(room, p2);

        Position p3 = new Position(1,1);
        Robot r3 = ControlledRobot.create(room, p3);

        r2.turn();

        EnvPresenter presenter = new EnvPresenter(room);

        r1.addObserver(presenter);
        r2.addObserver(presenter);
        r3.addObserver(presenter);

        presenter.open();


        sleep(1000);
        r2.turn();
        r1.move();
        sleep(1000);
        r1.turn();
        sleep(1000);
        r1.move();
        r2.move();
        sleep(1000);
        r1.turn();
        sleep(1000);
        r1.move();
        r2.move();
        sleep(1000);
        r1.move();
        sleep(1000);
        r1.turn();
        r2.turn();
    }

    /**
     * Uspani vlakna na zadany pocet ms.
     * @param ms Pocet ms pro uspani vlakna.
     */
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
