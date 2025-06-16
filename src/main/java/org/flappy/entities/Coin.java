package org.flappy.entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Coin {
    private final Image coinImage;
    private double x;
    private double y;
    private final double width = 40;
    private final double height = 40;

    public Coin(double startX, double startY) {
        this.coinImage = new Image(getClass().getResource("/images/coin/coin.png").toExternalForm());
        this.x = startX;
        this.y = startY;
    }

    public void update(double deltaX) {
        this.x += deltaX;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(coinImage, x, y, width, height);
    }

    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, width, height);
    }

    public double getX() {
        return x;
    }

    public double getWidth() {
        return width;
    }
}