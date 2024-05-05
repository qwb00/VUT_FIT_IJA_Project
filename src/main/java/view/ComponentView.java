package main.java.view;

import main.java.common.Robot;
import java.awt.Graphics;

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