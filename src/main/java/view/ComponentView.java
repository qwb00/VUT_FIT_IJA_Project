/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 * Author: xpetri23 - Aleksei Petrishko
 */
package main.java.view;

import main.java.common.Robot;
import java.awt.Graphics;

/**
 * The ComponentView interface defines a graphical component that can be painted
 * and associated with a robot model in the 2D robot simulator.
 */
public interface ComponentView {
    /**
     * Paints the component
     *
     * @param var1 The graphics object to paint
     */
    void paintComponent(Graphics var1);

    /**
     * Returns the model of the component
     *
     * @return The model of the component
     */
    Robot getModel();
}