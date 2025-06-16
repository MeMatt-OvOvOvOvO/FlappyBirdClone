package org.flappy.entities;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CoinManager {
    private final List<Coin> coins = new ArrayList<>();
    private final double screenWidth;
    private final double screenHeight;

    private double timeSinceLastSpawn = 0;
    private final double coinSpawnDelay = 2;

    public CoinManager(double screenWidth, double screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void update(double deltaTime) {
        Iterator<Coin> iterator = coins.iterator();
        while (iterator.hasNext()) {
            Coin coin = iterator.next();
            coin.update(-Pipe.PIPE_SPEED);
            if (coin.getX() + coin.getWidth() < 0) {
                iterator.remove();
            }
        }

        timeSinceLastSpawn += deltaTime;
        if (timeSinceLastSpawn >= coinSpawnDelay) {
            spawnCoin();
            timeSinceLastSpawn = 0;
        }
    }

    public void render(GraphicsContext gc) {
        for (Coin coin : coins) {
            coin.render(gc);
        }
    }

    public boolean checkCollision(Bird bird) {
        Iterator<Coin> iterator = coins.iterator();
        while (iterator.hasNext()) {
            Coin coin = iterator.next();
            if (coin.getHitbox().intersects(bird.getHitbox())) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    private void spawnCoin() {
        Random random = new Random();
        double coinX = screenWidth + 100;
        double coinY = 100 + random.nextDouble() * (screenHeight - 200);
        coins.add(new Coin(coinX, coinY));
    }

    public void reset() {
        coins.clear();
    }
}