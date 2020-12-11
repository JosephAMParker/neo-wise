package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.pickup.PowerUp;

/**
 * Created by tabletop on 6/14/16.
 */
public class WeaponUpgrader extends PowerUp {
    public float size;
    boolean inc;
    public WeaponUpgrader(Vector2 pos, Vector2 toPlanet){
        this.pos = pos;
        this.vel = toPlanet.scl(2);
        this.size = 3;
        inc = true;


//        animation = new MyAnimation("WeaponUpgrade",4,pos,0,true,8,20);
    }

    public void updateSize(float delta){

        if (inc){
            if (size > 7)
                inc = false;
            else
                size += 0.1;
        }

        else{
            if (size < 3)
                inc = true;
            else
                size -= 0.1;
        }

    }
}
