package org.flappy.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.flappy.app.Game;
import org.flappy.entities.Pipe;

public class BackgroundRenderer {
    private final GraphicsContext gc;

    private Image backgroundDay;
    private Image backgroundNight;
    private Image currentBackground;

    private double bgX1 = 0;
    private double bgX2 = Game.WIDTH;
    private double bgSpeed;

    public BackgroundRenderer(GraphicsContext gc, Image backgroundDay, Image backgroundNight, boolean isNight, String difficulty) {
        this.gc = gc;
        this.backgroundDay = backgroundDay;
        this.backgroundNight = backgroundNight;
        this.currentBackground = isNight ? backgroundNight : backgroundDay;

        switch (difficulty.toLowerCase()) {
            case "easy" -> {
                this.bgSpeed = 1.5;
            }
            case "medium" -> this.bgSpeed = 1.5;
            case "hard" -> {
                this.bgSpeed = 3;
            }
        }

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