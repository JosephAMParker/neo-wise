package com.neowise.game.gameObject.pickup;

import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.GameObject;

/**
 * Created by tabletop on 20/06/16.
 */
public class PowerUp extends GameObject {

    float altitude;
    MyAnimation animation;

    public void updatePos(float delta){
        pos.add(vel.cpy().scl(delta));
    }

}
