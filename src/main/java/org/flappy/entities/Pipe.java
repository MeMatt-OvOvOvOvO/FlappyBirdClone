package org.flappy.entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.Random;

public class Pipe {
    private Image pipeImage;
    private double x;
    private final double topY;
    private final double gap;
    private final double width = 52;
    private boolean isScored = false;
    private boolean moving = false;
    private double verticalOffset = 0;
    private boolean goingUp = true;
    private final double maxOffset = 80;


    public static double PIPE_SPEED = 2.0;

    public Pipe(double x, double gap, double screenHeight, Image pipeImage) {
        this.pipeImage = pipeImage;
        this.x = x;
        this.gap = gap;

        Random random = new Random();
        double maxTopY = screenHeight - gap - 170;
        this.topY = 50 + random.nextDouble() * (maxTopY - 50);
    }

    public void setPipeImage(Image pipeImage) {
        this.pipeImage = pipeImage;
    }

    public void update(double deltaX) {

        this.x += deltaX;
        if (moving) {
            if (goingUp) {
                verticalOffset -= PIPE_SPEED;
                if (verticalOffset < -maxOffset) {
                    goingUp = false;
                }
            } else {
                verticalOffset += PIPE_SPEED;
                if (verticalOffset > maxOffset) {
                    goingUp = true;
                }
            }
        }
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.scale(1, -1);
        gc.drawImage(pipeImage, x, -(topY + verticalOffset));
        gc.restore();

        gc.drawImage(pipeImage, x, topY + gap + verticalOffset);
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isMoving() {
        return moving;
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

//    public Rectangle2D getTopHitbox() {
//        return new Rectangle2D(x, topY - pipeImage.getHeight(), width, pipeImage.getHeight());
//    }
//
//    public Rectangle2D getBottomHitbox() {
//        return new Rectangle2D(x, topY + gap, width, pipeImage.getHeight());
//    }

    public Rectangle2D getTopHitbox() {
        double height = pipeImage.getHeight();
        return new Rectangle2D(x, topY + verticalOffset - height, width, height);
    }

    public Rectangle2D getBottomHitbox() {
        return new Rectangle2D(x, topY + gap + verticalOffset, width, pipeImage.getHeight());
    }

}