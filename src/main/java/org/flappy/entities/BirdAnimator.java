package org.flappy.entities;

import javafx.scene.image.Image;

public class BirdAnimator {
    private final Image[] flapImages;
    private int currentFlapIndex = 0;
    private double floatOffset = 0;
    private boolean goingUp = true;

    private final double animationSpeed = 0.1;
    private final double maxFloatOffset = 8;
    private final double frameDuration = 0.1;
    private double timeSinceLastFrame = 0;

    public BirdAnimator(String skinName) {
        this.flapImages = new Image[]{
                new Image(getClass().getResource(
                        "/images/birds/" + skinName + "/" + skinName + "bird-downflap.png").toExternalForm()),
                new Image(getClass().getResource(
                        "/images/birds/" + skinName + "/" + skinName + "bird-midflap.png").toExternalForm()),
                new Image(getClass().getResource(
                        "/images/birds/" + skinName + "/" + skinName + "bird-upflap.png").toExternalForm()),
        };
    }

    public Image getCurrentFrame() {
        return flapImages[currentFlapIndex];
    }

    public double getFloatOffset() {
        return floatOffset;
    }

    public void animate(double deltaTime) {
        timeSinceLastFrame += deltaTime;

        if (timeSinceLastFrame >= frameDuration) {
            currentFlapIndex = (currentFlapIndex + 1) % flapImages.length;
            timeSinceLastFrame -= frameDuration;
        }
    }

    public void animateIdleBird(double deltaTime) {
        if (goingUp) {
            floatOffset += animationSpeed;
            if (floatOffset > maxFloatOffset) {
                goingUp = false;
            }
        } else {
            floatOffset -= animationSpeed;
            if (floatOffset < -maxFloatOffset) {
                goingUp = true;
            }
        }

        animate(deltaTime);
    }
}