package com.neowise.game.gameObject.rocket;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by tabletop on 20/06/16.
 */
public class Rocket_Barrage extends Rocket{
    public Rocket_Barrage(Vector2 pos, Vector2 HBPos, float offset) {

        super(pos,10,25);
        speed = 15;
        vel = HBPos.sub(pos).nor().scl(4).rotate(offset);
        rotation = vel.angle() + 90;
    }
}
