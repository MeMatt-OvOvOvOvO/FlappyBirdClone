package org.flappy.entity;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.Random;

public class Pipe {
    private final Image pipeImage;
    private double x;
    private final double topY;
    private final double gap;
    private final double width = 52;
    private boolean isScored = false;

    public static final double PIPE_SPEED = 2.0;

    public Pipe(double x, double gap, double screenHeight) {
        this.pipeImage = new Image(getClass().getResource("/images/pipes/pipe-green.png").toExternalForm());
        this.x = x;
        this.gap = gap;

        Random random = new Random();
        double maxTopY = screenHeight - gap - 100;
        this.topY = 50 + random.nextDouble() * (maxTopY - 50);
    }

    public void update(double deltaX) {
        this.x += deltaX;
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.scale(1, -1);
        gc.drawImage(pipeImage, x, -(topY));
        gc.restore();

        gc.drawImage(pipeImage, x, topY + gap);
    }

    public double getX() {
        return x;
    }

    public double getWidth() {
        return width;
    }

    public boolean isScored() {
        return isScored;
    }

    public void setScored(boolean scored) {
        isScored = scored;
    }

    public Rectangle2D getTopHitbox() {
        return new Rectangle2D(x, topY - pipeImage.getHeight(), width, pipeImage.getHeight());
    }

    public Rectangle2D getBottomHitbox() {
        return new Rectangle2D(x, topY + gap, width, pipeImage.getHeight());
    }
}