package org.flappy.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.flappy.app.Game;

public class ScoreRenderer {
    private final GraphicsContext gc;

    public ScoreRenderer(GraphicsContext gc) {
        this.gc = gc;
    }

    public void renderScore(int score, double posX, double posY, double scale) {
        String scoreStr = String.valueOf(score);

        double digitWidth = 24 * scale;
        double digitHeight = 32 * scale;
        double totalWidth = (digitWidth + 5) * scoreStr.length() - 5;

        double startX = posX - (totalWidth / 2);

        for (int i = 0; i < scoreStr.length(); i++) {
            char digitChar = scoreStr.charAt(i);
            String digitPath = String.format("/images/numbers/%c.png", digitChar);

            Image digitImage = new Image(getClass().getResource(digitPath).toExternalForm());

            double currentX = startX + i * (digitWidth + 5);

            gc.drawImage(digitImage, currentX, posY, digitWidth, digitHeight);
        }
    }

    public void renderCoins(int coinCount, double posX, double posY, double scale) {
        String coinStr = String.valueOf(coinCount);

        double digitWidth = 24 * scale;
        double digitHeight = 32 * scale;
        double totalWidth = (digitWidth + 5) * coinStr.length() + 40;

        double startX = posX - totalWidth;

        for (int i = 0; i < coinStr.length(); i++) {
            char digitChar = coinStr.charAt(i);
            String digitPath = String.format("/images/numbers/%c.png", digitChar);

            Image digitImage = new Image(getClass().getResource(digitPath).toExternalForm());

            double currentX = startX + i * (digitWidth + 5);

            gc.drawImage(digitImage, currentX, posY, digitWidth, digitHeight);
        }

        Image coinIcon = new Image(getClass().getResource("/images/coin/coin.png").toExternalForm());
        gc.drawImage(coinIcon, startX + coinStr.length() * (digitWidth + 5) + 10, posY, 32 * scale, 32 * scale);
    }

    public void renderScoreboard(boolean started, int score, int coinCount, double gameOverImageHeight) {
        double posX = (double) Game.WIDTH / 2;
        double posY;
        double scale = 0;

        if (started) {
            System.out.println("started");
            posY = 20;
            scale = 1.0;
        } else {
            System.out.println("not started");
            posY = (Game.HEIGHT / 2) + (gameOverImageHeight / 2) + 20;
            scale = 2.0;
        }
        renderScore(score, posX, posY, scale);
        renderCoins(coinCount, posX, posY + 32 * scale, scale);
    }
}