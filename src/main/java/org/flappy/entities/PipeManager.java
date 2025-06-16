package org.flappy.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PipeManager {
    private final List<Pipe> pipes = new ArrayList<>();
    private final Image pipeGreen;
    private final Image pipeRed;
    private final double pipeGap;
    private final double screenWidth;
    private final double screenHeight;
    private boolean isNight = false;

    private final double pipeSpawnDelay;
    private double timeSinceLastSpawn = 0;

    public PipeManager(Image pipeGreen, Image pipeRed, double pipeGap, double screenWidth, double screenHeight, double pipeSpawnDelay) {
        this.pipeGreen = pipeGreen;
        this.pipeRed = pipeRed;
        this.pipeGap = pipeGap;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.pipeSpawnDelay = pipeSpawnDelay;
    }

    public void update(double delta, long now) {
        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.update(-Pipe.PIPE_SPEED);
            if (pipe.getX() + pipe.getWidth() < 0) {
                iterator.remove();
            }
        }

        timeSinceLastSpawn += delta;
        if (timeSinceLastSpawn >= pipeSpawnDelay) {
            spawnPipe();
            timeSinceLastSpawn = 0;
        }
    }

    public void render(GraphicsContext gc) {
        for (Pipe pipe : pipes) {
            pipe.render(gc);
        }
    }

    public boolean checkCollision(Bird bird) {
        for (Pipe pipe : pipes) {
            if (bird.getHitbox().intersects(pipe.getTopHitbox()) || bird.getHitbox().intersects(pipe.getBottomHitbox())) {
                return true;
            }
        }
        return false;
    }

    public int handleScoring(Bird bird) {
        int scoreIncrement = 0;
        for (Pipe pipe : pipes) {
            if (!pipe.isScored() && bird.getX() > pipe.getX() + pipe.getWidth()) {
                pipe.setScored(true);
                scoreIncrement++;
            }
        }
        return scoreIncrement;
    }

    private void spawnPipe() {
        pipes.add(new Pipe(screenWidth, pipeGap, screenHeight, isNight ? pipeRed : pipeGreen));
    }

    public void switchImage(boolean isNight) {
        for (Pipe pipe : pipes) {
            this.isNight = isNight;
            pipe.setPipeImage(isNight ? pipeRed : pipeGreen);
        }
    }

    public void switchMoving(boolean isMoving) {
        for (Pipe pipe : pipes) {
            pipe.setMoving(isMoving);
        }
    }

    public void reset() {
        pipes.clear();
    }
}