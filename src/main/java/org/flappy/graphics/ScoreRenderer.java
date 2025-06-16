package org.flappy.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

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
}