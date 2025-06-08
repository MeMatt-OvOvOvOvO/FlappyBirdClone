package org.flappy.app;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.flappy.core.GameLoop;

public class Game extends Application {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 600;

    private static Scene startScene;
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        Game.primaryStage = primaryStage;
        primaryStage.setTitle("Flappy Bird Clone");

        VBox startLayout = new VBox(10);
        startLayout.setStyle("-fx-alignment: center; -fx-padding: 50;");

        String[] skins = {"yellow", "red", "blue"};
        int[] skinIndex = {0};

        ImageView skinPreview = new ImageView();
        skinPreview.setFitWidth(50);
        skinPreview.setFitHeight(40);

        Runnable updateSkinImage = () -> {
            String skin = skins[skinIndex[0]];
            Image image = new Image(getClass().getResource("/images/birds/" + skin + "/" + skin + "bird-midflap.png").toExternalForm());
            skinPreview.setImage(image);
        };

        Button leftButton = new Button("<");
        Button rightButton = new Button(">");

        leftButton.setOnAction(e -> {
            skinIndex[0] = (skinIndex[0] - 1 + skins.length) % skins.length;
            updateSkinImage.run();
        });
        rightButton.setOnAction(e -> {
            skinIndex[0] = (skinIndex[0] + 1) % skins.length;
            updateSkinImage.run();
        });

        HBox skinSlider = new HBox(2, leftButton, skinPreview, rightButton);
        skinSlider.setAlignment(Pos.CENTER);
        updateSkinImage.run();

        ComboBox<String> speedSelector = new ComboBox<>();
        speedSelector.getItems().addAll("Easy", "Medium", "Hard");
        speedSelector.setValue("Medium");

        Image startImg = new Image(getClass().getResource("/images/basics/play-button.png").toExternalForm());
        ImageView startView = new ImageView(startImg);
        startView.setFitWidth(80);
        startView.setFitHeight(35);

        Button startButton = new Button("", startView);
        startButton.setStyle("-fx-background-color: transparent;");

        Image bgImage = new Image(getClass().getResource("/images/background/background-day.png").toExternalForm());
        BackgroundImage background = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(WIDTH, HEIGHT, false, false, false, false)
        );
        startLayout.setBackground(new Background(background));

        startLayout.getChildren().addAll(skinSlider, speedSelector, startButton);
        startScene = new Scene(startLayout, WIDTH, HEIGHT);

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

            GameLoop gameLoop = new GameLoop(gc, selectedSkin);

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