package org.flappy.core;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.flappy.app.Game;
import org.flappy.database.DatabaseManager;
import org.flappy.entity.Bird;
import org.flappy.entity.Pipe;
import org.flappy.utils.GraphicsUtils;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {
    private final GraphicsContext gc;
    private final Bird bird;
    private boolean started = false;
    private boolean gameOver = false;
    private double floatOffset = 0;
    private boolean goingUp = true;
    private int score = 0;
    private String difficulty;

    private double bgX1 = 0;
    private double bgX2;
    private double bgSpeed = 1;

    private final List<Pipe> pipes = new ArrayList<>();
    private static final double PIPE_GAP = 240;
    private double pipeSpawnDelay = 2000;
    private double timeSinceLastSpawn = 0;
    private long previousSpawnTime = 0;

    private final Image ground;
    private double groundX1 = 0;
    private double groundX2;
    private final double groundSpeed = 1;

    private Image backgroundDay;
    private Image backgroundNight;
    private Image currentBackground;

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
            default -> {
                bgSpeed = 1.5;
                Pipe.PIPE_SPEED = 2;
                pipeSpawnDelay = 2000;
            }
        }

        this.backgroundDay = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
        this.backgroundNight = new Image(getClass().getResource("/images/background/background-night.png").toExternalForm());
        this.currentBackground = backgroundDay;

        this.pipeGreen = new Image(getClass().getResource("/images/pipes/pipe-green.png").toExternalForm());
        this.pipeRed = new Image(getClass().getResource("/images/pipes/pipe-red.png").toExternalForm());
        this.currentPipeImage = pipeGreen;

        this.bgX1 = 0;
        this.bgX2 = Game.WIDTH;

        this.ground = new Image(getClass().getResource("/images/ground/ground.png").toExternalForm());
        this.groundX2 = Game.WIDTH;

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
        renderBackground();
        bird.render(gc);

        for (Pipe pipe : pipes) {
            pipe.render(gc);
        }

        renderGround();

        if (started && !gameOver) {
            renderScore(Game.WIDTH / 2, 20, 1.0);
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
            renderScore(Game.WIDTH / 2, scorePosY, 2.0);
        }
    }

    private void renderScore(double posX, double posY, double scale) {
        String scoreStr = String.valueOf(score);

        double digitWidth = 24 * scale;
        double digitHeight = 32 * scale;
        double totalWidth = (digitWidth + (double) 5) * scoreStr.length() - (double) 5;

        double startX = posX - (totalWidth / 2);

        for (int i = 0; i < scoreStr.length(); i++) {
            char digitChar = scoreStr.charAt(i);
            String digitPath = String.format("/images/numbers/%c.png", digitChar);

            Image digitImage = new Image(getClass().getResource(digitPath).toExternalForm());
            double currentX = startX + i * (digitWidth + (double) 5);
            gc.drawImage(digitImage, currentX, posY, digitWidth, digitHeight);
        }
    }

    private void updateVisualStyle() {
        if ((score / 10) % 2 == 0) {
            currentBackground = backgroundDay;
            currentPipeImage = pipeGreen;
        } else {
            currentBackground = backgroundNight;
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

    private void returnToStartScreen() {
        Game.goToStartScreen();
    }

    private void renderBackground() {
        double width = Game.WIDTH;
        double height = Game.HEIGHT;

        gc.drawImage(currentBackground, bgX1, 0, width, height);
        gc.drawImage(currentBackground, bgX2, 0, width, height);

        bgX1 -= bgSpeed;
        bgX2 -= bgSpeed;

        if (bgX1 + width <= 0) {
            bgX1 = bgX2 + width;
        }

        if (bgX2 + width <= 0) {
            bgX2 = bgX1 + width;
        }
    }

    private void renderGround() {
        double groundY = Game.HEIGHT - ground.getHeight();
        gc.drawImage(ground, groundX1, groundY, Game.WIDTH, ground.getHeight());
        gc.drawImage(ground, groundX2, groundY, Game.WIDTH, ground.getHeight());

        groundX1 -= groundSpeed;
        groundX2 -= groundSpeed;

        if (groundX1 + Game.WIDTH <= 0) {
            groundX1 = groundX2 + Game.WIDTH;
        }

        if (groundX2 + Game.WIDTH <= 0) {
            groundX2 = groundX1 + Game.WIDTH;
        }
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