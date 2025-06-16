package org.flappy.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GraphicsUtils {
    public static void drawImageCentered(GraphicsContext gc, Image image, double widthFactor, double heightFactor,
                                         double positionFactor, double screenWidth, double screenHeight) {
        double imgWidth = image.getWidth() * widthFactor;
        double imgHeight = image.getHeight() * heightFactor;

        double x = (screenWidth - imgWidth) / 2;
        double y = (screenHeight * positionFactor) - (imgHeight / 2);

        gc.drawImage(image, x, y, imgWidth, imgHeight);
    }
}