package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by tabletop on 6/19/15.
 */
public class CityBombGravity extends  CityBomb{

    Collection<Vector2> path;
    public boolean left;

    public CityBombGravity(Vector2 pos, Vector2 target, boolean left) {
        super(pos, target);
        speed = 2f;
        lifespan = 0;
        path = new ArrayList<Vector2>();
        vel = target.sub(pos).nor().scl(speed);
        this.left = left;
        if(left)
            vel.rotate(-150);
        else
            vel.rotate(150);

    }

    public void updatePos(float delta, boolean clamp) {
        if(clamp)
            vel.clamp(0,speed);
        lifespan += delta;

        //System.out.println(" lif: " + lifespan);

        path.add(pos.cpy());
        pos.add(vel.cpy().scl(delta));

    }


}
