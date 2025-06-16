package org.flappy.core;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.flappy.app.Game;
import org.flappy.database.DatabaseManager;
import org.flappy.entities.Bird;
import org.flappy.entities.Pipe;
import org.flappy.graphics.BackgroundRenderer;
import org.flappy.graphics.GraphicsUtils;
import org.flappy.graphics.GroundRenderer;
import org.flappy.graphics.ScoreRenderer;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {
    private final GraphicsContext gc;
    private final Bird bird;
    private final ScoreRenderer scoreRenderer;
    private final BackgroundRenderer backgroundRenderer;
    private final GroundRenderer groundRenderer;
    private boolean started = false;
    private boolean gameOver = false;
    private double floatOffset = 0;
    private boolean goingUp = true;
    private int score = 0;
    private String difficulty;

    private double bgSpeed = 1;

    private final List<Pipe> pipes = new ArrayList<>();
    private static final double PIPE_GAP = 240;
    private double pipeSpawnDelay = 2000;
    private double timeSinceLastSpawn = 0;
    private long previousSpawnTime = 0;

    private Image pipeGreen;
    private Image pipeRed;
    private Image currentPipeImage;

    private TextField nameField;
    private Button saveButton;
    private StackPane gameRoot;

    public GameLoop(GraphicsContext gc, String skinName, String difficulty, StackPane gameRoot) {
        this.gc = gc;
        this.bird = new Bird(100, 300, skinName);
        this.difficulty = difficulty;
        this.gameRoot = gameRoot;
        this.scoreRenderer = new ScoreRenderer(gc);

        switch (difficulty.toLowerCase()) {
            case "easy" -> {
                bgSpeed = 1.5;
                Pipe.PIPE_SPEED = 1;
                pipeSpawnDelay = 5000;
            }
            case "medium" -> {
                bgSpeed = 1.5;
                Pipe.PIPE_SPEED = 2;
                pipeSpawnDelay = 2000;
            }
            case "hard" -> {
                bgSpeed = 3;
                Pipe.PIPE_SPEED = 4;
                pipeSpawnDelay = 1200;
            }
        }

        this.backgroundRenderer = new BackgroundRenderer(gc, new Image(getClass().getResource("/images/background/background-day.png").toExternalForm()), new Image(getClass().getResource("/images/background/background-night.png").toExternalForm()), false, bgSpeed);
        this.groundRenderer = new GroundRenderer(gc, new Image(getClass().getResource("/images/ground/ground.png").toExternalForm()), 1);

        this.pipeGreen = new Image(getClass().getResource("/images/pipes/pipe-green.png").toExternalForm());
        this.pipeRed = new Image(getClass().getResource("/images/pipes/pipe-red.png").toExternalForm());
        this.currentPipeImage = pipeGreen;
    }

    public Bird getBird() {
        return bird;
    }

    @Override
    public void handle(long now) {
        if (!started) {
            animateIdleBird();
            render();
            return;
        }

        if (gameOver) {
            return;
        }

        double deltaTime = (now - previousSpawnTime) / 1e9;
        previousSpawnTime = now;

        bird.update();
        updatePipes(deltaTime);

        checkCollisions();
        backgroundRenderer.update();
        groundRenderer.update();
        render();
    }

    private void updatePipes(double delta) {
        timeSinceLastSpawn += delta;

        if (timeSinceLastSpawn > pipeSpawnDelay / 1000.0) {
            spawnPipe();
            timeSinceLastSpawn = 0;
        }

        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.update(-Pipe.PIPE_SPEED);

            if (!pipe.isScored() && bird.getHitbox().getMinX() > pipe.getX() + pipe.getWidth()) {
                score++;
                pipe.setScored(true);
                updateVisualStyle();

                if (score >= 5) {
                    for (Pipe p : pipes) {
                        p.setMoving(true);
                    }
                }
            }


            if (pipe.getX() + pipe.getWidth() < 0) {
                iterator.remove();
            }
        }
    }

    private void spawnPipe() {

        Pipe pipe = new Pipe(Game.WIDTH, PIPE_GAP, Game.HEIGHT, currentPipeImage);
        if (score >= 5) {
            pipe.setMoving(true);
        }
        pipes.add(pipe);
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
        backgroundRenderer.render();
        bird.render(gc);

        for (Pipe pipe : pipes) {
            pipe.render(gc);
        }

        groundRenderer.render();

        if (started && !gameOver) {
            scoreRenderer.renderScore(score, Game.WIDTH / 2, 20, 1.0); // Replace posX, posY, and scale with relevant values
        }

        if (!started) {
            Image getReadyImage = new Image(getClass().getResource("/images/basics/get-ready.png").toExternalForm());
            GraphicsUtils.drawImageCentered(gc, getReadyImage, 2.5, 2.5, 0.33, Game.WIDTH, Game.HEIGHT);
        }

        if (gameOver) {
            Image gameOverImage = new Image(getClass().getResource("/images/basics/game-over.png").toExternalForm());
            GraphicsUtils.drawImageCentered(gc, gameOverImage, 2.5, 2.5, 0.33, Game.WIDTH, Game.HEIGHT);

            double gameOverImageHeight = gameOverImage.getHeight() * 2.5;
            double scorePosY = (Game.HEIGHT / 2) + (gameOverImageHeight / 2) + 20;
            scoreRenderer.renderScore(score,Game.WIDTH / 2, scorePosY, 2.0);
        }
    }

    private void updateVisualStyle() {
        if ((score / 10) % 2 == 0) {
            backgroundRenderer.switchBackground(false);
            currentPipeImage = pipeGreen;
        } else {
            backgroundRenderer.switchBackground(true);
            currentPipeImage = pipeRed;
        }

        for (Pipe pipe : pipes) {
            pipe.setPipeImage(currentPipeImage);
        }
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        gameOver = true;

        nameField = new TextField();
        nameField.setPromptText("Enter your name");

        nameField.setMaxWidth(200);
        nameField.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #5d4037;
            -fx-border-width: 2px;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-padding: 5 10 5 10;
            -fx-font-size: 14px;
            -fx-font-family: "Arial";
        """);

        saveButton = new Button("Save Score");
        saveButton.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #ffffff, yellow);
            -fx-border-color: #5d4037;
            -fx-border-width: 2px;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-padding: 5 10 5 10;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-font-family: "Arial";
        """);

        saveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                DatabaseManager.saveScore(name, score, difficulty);
                gameRoot.getChildren().removeAll(nameField, saveButton);
                Game.goToStartScreen();
            }
        });

        StackPane.setAlignment(nameField, Pos.CENTER);
        StackPane.setAlignment(saveButton, Pos.CENTER);
        StackPane.setMargin(saveButton, new Insets(100, 0, 0, 0));

        gameRoot.getChildren().addAll(nameField, saveButton);
    }

    public void activateBird() {
        started = true;
        bird.activate();
        pipes.clear();
        score = 0;
    }

    public boolean isStarted() {
        return started;
    }

    private void checkCollisions() {
        // Pipe checks
        for (Pipe pipe : pipes) {
            if (pipe.getTopHitbox().intersects(bird.getHitbox()) ||
                    pipe.getBottomHitbox().intersects(bird.getHitbox())) {
                stop();
                return;
            }
        }

        // Border checks
        if (bird.getHitbox().getMinY() <= 0 || bird.getHitbox().getMaxY() >= Game.HEIGHT - ground.getHeight()) {
            stop();
        }

    }
}