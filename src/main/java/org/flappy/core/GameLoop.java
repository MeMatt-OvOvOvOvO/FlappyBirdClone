package org.flappy.core;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.flappy.app.Game;
import org.flappy.entity.Bird;

public class GameLoop extends AnimationTimer {
    private final GraphicsContext gc;
    private final Bird bird;
    private boolean started = false;
    private boolean gameOver = false;
    private double floatOffset = 0;
    private boolean goingUp = true;

    private final Image background;
    private final Image gameOverImage;
    private double bgX1 = 0;
    private double bgX2;
    private final double bgSpeed = 1;

    public GameLoop(GraphicsContext gc, String skinName) {
        this.gc = gc;
        this.bird = new Bird(100, 300, skinName);

        this.background = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
        this.gameOverImage = new Image(getClass().getResource("/images/basics/game-over.png").toExternalForm());

        this.bgX1 = 0;
        this.bgX2 = Game.WIDTH;
    }

    public Bird getBird() {
        return bird;
    }

    @Override
    public void handle(long now) {
        if (started && !gameOver) {
            bird.update();
            checkCollisions();
        } else if (!started) {
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
    }

    private void render() {
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
        renderBackground();

        if (gameOver) {
            double centerX = (Game.WIDTH - gameOverImage.getWidth()) / 2;
            double centerY = (Game.HEIGHT - gameOverImage.getHeight()) / 2;
            gc.drawImage(gameOverImage, centerX, centerY);
        } else {
            bird.render(gc);
        }
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (!gameOver) {
            gameOver = true;

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> returnToStartScreen());
            pause.play();
        }
    }

    public void activateBird() {
        started = true;
        bird.activate();
        bird.setOffset(0);
    }

    public boolean isStarted() {
        return started;
    }

    private void returnToStartScreen() {
        gameOver = false;
        Game.goToStartScreen();
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

    private void checkCollisions() {
        if (bird.getHitbox().getMinY() <= 0 || bird.getHitbox().getMaxY() >= Game.HEIGHT) {
            stop();
        }
    }
}