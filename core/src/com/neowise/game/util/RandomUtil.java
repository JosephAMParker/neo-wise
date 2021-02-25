package com.neowise.game.util;

import java.util.Random;

public class RandomUtil {
    private static Random random = new Random();

    public static int nextInt(int i){
        return random.nextInt(i);
    }

    public static int nextInt(){
        return random.nextInt();
    }

    public static float nextFloat(){
        return random.nextFloat();
    }

    public static float nextFloat2() { return (random.nextFloat() - 0.5f) * 2; }

    public static boolean nextBoolean(){
        return random.nextBoolean();
    }

    public static boolean nextBoolean(float prob){
        return random.nextFloat() <= prob;
    }

    public static int nextOne() {
        if(random.nextBoolean())
            return 1;
        return -1;
    }
}
