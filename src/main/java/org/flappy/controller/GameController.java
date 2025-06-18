package org.flappy.controller;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
        import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import org.flappy.database.DatabaseManager;
import org.flappy.graphics.ScoreRenderer;
import org.flappy.app.Game;
import org.flappy.core.GameLoop;

import java.util.List;
import java.util.Set;

public class GameController {
    private final Stage stage;
    private final Set<String> unlockedSkins;
    private final String[] skins;
    private final int width;
    private final int height;

    public GameController(Stage stage, Set<String> unlockedSkins, String[] skins, int width, int height) {
        this.stage = stage;
        this.unlockedSkins = unlockedSkins;
        this.skins = skins;
        this.width = width;
        this.height = height;
    }

    public void startGame(String selectedSkin, String difficulty) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image bgImageGame = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
        ImageView bgView = new ImageView(bgImageGame);
        bgView.setFitWidth(width);
        bgView.setFitHeight(height);

        StackPane gameRoot = new StackPane(bgView, canvas);
        Scene gameScene = new Scene(gameRoot);

        GameLoop gameLoop = new GameLoop(gc, selectedSkin, difficulty, gameRoot);

        gameScene.setOnKeyPressed(ev -> {
            switch (ev.getCode()) {
                case SPACE -> {
                    if (!gameLoop.isStarted()) {
                        gameLoop.activateBird();
                    }
                    gameLoop.getBird().jump();
                }
            }
        });

        gameLoop.start();
        stage.setScene(gameScene);
    }

    public void showLeaderboard(String difficulty, Scene backScene) {
        List<String> topScores = DatabaseManager.getTopScores(difficulty, 10);

        VBox leaderboardLayout = new VBox(15);
        leaderboardLayout.setAlignment(Pos.CENTER);
        leaderboardLayout.setPadding(new Insets(20));

        Label title = new Label("Top Scores:");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        VBox scoreBox = new VBox(5);
        scoreBox.setAlignment(Pos.CENTER);

        for (String entry : topScores) {
            Label scoreLabel = new Label(entry);
            scoreLabel.setStyle("-fx-font-size: 14px; -fx-font-family: 'Arial';");
            scoreBox.getChildren().add(scoreLabel);
        }

        javafx.scene.control.Button backBtn = new javafx.scene.control.Button("Back");
        backBtn.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #ffffff, #eeeeee);
            -fx-border-color: #5d4037;
            -fx-border-width: 2px;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-padding: 5 10 5 10;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-font-family: "Arial";
        """);
        backBtn.setOnAction(ev -> stage.setScene(backScene));

        leaderboardLayout.getChildren().addAll(title, scoreBox, backBtn);

        Image bgImage = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(width);
        bgView.setFitHeight(height);

        Image groundImg = new Image(getClass().getResource("/images/ground/ground.png").toExternalForm());
        ImageView groundView = new ImageView(groundImg);
        groundView.setFitWidth(width);
        groundView.setPreserveRatio(false);
        StackPane.setAlignment(groundView, Pos.BOTTOM_CENTER);

        StackPane root = new StackPane(bgView, groundView, leaderboardLayout);
        Scene leaderboardScene = new Scene(root, width, height);

        stage.setScene(leaderboardScene);
    }

    public void showShop(Scene backScene) {
        VBox shopLayout = new VBox(10);
        shopLayout.setAlignment(Pos.CENTER);

        Label title = new Label("Buy a skin for 5 coins:");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox skinsGrid = new VBox(10);
        skinsGrid.setAlignment(Pos.CENTER);

        Canvas coinCanvas = new Canvas(width, 50);
        GraphicsContext gc = coinCanvas.getGraphicsContext2D();
        ScoreRenderer scoreRenderer = new ScoreRenderer(gc);
        scoreRenderer.renderCoins(Game.getCoins(), 1.0);

        HBox currentRow = new HBox(10);
        currentRow.setAlignment(Pos.CENTER);

        int count = 0;

        for (String skin : skins) {
            StackPane skinPane = new StackPane();

            ImageView skinImg = new ImageView(
                    new Image(getClass().getResource("/images/birds/" + skin + "/" + skin + "bird-midflap.png").toExternalForm())
            );
            skinImg.setFitWidth(60);
            skinImg.setFitHeight(48);

            Region overlay = new Region();
            overlay.setStyle("-fx-background-color: red; -fx-opacity: 0.5;");
            overlay.setPrefSize(60, 48);

            javafx.scene.control.Button skinButton = new javafx.scene.control.Button("", skinImg);
            skinButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

            skinPane.getChildren().addAll(skinImg);
            if (!unlockedSkins.contains(skin)) {
                skinPane.getChildren().addAll(overlay);
                overlay.setMouseTransparent(true);
                skinButton.setOnAction(ev -> {
                    if (Game.getCoins() >= 5) {
                        unlockedSkins.add(skin);
                        Game.addCoins(-5);
                        DatabaseManager.unlockSkin(skin);
                        showShop(backScene); // odśwież widok
                    }
                });
            }

            skinPane.getChildren().add(skinButton);
            StackPane.setAlignment(skinButton, Pos.CENTER);

            VBox skinBox = new VBox(skinPane);
            skinBox.setAlignment(Pos.CENTER);

            currentRow.getChildren().add(skinBox);
            count++;

            if (count % 3 == 0) {
                skinsGrid.getChildren().add(currentRow);
                currentRow = new HBox(10);
                currentRow.setAlignment(Pos.CENTER);
            }
        }

        if (!currentRow.getChildren().isEmpty()) {
            skinsGrid.getChildren().add(currentRow);
        }

        javafx.scene.control.Button backButton = new javafx.scene.control.Button("Back");
        backButton.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #ffffff, #eeeeee);
            -fx-border-color: #5d4037;
            -fx-border-width: 2px;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-padding: 5 10 5 10;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-font-family: "Arial";
        """);
        backButton.setOnAction(ev -> stage.setScene(backScene));

        shopLayout.getChildren().addAll(coinCanvas, title, skinsGrid, backButton);
        Image bgImage = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(width);
        bgView.setFitHeight(height);

        Image groundImg = new Image(getClass().getResource("/images/ground/ground.png").toExternalForm());
        ImageView groundView = new ImageView(groundImg);
        groundView.setFitWidth(width);
        groundView.setPreserveRatio(false);
        StackPane.setAlignment(groundView, Pos.BOTTOM_CENTER);

        StackPane root = new StackPane(bgView, groundView, shopLayout);
        Scene shopScene = new Scene(root, width, height);
        stage.setScene(shopScene);
    }
}
