package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.util.OrbitalAngle;

/**
 * Created by tabletop on 6/22/15.
 */
public class Ship_CityBombLobber extends Ship {

    float dis2orbit;
    public float bombTimer, bombTimerMax;
    public boolean shootLeft;

    public Ship_CityBombLobber(float x, float y) {
        super(x, y);

        altitude = 999;
        dead = false;
        inSquad = false;
        oa = new OrbitalAngle(0.0f);
        vel = new Vector2(0,0);

        width = 15;
        height = 10;
        health = 100;
        resWorth = 50;


        speed = 0.4f;
        bombTimer = 0;
        bombTimerMax = 200;
        shootLeft = true;


    }

    @Override
    public void updateTimers(float delta){

        bombTimer += delta;
    }

    @Override
    public void updateVel(Vector2 HBPos) {

        Vector2 toPlanet = (HBPos.cpy()).sub(pos);
        oa.angle = 360-toPlanet.angle();
        rotation = (float) ((90 + toPlanet.angle()) * Math.PI / 180);
        toPlanet.nor();

		/*
			sqPlace.pos.setAngle(180+(float) (rotation * 180 / Math.PI));
			vel.scl(0);
			pos.lerp(squad.pos.cpy().add(sqPlace.pos), 0.8f);
			*/

        altitude = pos.dst(HBPos);


        dis2orbit = altitude - 60 - 40 - 50;

        vel.add(toPlanet.cpy().scl(05f*dis2orbit));
        vel.clamp(0, Math.min(5, Math.abs(dis2orbit)));

        toPlanet.rotate(90);


        vel.sub(toPlanet);


    }

    public boolean isBombArmed(){
        return bombTimer > bombTimerMax;
    }
}
