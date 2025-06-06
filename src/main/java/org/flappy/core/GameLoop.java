package org.flappy.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import org.flappy.app.Game;
import org.flappy.entity.Bird;

import javafx.scene.image.Image;

public class GameLoop extends AnimationTimer {
    private final GraphicsContext gc;
    private final Bird bird;
    private boolean started = false;
    private double floatOffset = 0;
    private boolean goingUp = true;

    private Image background;
    private double bgX1 = 0;
    private double bgX2;
    private final double bgSpeed = 1;

    public GameLoop(GraphicsContext gc, String skinName) {
        this.gc = gc;
        this.bird = new Bird(100, 300, skinName);

        this.background = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
        this.bgX1 = 0;
        this.bgX2 = Game.WIDTH;
    }

    public Bird getBird() {
        return bird;
    }

    @Override
    public void handle(long now) {
        if (started) {
            bird.update();
        } else {
            animateIdleBird();
        }
        render();
    }

    private void animateIdleBird() {
        if (goingUp) {
            floatOffset -= 0.5;
            if (floatOffset < -10) goingUp = false;
        } else {
            floatOffset += 0.5;
            if (floatOffset > 10) goingUp = true;
        }
        bird.setOffset(floatOffset);
        System.out.println("Offset: " + floatOffset);
    }

    private void render() {
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
        renderBackground();
        bird.render(gc);
    }

    @Override
    public void start() {
        super.start();
    }

    public void activateBird() {
        started = true;
        bird.activate();
        bird.setOffset(0);
    }

    public boolean isStarted() {
        return started;
    }

    private void renderBackground() {
        double width = Game.WIDTH;
        double height = Game.HEIGHT;

        gc.drawImage(background, bgX1, 0, width, height);
        gc.drawImage(background, bgX2, 0, width, height);

        bgX1 -= bgSpeed;
        bgX2 -= bgSpeed;

        if (bgX1 + width <= 0) {
            bgX1 = bgX2 + width;
        }

        if (bgX2 + width <= 0) {
            bgX2 = bgX1 + width;
        }
    }
}