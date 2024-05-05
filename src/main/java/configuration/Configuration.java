package main.java.configuration;

import main.java.common.Environment;
import main.java.common.Position;
import main.java.environment.Room;
import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Scanner;

public class Configuration {
    private static final Logger logger = LogManager.getLogger(Configuration.class);

    public static void saveConfiguration(Environment env, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(env.toString());
            logger.info("Configuration saved to file: " + filePath);
        } catch (IOException e) {
            logger.error("Failed to save configuration to file: " + filePath, e);
            throw new RuntimeException(e);
        }
    }

    public static Environment loadConfiguration(String filePath) {
        File file = new File(filePath);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            logger.error("Failed to load configuration from file: " + filePath, e);
            throw new RuntimeException(e);
        }
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
                assert room != null;
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
            logger.error("Configuration mismatch: Not all obstacles or robots were processed");
            throw new RuntimeException("Configuration mismatch: Not all obstacles or robots were processed");
        }
        logger.info("Configuration loaded from file: " + filePath);
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
            int speed = readInteger(scanner, "speed");
            AutonomousRobot.create(room, new Position(row, col), speed, detectionRange, turnAngle, turnDirection, 0);
        } else if (robotType.contains("ControlledRobot")) {
            int speed = readInteger(scanner, "speed");
            ControlledRobot.create(room, new Position(row, col), speed, 0);
        }
    }

    private static int readInteger(Scanner scanner, String fieldName) throws RuntimeException {
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("=");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1].trim());
            } else {
                logger.error("Malformed line for " + fieldName + ": " + line);
                throw new RuntimeException("Malformed line for " + fieldName + ": " + line);
            }
        }
        logger.error("Missing line for " + fieldName);
        throw new RuntimeException("Missing line for " + fieldName);
    }

}
