package com.neowise.game.draw;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class ScreenShake {

    private static float shakeTime = 1, maxShakeTime = 1, duration = 0, intensity = 10;

    public static void shake(float delta, OrthographicCamera camera) {

        // Only shake when required.
        if(duration > 0) {

            maxShakeTime = Math.max(maxShakeTime, duration);

            // Calculate the amount of shake based on how long it has been shaking already
            float currentPower = intensity * camera.zoom * (duration / maxShakeTime);
            float x = (RandomUtil.nextFloat() - 0.5f) * currentPower;
            float y = (RandomUtil.nextFloat() - 0.5f) * currentPower;
            camera.translate(-x, -y);

            // Increase the elapsed time by the delta provided.
            duration -= delta;
        }
    }

    public static void addDuration(){
        duration = shakeTime;
    }

    public static void addDuration(float dur){
        if(dur > duration)
            duration = dur;
    }

    public static void clear(){
        duration = 0;
        maxShakeTime = 1;
    }
}
