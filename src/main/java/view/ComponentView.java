package main.java.view;

import main.java.common.Robot;
import java.awt.Graphics;

public interface ComponentView {
    void paintComponent(Graphics var1);

    int numberUpdates();

    Robot getModel();

    void clearChanged();
}