package main.java.view;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import main.java.common.Position;
import main.java.common.Environment;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class FieldView extends JPanel {
    private final Environment model;
    private final Position position;
    private ComponentView obj;
    private int changedModel = 0;

    public FieldView(Environment var1, Position var2) {
        this.model = var1;
        this.position = var2;
        this.privUpdate();
    }

    protected void paintComponent(Graphics var1) {
        super.paintComponent(var1);
        if (this.obj != null) {
            this.obj.paintComponent(var1);
        }

    }

    private void privUpdate() {
        if (this.model.obstacleAt(this.position)) {
            this.setBackground(Color.lightGray);
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

    }

    public void addComponent(ComponentView var1) {
        this.obj = var1;
    }

    public void removeComponent() {
        this.obj = null;
    }

    public ComponentView getComponent() {
        return this.obj;
    }

    public int numberUpdates() {
        return this.obj == null ? 0 : this.obj.numberUpdates();
    }

    public void clearChanged() {
        if (this.obj != null) {
            this.obj.clearChanged();
        }

    }
}
