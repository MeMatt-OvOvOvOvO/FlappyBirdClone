package org.flappy.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.flappy.app.Game;
import org.flappy.config.GameConfig;

public class GroundRenderer {
    private final GraphicsContext gc;
    private final Image groundImage;

    private double groundX1 = 0;
    private double groundX2 = Game.WIDTH;
    private final double groundSpeed;

    private final double groundHeightOffset;

    public GroundRenderer(GraphicsContext gc, Image groundImage) {
        this.gc = gc;
        this.groundImage = groundImage;
        this.groundSpeed = GameConfig.GROUND_SPEED;

        this.groundHeightOffset = Game.HEIGHT - groundImage.getHeight();
    }

    public void update() {
        groundX1 -= groundSpeed;
        groundX2 -= groundSpeed;

        if (groundX1 + Game.WIDTH <= 0) {
            groundX1 = groundX2 + Game.WIDTH;
        }
        if (groundX2 + Game.WIDTH <= 0) {
            groundX2 = groundX1 + Game.WIDTH;
        }
    }

    public void render() {
        gc.drawImage(groundImage, groundX1, groundHeightOffset, Game.WIDTH, groundImage.getHeight());
        gc.drawImage(groundImage, groundX2, groundHeightOffset, Game.WIDTH, groundImage.getHeight());
    }

    public double getHeight() {
        return groundImage.getHeight();
    }
}