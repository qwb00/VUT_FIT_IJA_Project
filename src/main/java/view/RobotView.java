package main.java.view;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import main.java.EnvPresenter;
import main.java.common.Observable;
import main.java.common.Robot;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

public class RobotView implements ComponentView, Observable.Observer {
    private final Robot model;
    private final EnvPresenter parent;
    private FieldView current;
    private int changedModel = 0;

    public RobotView(EnvPresenter var1, Robot var2) {
        this.model = var2;
        this.parent = var1;
        var2.addObserver(this);
        this.privUpdate();
    }

    private void privUpdate() {
        FieldView var1 = this.parent.fieldAt(this.model.getPosition());
        if (this.current != null) {
            this.current.removeComponent();
            this.current.repaint();
        }

        this.current = var1;
        var1.addComponent(this);
        this.current.repaint();
    }

    public final void update(Observable var1) {
        ++this.changedModel;
        this.privUpdate();
    }

    public void paintComponent(Graphics var1) {
        Graphics2D var2 = (Graphics2D)var1;
        Rectangle var3 = this.current.getBounds();
        double var4 = var3.getWidth();
        double var6 = var3.getHeight();
        Math.max(var6, var4);
        double var10 = Math.min(var6, var4) - 10.0;
        double var12 = (var4 - var10) / 2.0;
        double var14 = (var6 - var10) / 2.0;
        Ellipse2D.Double var16 = new Ellipse2D.Double(var12, var14, var10, var10);
        var2.setColor(Color.cyan);
        var2.fill(var16);
        double var17 = var12 + var10 / 2.0;
        double var19 = var14 + var10 / 2.0;
        int var21 = this.model.angle();
        double var22 = 0.0;
        double var24 = 0.0;
        double var26 = var10 / 4.0;
        switch (var21) {
            case 0:
                var22 = var17;
                var24 = var14;
                break;
            case 45:
                var22 = var17 + var26;
                var24 = var14 + var26;
                break;
            case 90:
                var22 = var17 + var26 * 2.0;
                var24 = var14 + var26 * 2.0;
                break;
            case 135:
                var22 = var17 + var26;
                var24 = var14 + var26 * 3.0;
                break;
            case 180:
                var22 = var17;
                var24 = var19 + var26 * 2.0;
                break;
            case 225:
                var22 = var17 - var26;
                var24 = var19 + var26;
                break;
            case 270:
                var22 = var12;
                var24 = var19;
                break;
            case 315:
                var22 = var17 - var26;
                var24 = var19 - var26;
        }

        Ellipse2D.Double var28 = new Ellipse2D.Double(var22 - 3.0, var24 - 3.0, 6.0, 6.0);
        var2.setColor(Color.black);
        var2.fill(var28);
    }

    public int numberUpdates() {
        return this.changedModel;
    }

    public void clearChanged() {
        this.changedModel = 0;
    }

    public Robot getModel() {
        return this.model;
    }
}

