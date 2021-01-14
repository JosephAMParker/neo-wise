package com.neowise.game.gameObject;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.util.Constants;

/**
 * Created by tabletop on 6/2/16.
 */
public class GameObject {

    public Vector2 pos, vel, acc, impulse;
    public float mass;

    public Vector2 getPos() {
        return pos;
    }

    public Vector2 getVel() {
        return vel;
    }

    public void setVel(Vector2 vel) {
        this.vel = vel;
    }
}
