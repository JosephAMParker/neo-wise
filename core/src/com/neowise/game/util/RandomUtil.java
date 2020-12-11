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

    public static boolean nextBoolean(){
        return random.nextBoolean();
    }
}
