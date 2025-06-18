package org.flappy.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.flappy.controller.GameController;
import org.flappy.database.DatabaseManager;

import java.util.HashSet;
import java.util.Set;

public class Game extends Application {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 600;

    private static Scene startScene;
    private static Stage primaryStage;
    private static int coins = 0;
    public static final Set<String> unlockedSkins = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.initializeDatabase();
        Game.coins = DatabaseManager.getCoins();
        Game.primaryStage = primaryStage;
        primaryStage.setTitle("Flappy Bird Clone");

        String[] skins = {"yellow", "red", "blue", "green", "orange", "sea", "carmine", "mint", "purple", "pink"};
        int[] skinIndex = {0};

        unlockedSkins.addAll(DatabaseManager.getUnlockedSkins());
        if (unlockedSkins.isEmpty()) {
            unlockedSkins.add("yellow");
            DatabaseManager.unlockSkin("yellow");
        }

        GameController controller = new GameController(primaryStage, unlockedSkins, skins, WIDTH, HEIGHT);

        VBox startLayout = new VBox(10);
        startLayout.setAlignment(Pos.CENTER);
        startLayout.setTranslateY(-30);
        startLayout.setPadding(new Insets(20));

        Image flappyBirdImage = new Image(getClass().getResource("/images/basics/flappy-bird.png").toExternalForm());
        ImageView flappyBirdView = new ImageView(flappyBirdImage);
        flappyBirdView.setFitWidth(200);
        flappyBirdView.setPreserveRatio(true);


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
        VBox.setMargin(skinSlider, new Insets(15, 0, 15, 0));
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
        startButton.setOnAction(e -> controller.startGame(skins[skinIndex[0]], speedSelector.getValue()));


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
        shopButton.setOnAction(e -> controller.showShop(startScene));

        Button leaderboardButton = new Button("LEADERBOARD");
        leaderboardButton.setStyle(shopButton.getStyle());
        leaderboardButton.setOnAction(e -> controller.showLeaderboard(speedSelector.getValue(), startScene));

        startLayout.getChildren().addAll(flappyBirdView, skinSlider, speedSelector, startButton, shopButton, leaderboardButton);


        Image bgImage = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
        ImageView backgroundView = new ImageView(bgImage);
        backgroundView.setFitWidth(WIDTH);
        backgroundView.setFitHeight(HEIGHT);

        Image groundImg = new Image(getClass().getResource("/images/ground/ground.png").toExternalForm());
        ImageView groundView = new ImageView(groundImg);
        groundView.setFitWidth(WIDTH);
        groundView.setPreserveRatio(false);
        StackPane.setAlignment(groundView, Pos.BOTTOM_CENTER);

        StackPane startRoot = new StackPane(backgroundView, groundView, startLayout);
        startScene = new Scene(startRoot, WIDTH, HEIGHT);

        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    public static void addCoins(int value) {
        coins += value;
        DatabaseManager.setCoins(coins);
    }

    public static int getCoins() {
        return coins;
    }

    public static void goToStartScreen() {
        primaryStage.setScene(startScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
