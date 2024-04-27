package main.java.Configuration;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.environment.Room;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;

import java.io.*;
import java.util.Scanner;

public class Configuration {
    public static void saveConfiguration(Environment env, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(env.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Environment loadConfiguration(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        Environment room = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.equals("Room")) {
                int rows = Integer.parseInt(scanner.nextLine().split("=")[1]);
                int cols = Integer.parseInt(scanner.nextLine().split("=")[1]);
                room = Room.create(rows, cols);
                scanner.nextLine(); // Skip the Obstacles count
            } else if (line.startsWith("Obstacle")) {
                scanner.nextLine(); // Skip "Position"
                int row = Integer.parseInt(scanner.nextLine().split("=")[1]);
                int col = Integer.parseInt(scanner.nextLine().split("=")[1]);
                if (room != null) {
                    room.createObstacleAt(row, col);
                }
            } else if (line.contains("Robot")) {
                String robotType = line.trim();
                int row = Integer.parseInt(scanner.nextLine().split("=")[1]);
                int col = Integer.parseInt(scanner.nextLine().split("=")[1]);

                if (robotType.equals("AutonomousRobot")) {
                    int detectionRange = Integer.parseInt(scanner.nextLine().split("=")[1]);
                    int turnAngle = Integer.parseInt(scanner.nextLine().split("=")[1]);
                    boolean turnDirection = Boolean.parseBoolean(scanner.nextLine().split("=")[1]);
                    AutonomousRobot robot = AutonomousRobot.create(room, new Position(row, col), detectionRange, turnAngle, turnDirection);
                    room.addRobot(robot);
                } else if (robotType.equals("ControlledRobot")) {
                    ControlledRobot robot = ControlledRobot.create(room, new Position(row, col));
                    room.addRobot(robot);
                }
            }
        }
        scanner.close();
        return room;
    }
}
