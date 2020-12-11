package com.neowise.game.gameObject.rocket;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.RectangleGameObject;

/**
 * Created by tabletop on 5/31/16.
 */
public class Rocket extends RectangleGameObject {

    protected float health,damage,speed;
    public boolean dead;
    public OrbitalAngle oa;
    public int resWorth;
    MyAnimation animation;

    public Rocket(Vector2 pos, float width, float height){

        this.pos    = pos;
        this.width  = width;
        this.height = height;

        health = 1;

        dead = false;

    }

    public void updateVel(Vector2 HBPos){

    }

    public Vector2 collisionSpot() {
        return pos.cpy().add(vel.cpy().nor().scl(height/2));
    }
}