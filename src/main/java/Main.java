package main.java;


import main.java.Configuration.Configuration;
import main.java.common.Environment;
import main.java.common.Obstacle;
import main.java.common.Position;
import main.java.environment.Room;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;
import main.java.common.Robot;

import static main.java.Configuration.Configuration.loadConfiguration;

public class Main {
    public static void main(String... args) {
        try {
            // Загрузка конфигурации из файла
            Environment env = loadConfiguration("/Users/aleksander/Documents/SchoolProjects/IJA/IJA-Project/config.txt");

            if (env instanceof Room) {
                Room room = (Room) env;

                // Вывод информации о препятствиях
                System.out.println("Obstacles:");
                for(Obstacle obstacle : room.getObstacles()) {
                    Position pos = obstacle.getPosition();
                    System.out.println("Obstacle at Row=" + pos.getRow() + " Col=" + pos.getCol());
                }

                // Вывод информации о роботах
                System.out.println("Robots:");
                for(Robot robot : room.getRobots()) {
                    Position pos = robot.getPosition();
                    if(robot instanceof ControlledRobot)
                        System.out.println("Controlled Robot at Row=" + pos.getRow() + " Col=" + pos.getCol());
                    else if(robot instanceof AutonomousRobot)
                        System.out.println("Autonomous Robot at Row=" + pos.getRow() + " Col=" + pos.getCol());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }
}
