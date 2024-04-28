package main.java.view;

import main.java.EnvPresenter;
import main.java.common.Position;
import main.java.common.Environment;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FieldView extends JPanel {
    private final Environment model;
    private final Position position;
    private EnvPresenter presenter;
    private ComponentView obj;
    private int changedModel = 0;

    public FieldView(Environment env, Position pos, EnvPresenter presenter) {
        this.model = env;
        this.position = pos;
        this.presenter = presenter;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick();
            }
        });
    }

    private void handleMouseClick() {
        if (presenter.isObstacleMode()) {
            if (model.obstacleAt(position)) {
                model.removeObstacleAt(position.getRow(), position.getCol());
                //System.out.println("Removed obstacle at " + position.getRow() + " / " + position.getCol());
            } else {
                model.createObstacleAt(position.getRow(), position.getCol());
                //System.out.println("Created obstacle at " + position.getRow() + " / " + position.getCol());
            }
            privUpdate();
            repaint();
        } else {
            // Активировать робота, если кликнули по клетке, где он находится
            if (model.robotAt(position)) {
                presenter.setActiveRobotByPosition(position);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE); // Установим белый фон для клеток без препятствий
        setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Граница для клеток

        // Проверяем наличие препятствия
        if (model.obstacleAt(position)) {
            g.setColor(Color.DARK_GRAY); // Цвет препятствий
            g.fillRect(0, 0, getWidth(), getHeight()); // Заполняем всю площадь панели
        }

        // Если в клетке есть компонент (робот), вызываем его paintComponent
        if (obj != null) {
            obj.paintComponent(g);
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
