package org.flappy.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bird {
    private final double x;
    private final double baseY;
    private double y;
    private double velocity = 0;
    private final double gravity = 0.4;
    private final double jumpStrength = -7;

    private final Image upFlap;
    private final Image midFlap;
    private final Image downFlap;

    private boolean isActive = false;
    private double offset = 0;

    public Bird(double x, double y, String skinName) {
        this.x = x;
        this.y = y;
        this.baseY = y;
        this.upFlap = new Image(getClass().getResource(
                "/images/birds/" + skinName + "/" + skinName + "bird-upflap.png").toExternalForm());

        this.midFlap = new Image(getClass().getResource(
                "/images/birds/" + skinName + "/" + skinName + "bird-midflap.png").toExternalForm());

        this.downFlap = new Image(getClass().getResource(
                "/images/birds/" + skinName + "/" + skinName + "bird-downflap.png").toExternalForm());
    }

    public void update() {
        if (!isActive) return;
        velocity += gravity;
        y += velocity;
    }

    public void jump() {
        if (!isActive) return;
        velocity = jumpStrength;
    }

    public void activate() {
        isActive = true;
        y = baseY;
        velocity = 0;
    }

    public void setOffset(double offset) {
        if (!isActive) {
            this.offset = offset;
        }
    }

    public void render(GraphicsContext gc) {
        double drawY = isActive ? y : baseY + offset;
        Image current;

        if (!isActive) {
            current = midFlap;
        } else if (velocity < -2) {
            current = upFlap;
        } else if (velocity > 2) {
            current = downFlap;
        } else {
            current = midFlap;
        }

        gc.save();
        gc.translate(x + 15, drawY + 15);
        double angle = Math.max(-30, Math.min(velocity * 3, 20));
        gc.rotate(angle);
        gc.drawImage(current, -15, -15, 30, 30);
        gc.restore();
    }
}