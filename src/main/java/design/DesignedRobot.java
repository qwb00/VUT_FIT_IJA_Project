/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 * A graphical representation of a robot with specific visual details.
 */
package main.java.design;

import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;
import main.java.common.Robot;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class DesignedRobot {
    private final Robot model;

    /**
     * Constructs a DesignedRobot associated with a specific robot model.
     *
     * @param model The robot model to represent.
     */
    public DesignedRobot(Robot model) {
        this.model = model;
    }

    /**
     * Paints the robot on the screen.
     *
     * @param g The graphics context used to draw the robot.
     * @param width The width of the drawing area.
     * @param height The height of the drawing area.
     * @param isActive Whether the robot is currently active.
     */
    public void paintComponent(Graphics g, int width, int height, boolean isActive) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate robot size and position
        double robotDiameter = Math.min(width, height) - 10.0;
        double x = (width - robotDiameter) / 2.0;
        double y = (height - robotDiameter) / 2.0;

        // Determine robot fill color based on its type
        Color fillColor = (model instanceof ControlledRobot) ? new Color(0, 204, 255) : (model instanceof AutonomousRobot) ? new Color(255, 153, 0) : Color.GRAY;

        // Draw robot as a filled circle
        g2d.setColor(fillColor);
        Ellipse2D.Double robotShape = new Ellipse2D.Double(x, y, robotDiameter, robotDiameter);
        g2d.fill(robotShape);

        // Draw border if the robot is active
        if (isActive) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(robotShape);
        }

        // Calculate the position of the robot's "eye" based on its angle
        int angle = model.angle();
        double angleInRadians = Math.toRadians(angle - 90);

        double centerX = x + robotDiameter / 2.0;
        double centerY = y + robotDiameter / 2.0;

        double eyeOffset = robotDiameter / 3.0;
        double eyeX = centerX + eyeOffset * Math.cos(angleInRadians);
        double eyeY = centerY + eyeOffset * Math.sin(angleInRadians);

        // Draw the robot's eye
        Ellipse2D.Double eye = new Ellipse2D.Double(eyeX - 3.0, eyeY - 3.0, 6.0, 6.0);
        g2d.setColor(Color.BLACK);
        g2d.fill(eye);
    }
}
