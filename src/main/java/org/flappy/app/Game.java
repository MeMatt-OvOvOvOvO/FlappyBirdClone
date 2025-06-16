package org.flappy.app;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.flappy.core.GameLoop;
import org.flappy.database.DatabaseManager;

import java.util.List;

public class Game extends Application {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 600;

    private static Scene startScene;
    private static Stage primaryStage;

    private static final java.util.Set<String> unlockedSkins = new java.util.HashSet<>();


    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.initializeDatabase();
        Game.primaryStage = primaryStage;
        primaryStage.setTitle("Flappy Bird Clone");

        VBox startLayout = new VBox(10);
        startLayout.setAlignment(Pos.CENTER);
        startLayout.setPadding(new javafx.geometry.Insets(50));

        String[] skins = {"yellow", "red", "blue"};
        int[] skinIndex = {0};
        unlockedSkins.add("yellow");

        ImageView skinPreview = new ImageView();
        skinPreview.setFitWidth(50);
        skinPreview.setFitHeight(40);

        Runnable updateSkinImage = () -> {
            String skin = skins[skinIndex[0]];
            Image image = new Image(getClass().getResource("/images/birds/" + skin + "/" + skin + "bird-midflap.png").toExternalForm());
            skinPreview.setImage(image);
        };

        Image leftArrowImg = new Image(getClass().getResource("/images/arrows/arrow-left.png").toExternalForm());
        ImageView leftArrowView = new ImageView(leftArrowImg);
        leftArrowView.setFitWidth(32);
        leftArrowView.setFitHeight(32);
        Button leftButton = new Button("", leftArrowView);
        leftButton.setStyle("-fx-background-color: transparent;");

        Image rightArrowImg = new Image(getClass().getResource("/images/arrows/arrow-right.png").toExternalForm());
        ImageView rightArrowView = new ImageView(rightArrowImg);
        rightArrowView.setFitWidth(32);
        rightArrowView.setFitHeight(32);
        Button rightButton = new Button("", rightArrowView);
        rightButton.setStyle("-fx-background-color: transparent;");


        leftButton.setOnAction(e -> {
            do {
                skinIndex[0] = (skinIndex[0] - 1 + skins.length) % skins.length;
            } while (!unlockedSkins.contains(skins[skinIndex[0]]));
            updateSkinImage.run();
        });

        rightButton.setOnAction(e -> {
            do {
                skinIndex[0] = (skinIndex[0] + 1) % skins.length;
            } while (!unlockedSkins.contains(skins[skinIndex[0]]));
            updateSkinImage.run();
        });

        HBox skinSlider = new HBox(2, leftButton, skinPreview, rightButton);
        skinSlider.setAlignment(Pos.CENTER);
        updateSkinImage.run();

        ComboBox<String> speedSelector = new ComboBox<>();
            speedSelector.setStyle("""
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
        speedSelector.getItems().addAll("Easy", "Medium", "Hard");
        speedSelector.setValue("Medium");

        Image startImg = new Image(getClass().getResource("/images/basics/play-button.png").toExternalForm());
        ImageView startView = new ImageView(startImg);
        startView.setFitWidth(80);
        startView.setFitHeight(35);

        Button startButton = new Button("", startView);
        startButton.setStyle("-fx-background-color: transparent;");

        Button shopButton = new Button("SHOP");
        shopButton.setStyle("""
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

        Button leaderboardButton = new Button("LEADERBOARD");
        leaderboardButton.setStyle("""
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


        leaderboardButton.setOnAction(e -> {
            List<String> topScores = DatabaseManager.getTopScores(speedSelector.getValue(), 10);
            VBox leaderboardLayout = new VBox(10);
            leaderboardLayout.setAlignment(Pos.CENTER);
            leaderboardLayout.getChildren().add(new Label("Top Scores:"));

            for (String entry : topScores) {
                leaderboardLayout.getChildren().add(new Label(entry));
            }

            Button backBtn = new Button("Back");
            backBtn.setOnAction(ev -> primaryStage.setScene(startScene));
            leaderboardLayout.getChildren().add(backBtn);

            Scene leaderboardScene = new Scene(leaderboardLayout, WIDTH, HEIGHT);
            primaryStage.setScene(leaderboardScene);
        });


        Image bgImage = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
        ImageView backgroundView = new ImageView(bgImage);
        backgroundView.setFitWidth(WIDTH);
        backgroundView.setFitHeight(HEIGHT);


        Image groundImg = new Image(getClass().getResource("/images/ground/ground.png").toExternalForm());
        ImageView groundView = new ImageView(groundImg);
        groundView.setFitWidth(WIDTH);
        groundView.setPreserveRatio(false);

        StackPane.setAlignment(groundView, Pos.BOTTOM_CENTER);



        startLayout.getChildren().addAll(skinSlider, speedSelector, startButton, shopButton, leaderboardButton);

        StackPane startRoot = new StackPane(backgroundView, groundView, startLayout);
        startScene = new Scene(startRoot, WIDTH, HEIGHT);

        startButton.setOnAction(e -> {
            String selectedSkin = skins[skinIndex[0]];

            Canvas canvas = new Canvas(WIDTH, HEIGHT);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            Image bgImageGame = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
            ImageView bgView = new ImageView(bgImageGame);
            bgView.setFitWidth(WIDTH);
            bgView.setFitHeight(HEIGHT);

            StackPane gameRoot = new StackPane(bgView, canvas);
            Scene gameScene = new Scene(gameRoot);

            GameLoop gameLoop = new GameLoop(gc, selectedSkin, speedSelector.getValue(), gameRoot);

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
            primaryStage.setScene(gameScene);
        });

        shopButton.setOnAction(e -> {
            VBox shopLayout = new VBox(10);
            shopLayout.setAlignment(Pos.CENTER);

            Label title = new Label("Buy a skin:");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            HBox skinsRow = new HBox(10);
            skinsRow.setAlignment(Pos.CENTER);

            for (String skin : skins) {
                if (unlockedSkins.contains(skin)) continue;

                ImageView skinImg = new ImageView(
                        new Image(getClass().getResource("/images/birds/" + skin + "/" + skin + "bird-midflap.png").toExternalForm())
                );
                skinImg.setFitWidth(60);
                skinImg.setFitHeight(48);

                Button skinButton = new Button("", skinImg);
                skinButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                skinButton.setOnAction(ev -> {
                    unlockedSkins.add(skin);
                    updateSkinImage.run();
                    primaryStage.setScene(startScene);
                });

                VBox skinBox = new VBox(skinButton);
                skinBox.setAlignment(Pos.CENTER);
                skinsRow.getChildren().add(skinBox);
            }

            Button backButton = new Button("Back");
                backButton.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #ffffff, #eeeeee);
            -fx-border-color: #5d4037;
            -fx-border-width: 2px;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-padding: 5 10 5 10;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-font-family: "Arial";""");
            backButton.setOnAction(ev -> primaryStage.setScene(startScene));

            shopLayout.getChildren().addAll(title, skinsRow, backButton);
            Scene shopScene = new Scene(shopLayout, WIDTH, HEIGHT);
            primaryStage.setScene(shopScene);
        });

        primaryStage.setScene(startScene);
        primaryStage.show();

    }

    public static void goToStartScreen() {
        primaryStage.setScene(startScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}