package com.neowise.game.gameObject.particle;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.physics.CollisionDetector;

import java.util.Random;

/**
 * Created by tabletop on 2/27/16.
 */
public class SmokeGenerator {

    float lifespan;
    float smokeTimer,smokeMin;
    boolean makeSmoke;
    Vector2 pos;
    Random random;

    SmokeGenerator(Vector2 pos) {

        this.pos = pos;
        random = new Random();
        lifespan = random.nextInt() % 20;
        smokeTimer = random.nextInt() % 30;
        smokeMin   = random.nextInt() % 10;

    }

    /**
     * rotates the turret in space to match the rotation of the planet its resting on
     * @param
     */
    public void rotateByPlanet(double rotationDelta, Vector2 PlanetPos) {


        pos.sub(PlanetPos);
        Vector2 newPos = new Vector2();
        newPos.x  = (float) (pos.x * Math.cos(rotationDelta - Math.PI*2) - pos.y  * Math.sin(rotationDelta - Math.PI*2));
        newPos.y = (float) (pos.x * Math.sin(rotationDelta - Math.PI*2) + pos.y * Math.cos(rotationDelta - Math.PI*2));
        pos = newPos.add(PlanetPos);

        //this.rotation += rotation;
    }

    public void updateTimer(float delta){
        lifespan -= delta;

        if(--smokeTimer > 0) {
            makeSmoke  = true;
        }
        else
            makeSmoke = false;

        if(smokeTimer < -smokeMin){
            smokeMin   = random.nextInt() % 8;
            smokeTimer = random.nextInt() % 20;
        }
    }
}
