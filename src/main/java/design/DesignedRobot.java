package main.java.design;

import main.java.robot.AutonomousRobot;
import main.java.robot.ControlledRobot;
import main.java.common.Robot;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class DesignedRobot {
    private final Robot model;

    public DesignedRobot(Robot model) {
        this.model = model;
    }

    public void paintComponent(Graphics g, int width, int height, boolean isActive) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Размер и положение робота
        double robotDiameter = Math.min(width, height) - 10.0;
        double x = (width - robotDiameter) / 2.0;
        double y = (height - robotDiameter) / 2.0;

        // Цвета для роботов
        Color fillColor = (model instanceof ControlledRobot) ? new Color(0, 204, 255) : (model instanceof AutonomousRobot) ? new Color(255, 153, 0) : Color.GRAY;

        // Рисуем робот в виде круга
        g2d.setColor(fillColor);
        Ellipse2D.Double robotShape = new Ellipse2D.Double(x, y, robotDiameter, robotDiameter);
        g2d.fill(robotShape);

        // Черная обводка, если робот активен
        if (isActive) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(robotShape);
        }

        // Рассчитываем положение глаза с учетом угла
        int angle = model.angle();
        double angleInRadians = Math.toRadians(angle - 90); // Корректируем начальное положение

        double centerX = x + robotDiameter / 2.0;
        double centerY = y + robotDiameter / 2.0;

        double eyeOffset = robotDiameter / 3.0;
        double eyeX = centerX + eyeOffset * Math.cos(angleInRadians);
        double eyeY = centerY + eyeOffset * Math.sin(angleInRadians);

        Ellipse2D.Double eye = new Ellipse2D.Double(eyeX - 3.0, eyeY - 3.0, 6.0, 6.0);
        g2d.setColor(Color.BLACK);
        g2d.fill(eye);
    }
}
