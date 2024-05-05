/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 * A graphical view representation of a robot.
 * This class observes changes in the robot's state and updates the GUI accordingly.
 */
package main.java.view;

import main.java.EnvPresenter;
import main.java.common.Observable;
import main.java.common.Robot;
import main.java.design.DesignedRobot;

import java.awt.*;

public class RobotView implements ComponentView, Observable.Observer {
    private final Robot model;
    private final EnvPresenter parent;
    private FieldView current;
    private final DesignedRobot designedRobot;

    /**
     * Constructs a RobotView for the given robot and environment presenter.
     * Adds this view as an observer of the robot to track state changes.
     *
     * @param var1 The environment presenter to which this view belongs.
     * @param var2 The robot model being represented.
     */
    public RobotView(EnvPresenter var1, Robot var2) {
        this.model = var2;
        this.parent = var1;
        var2.addObserver(this);
        this.privUpdate();

        this.designedRobot = new DesignedRobot(model);
    }

    /**
     * Updates the graphical view of the robot based on its current position.
     */
    private void privUpdate() {
        FieldView field = this.parent.fieldAt(this.model.getPosition());
        if (this.current != null) {
            this.current.removeComponent();
            this.current.repaint();
        }

        this.current = field;
        if (field != null) {
            field.addComponent(this);
            field.repaint();
        }
    }

    /**
     * Updates the view in response to changes in the observed robot's state.
     *
     * @param var1 The observable object notifying of the change.
     */
    public final void update(Observable var1) {
        this.privUpdate();
    }

    /**
     * Paints the graphical representation of the robot onto the given Graphics context.
     *
     * @param g The Graphics context to draw on.
     */
    public void paintComponent(Graphics g) {
        if (this.current != null) {
            Rectangle bounds = this.current.getBounds();
            this.designedRobot.paintComponent(g, bounds.width, bounds.height, this.parent.isActive(this.model));
        }
    }

    /**
     * Returns the robot model being represented by this view.
     *
     * @return The robot model.
     */
    public Robot getModel() {
        return this.model;
    }

    /**
     * Refreshes the view by updating it based on the robot's current state.
     */
    public void refreshView() {
        this.privUpdate();
    }
}

