package com.neowise.game.main;

import java.util.Random;

public class LevelInfo {

    public float levelDistance;
    public float levelTimer;
    public int level;
    public enum levelType {MINIGAME, BASICLEVEL}
    private Random random;

    public float dumbEnemyTimer;
    public float dumbEnemyTimerReset;

    public LevelInfo(int level){

        this.level = level;
        random = new Random();
        levelDistance = -1500000;//;- random.nextInt(2000) * (level+1);
        levelTimer = 0;

        dumbEnemyTimerReset = 10;
        dumbEnemyTimer = 0.5f;

    }
}
