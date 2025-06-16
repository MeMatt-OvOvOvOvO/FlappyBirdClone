package org.flappy.entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bird {
    private final BirdAnimator animator;

    private double x;
    private double y;
    private final double width = 34;
    private final double height = 24;
    private final double gravity = 0.5;
    private double velocity = 0;
    private boolean idle = true;

    public Bird(double x, double y, String skinName) {
        this.x = x;
        this.y = y;
        this.animator = new BirdAnimator(skinName);
    }

    public void update(double deltaTime) {
        if (!idle) {
            velocity += gravity;
            y += velocity;
            animator.animate(deltaTime);
        } else {
            animator.animateIdleBird(deltaTime);
        }
    }

    public void render(GraphicsContext gc) {
        double offsetY = idle ? animator.getFloatOffset() : 0;
        System.out.println(velocity);
        double angle = Math.max(-30, Math.min(velocity * 3, 20));

        gc.save();

        gc.translate(x + width / 2, y + height / 2 + offsetY);
        gc.rotate(angle);

        Image currentFrame = animator.getCurrentFrame();
        gc.drawImage(currentFrame, -width / 2, -height / 2, width, height);

        gc.restore();
    }

    public void jump() {
        velocity = -8;
        idle = false;
    }

    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, width, height);
    }

    public boolean isIdle() {
        return idle;
    }

    public double getX() {
        return x;
    }
}