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
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        Room room = null;
        int numObstacles = 0;
        int numRobots = 0;
        boolean readingObstacles = false;  // To track when we are reading obstacle details
        boolean readingRobots = false;     // To track when we are reading robot details

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;  // Skip empty lines

            if (line.startsWith("Room")) {
                room = parseRoom(scanner);
            } else if (line.startsWith("Obstacles=")) {
                numObstacles = Integer.parseInt(line.split("=")[1].trim());
                readingObstacles = true;  // Start reading obstacle positions
            } else if (line.startsWith("Robots=")) {
                numRobots = Integer.parseInt(line.split("=")[1].trim());
                readingRobots = true;    // Start reading robot details
            } else if (line.startsWith("Obstacle Position") && readingObstacles && numObstacles > 0) {
                parseObstacle(room, scanner);
                numObstacles--;
                if (numObstacles == 0) {
                    readingObstacles = false;  // Stop reading obstacle positions
                }
            } else if (line.contains("Robot") && readingRobots && numRobots > 0) {
                parseRobot(room, scanner, line);
                numRobots--;
                if (numRobots == 0) {
                    readingRobots = false;    // Stop reading robot details
                }
            }
        }
        scanner.close();

        if (numObstacles != 0 || numRobots != 0) {
            throw new RuntimeException("Configuration mismatch: Not all obstacles or robots were processed");
        }

        return room;
    }

    private static Room parseRoom(Scanner scanner) throws RuntimeException {
        int rows = readInteger(scanner, "Rows");
        int cols = readInteger(scanner, "Cols");
        return Room.create(rows, cols);
    }

    private static void parseObstacle(Room room, Scanner scanner) throws RuntimeException {
        int row = readInteger(scanner, "Row");
        int col = readInteger(scanner, "Col");
        room.createObstacleAt(row, col);
    }

    private static void parseRobot(Room room, Scanner scanner, String robotType) throws RuntimeException {
        int row = readInteger(scanner, "positionRow");
        int col = readInteger(scanner, "positionCol");

        if (robotType.contains("AutonomousRobot")) {
            int detectionRange = readInteger(scanner, "detectionRange");
            int turnAngle = readInteger(scanner, "turnAngle");
            boolean turnDirection = Boolean.parseBoolean(scanner.nextLine().split("=")[1].trim());
            AutonomousRobot.create(room, new Position(row, col), detectionRange, turnAngle, turnDirection);
        } else if (robotType.contains("ControlledRobot")) {
            ControlledRobot.create(room, new Position(row, col));
        }
    }

    private static int readInteger(Scanner scanner, String fieldName) throws RuntimeException {
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("=");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1].trim());
            } else {
                throw new RuntimeException("Malformed line for " + fieldName + ": " + line);
            }
        }
        throw new RuntimeException("Missing line for " + fieldName);
    }

}
