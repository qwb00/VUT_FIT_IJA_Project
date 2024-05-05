/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xposte00 - Aleksander Postelga
 * Configuration class for saving and loading simulation configurations to/from files.
 */
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

    /**
     * Saves the environment configuration to a file.
     *
     * @param env      The environment to be saved.
     * @param filePath The file path to save the configuration.
     */
    public static void saveConfiguration(Environment env, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(env.toString());
            logger.info("Configuration saved to file: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to save configuration to file: {}", filePath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the environment configuration from a file.
     *
     * @param filePath The file path to load the configuration.
     * @return The environment created from the configuration.
     */
    public static Environment loadConfiguration(String filePath) {
        File file = new File(filePath);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            logger.error("Failed to load configuration from file: {}", filePath, e);
            throw new RuntimeException(e);
        }
        Room room = null;
        int numObstacles = 0;
        int numRobots = 0;
        boolean readingObstacles = false;
        boolean readingRobots = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("Room")) {
                room = parseRoom(scanner);
            } else if (line.startsWith("Obstacles=")) {
                numObstacles = Integer.parseInt(line.split("=")[1].trim());
                readingObstacles = true;
            } else if (line.startsWith("Robots=")) {
                numRobots = Integer.parseInt(line.split("=")[1].trim());
                readingRobots = true;
            } else if (line.startsWith("Obstacle Position") && readingObstacles && numObstacles > 0) {
                assert room != null;
                parseObstacle(room, scanner);
                numObstacles--;
                if (numObstacles == 0) {
                    readingObstacles = false;
                }
            } else if (line.contains("Robot") && readingRobots && numRobots > 0) {
                parseRobot(room, scanner, line);
                numRobots--;
                if (numRobots == 0) {
                    readingRobots = false;
                }
            }
        }
        scanner.close();

        if (numObstacles != 0 || numRobots != 0) {
            logger.error("Configuration mismatch: Not all obstacles or robots were processed");
            throw new RuntimeException("Configuration mismatch: Not all obstacles or robots were processed");
        }
        logger.info("Configuration loaded from file: {}", filePath);
        return room;
    }

    /**
     * Parses and creates a Room object from the configuration file.
     *
     * @param scanner The scanner to read from the file.
     * @return The Room object created.
     * @throws RuntimeException if an error occurs while reading the configuration.
     */
    private static Room parseRoom(Scanner scanner) throws RuntimeException {
        int rows = readInteger(scanner, "Rows");
        int cols = readInteger(scanner, "Cols");
        return Room.create(rows, cols);
    }

    /**
     * Parses and creates an obstacle in the given room from the configuration file.
     *
     * @param room    The room to add the obstacle to.
     * @param scanner The scanner to read from the file.
     * @throws RuntimeException if an error occurs while reading the configuration.
     */
    private static void parseObstacle(Room room, Scanner scanner) throws RuntimeException {
        int row = readInteger(scanner, "Row");
        int col = readInteger(scanner, "Col");
        room.createObstacleAt(row, col);
    }

    /**
     * Parses and creates a robot in the given room from the configuration file.
     *
     * @param room      The room to add the robot to.
     * @param scanner   The scanner to read from the file.
     * @param robotType The type of the robot to be created.
     * @throws RuntimeException if an error occurs while reading the configuration.
     */
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

    /**
     * Reads an integer value from the configuration file based on the specified field name.
     *
     * @param scanner   The scanner to read from the file.
     * @param fieldName The field name to read the value for.
     * @return The integer value read from the file.
     * @throws RuntimeException if an error occurs while reading the configuration.
     */
    private static int readInteger(Scanner scanner, String fieldName) throws RuntimeException {
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("=");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1].trim());
            } else {
                logger.error("Malformed line for {}: {}", fieldName, line);
                throw new RuntimeException("Malformed line for " + fieldName + ": " + line);
            }
        }
        logger.error("Missing line for {}", fieldName);
        throw new RuntimeException("Missing line for " + fieldName);
    }

}
