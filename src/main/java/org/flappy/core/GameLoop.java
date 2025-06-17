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
import org.flappy.entities.CoinManager;
import org.flappy.entities.PipeManager;
import org.flappy.graphics.BackgroundRenderer;
import org.flappy.graphics.GraphicsUtils;
import org.flappy.graphics.GroundRenderer;
import org.flappy.graphics.ScoreRenderer;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class GameLoop extends AnimationTimer {
    private final GraphicsContext gc;
    private final Bird bird;
    private final ScoreRenderer scoreRenderer;
    private final BackgroundRenderer backgroundRenderer;
    private final GroundRenderer groundRenderer;
    private final PipeManager pipeManager;
    private final CoinManager coinManager;

    private boolean started = false;
    private boolean gameOver = false;
    private int score = 0;
    private String difficulty;

    private long previousSpawnTime = 0;

    private TextField nameField;
    private Button saveButton;
    private StackPane gameRoot;
    private final Image gameOverImage = new Image(getClass().getResource("/images/basics/game-over.png").toExternalForm());
    private final Image getReadyImage = new Image(getClass().getResource("/images/basics/get-ready.png").toExternalForm());

    public GameLoop(GraphicsContext gc, String skinName, String difficulty, StackPane gameRoot) {
        this.gc = gc;
        this.bird = new Bird(100, 300, skinName);
        this.difficulty = difficulty;
        this.gameRoot = gameRoot;
        this.scoreRenderer = new ScoreRenderer(gc);

        this.backgroundRenderer = new BackgroundRenderer(gc, new Image(getClass().getResource("/images/background/background-day.png").toExternalForm()), new Image(getClass().getResource("/images/background/background-night.png").toExternalForm()), false,  difficulty);
        this.groundRenderer = new GroundRenderer(gc, new Image(getClass().getResource("/images/ground/ground.png").toExternalForm()));
        this.pipeManager = new PipeManager(
                new Image(getClass().getResource("/images/pipes/pipe-green.png").toExternalForm()),
                new Image(getClass().getResource("/images/pipes/pipe-red.png").toExternalForm()),
                Game.WIDTH,
                Game.HEIGHT,
                difficulty
        );
        this.coinManager = new CoinManager(Game.WIDTH, Game.HEIGHT);
    }

    public Bird getBird() {
        return bird;
    }

    @Override
    public void handle(long now) {
        double deltaTime = (now - previousSpawnTime) / 1e9;
        previousSpawnTime = now;
        if (!started) {
            bird.update(deltaTime);
            backgroundRenderer.render();
            groundRenderer.render();
            bird.render(gc);
            return;
        }

        if (gameOver) {
            return;
        }

        bird.update(deltaTime);

        checkCollisions();
        checkCoinCollisions();
        backgroundRenderer.update();
        groundRenderer.update();
        pipeManager.update(deltaTime, now);
        coinManager.update(deltaTime);
        score += pipeManager.handleScoring(bird);
        if (score >= 5) {
            pipeManager.switchMoving(true);
        }
        updateVisualStyle();
        render();
    }

    private void render() {
        backgroundRenderer.render();
        bird.render(gc);

        pipeManager.render(gc);
        coinManager.render(gc);

        groundRenderer.render();
        scoreRenderer.renderScoreboard(started && !gameOver, score, Game.getCoins(), gameOverImage.getHeight() * 2.5);

        if (!started) {
            GraphicsUtils.drawImageCentered(gc, getReadyImage, 2.5, 2.5, 0.33, Game.WIDTH, Game.HEIGHT);
        }

        if (gameOver) {
            GraphicsUtils.drawImageCentered(gc, gameOverImage, 2.5, 2.5, 0.33, Game.WIDTH, Game.HEIGHT - 100);
        }
    }

    private void updateVisualStyle() {
        if ((score / 10) % 2 == 0) {
            backgroundRenderer.switchBackground(false);
            pipeManager.switchImage(false);
        } else {
            backgroundRenderer.switchBackground(true);
            pipeManager.switchImage(true);
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
        if (bird.isIdle()) {
            bird.jump();
            started = true;
        }
    }
    public boolean isStarted() {
        return started;
    }

    private void checkCollisions() {
        if (pipeManager.checkCollision(bird)) {
            gameOver = true;
            stop();
            return;
        }

        if (bird.getHitbox().getMinY() <= 0 || bird.getHitbox().getMaxY() >= Game.HEIGHT - groundRenderer.getHeight()) {
            stop();
        }
    }

    private void checkCoinCollisions() {
        if (coinManager.checkCollision(bird)) {
            Game.addCoins(1);
        }
    }
}