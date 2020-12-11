package com.neowise.game.gameObject.rocket;

import com.neowise.game.util.OrbitalAngle;

/**
 * Created by tabletop on 5/31/16.
 */
public class Target_RocketBarrage extends Target {

    int rockets;
    float timer;
    boolean launch;

    Target_RocketBarrage(OrbitalAngle oa, int rockets){
        super(oa);
        this.rockets = rockets;
        timer = 60;
        launch = false;

    }

    public void updateTimer(float delta){
        timer -= delta;

        if (timer <= 0){
            timer = 20;
            launch = true;
        }
    }


}
