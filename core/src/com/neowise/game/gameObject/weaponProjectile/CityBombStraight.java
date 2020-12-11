package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by tabletop on 6/19/15.
 */
public class CityBombStraight extends CityBomb{


    public CityBombStraight(Vector2 pos, Vector2 target) {
        super(pos, target);
        vel = target.sub(pos).nor().scl(speed);
        this.size = 3;

    }

    public void updatePos(float delta) {

        lifespan += delta;
        pos.add(vel.cpy().scl(delta));

    }
}
