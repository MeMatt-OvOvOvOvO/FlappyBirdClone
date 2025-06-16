package org.flappy.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.flappy.app.Game;

public class BackgroundRenderer {
    private final GraphicsContext gc;

    private Image backgroundDay;
    private Image backgroundNight;
    private Image currentBackground;

    private double bgX1 = 0;
    private double bgX2 = Game.WIDTH;
    private double bgSpeed;

    public BackgroundRenderer(GraphicsContext gc, Image backgroundDay, Image backgroundNight, boolean isNight, double bgSpeed) {
        this.gc = gc;
        this.backgroundDay = backgroundDay;
        this.backgroundNight = backgroundNight;
        this.bgSpeed = bgSpeed;
        this.currentBackground = isNight ? backgroundNight : backgroundDay;
    }

    public void update() {
        bgX1 -= bgSpeed;
        bgX2 -= bgSpeed;

        if (bgX1 + Game.WIDTH <= 0) {
            bgX1 = bgX2 + Game.WIDTH;
        }
        if (bgX2 + Game.WIDTH <= 0) {
            bgX2 = bgX1 + Game.WIDTH;
        }
    }

    public void render() {
        gc.drawImage(currentBackground, bgX1, 0, Game.WIDTH, Game.HEIGHT);
        gc.drawImage(currentBackground, bgX2, 0, Game.WIDTH, Game.HEIGHT);
    }

    public void switchBackground(boolean isNight) {
        currentBackground = isNight ? backgroundNight : backgroundDay;
    }
}