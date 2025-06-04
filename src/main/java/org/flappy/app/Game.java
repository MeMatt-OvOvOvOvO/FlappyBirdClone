package org.flappy.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.flappy.core.GameLoop;

public class Game extends Application {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 600;

    private Scene startScene;
    private Scene gameScene;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Flappy Bird Clone");

        VBox startLayout = new VBox(10);
        startLayout.setStyle("-fx-alignment: center; -fx-padding: 50;");

        ComboBox<String> skinSelector = new ComboBox<>();
        skinSelector.getItems().addAll("Yellow", "Red", "Blue");
        skinSelector.setValue("Yellow");

        ComboBox<String> speedSelector = new ComboBox<>();
        speedSelector.getItems().addAll("Easy", "Medium", "Hard");
        speedSelector.setValue("Medium");

        Button startButton = new Button("Start Game");

        startLayout.getChildren().addAll(skinSelector, speedSelector, startButton);
        startScene = new Scene(startLayout, WIDTH, HEIGHT);

        // Game canvas
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        GameLoop gameLoop = new GameLoop(gc);
        gameScene = new Scene(new StackPane(canvas));

        gameScene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE -> gameLoop.getBird().jump();
            }
        });

        startButton.setOnAction(e -> {
            gameLoop.start();
            primaryStage.setScene(gameScene);
        });

        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}