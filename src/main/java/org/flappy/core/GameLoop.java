package org.flappy.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.flappy.entity.Bird;

public class GameLoop extends AnimationTimer {
    private final GraphicsContext gc;
    private final Bird bird;

    public GameLoop(GraphicsContext gc) {
        this.gc = gc;
        this.bird = new Bird(100, 300);
    }

    public Bird getBird() {
        return bird;
    }

    @Override
    public void handle(long now) {
        bird.update();
        render();
    }

    private void render() {
        gc.setFill(Color.SKYBLUE);
        gc.fillRect(0, 0, 400, 600);

        bird.render(gc);
    }
}