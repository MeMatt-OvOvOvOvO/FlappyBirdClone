package org.flappy.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bird {
    private double x, y;
    private double velocity = 0;
    private final double gravity = 0.4;
    private final double jumpStrength = -7;

    public Bird(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        velocity += gravity;
        y += velocity;
    }

    public void jump() {
        velocity = jumpStrength;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillOval(x, y, 30, 30);
    }

    public double getY() {
        return y;
    }
}
