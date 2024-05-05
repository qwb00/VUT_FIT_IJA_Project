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
    private int changedModel = 0;
    private final DesignedRobot designedRobot;


    public RobotView(EnvPresenter var1, Robot var2) {
        this.model = var2;
        this.parent = var1;
        var2.addObserver(this);
        this.privUpdate();
        this.designedRobot = new DesignedRobot(model);
    }


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

    public final void update(Observable var1) {
        ++this.changedModel;
        this.privUpdate();
    }

    public void paintComponent(Graphics g) {
        if (this.current != null) {
            Rectangle bounds = this.current.getBounds();
            this.designedRobot.paintComponent(g, bounds.width, bounds.height, this.parent.isActive(this.model)); // Используем новый дизайн
        }
    }

    public Robot getModel() {
        return this.model;
    }

    public void refreshView() {
        // Обновление вида робота
        this.privUpdate();
    }
}

