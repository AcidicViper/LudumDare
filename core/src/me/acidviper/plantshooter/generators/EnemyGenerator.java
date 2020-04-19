package me.acidviper.plantshooter.generators;

import me.acidviper.plantshooter.entities.Enemy;
import me.acidviper.plantshooter.screens.GameScreen;

import java.util.Random;

public class EnemyGenerator {
    public int waveNumber;
    int buyTime;

    long timeSinceLastRound;
    long lastSpawn = 0;
    public boolean inWave = false;

    GameScreen screen;

    int enemiesToSpawnInWave =0;

    float enemyStrength;

    public EnemyGenerator(int waveNumber, int buyTime, GameScreen screen, boolean inWave) {
        this.inWave = inWave;
        this.waveNumber = waveNumber;
        this.buyTime = buyTime;
        this.screen = screen;
    }
    public void update(float dt) {
        long currentTime = System.currentTimeMillis();

        if (waveNumber> 15) { enemyStrength = 2.2f; } else if (waveNumber > 10) { enemyStrength = 2f;} else if (waveNumber > 6) { enemyStrength = 1.8f;} else if (waveNumber > 2) { enemyStrength = 1.5f;} else {enemyStrength = 1;}

        if (inWave) {
            timeSinceLastRound = currentTime;
            if (enemiesToSpawnInWave < 1) { waveNumber += 1; inWave = false; return;}
            if (currentTime - lastSpawn >= 1200) {
                lastSpawn = currentTime;
                Random random = new Random();
                if (random.nextBoolean()) {
                    screen.enemyArrayList.add(new Enemy(25,250, screen.world,screen, true, enemyStrength));
                    enemiesToSpawnInWave--;
                } else {
                    enemiesToSpawnInWave --;
                    screen.enemyArrayList.add(new Enemy(2000,250, screen.world,screen, false, enemyStrength));
                }
            }
        }

        if (currentTime - timeSinceLastRound >= buyTime && !inWave) {
                screen.plant.currentShield = screen.shield;
                inWave = true;
                timeSinceLastRound = currentTime;
                Random random = new Random();
                enemiesToSpawnInWave = random.nextInt((waveNumber + 10) - (waveNumber + 5) + 1) + waveNumber + 5;
        }
    }
}
