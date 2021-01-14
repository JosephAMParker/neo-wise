package com.neowise.game.gameObject.pickup;

import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.GameObject;
import com.neowise.game.main.BasicLevel;

/**
 * Created by tabletop on 20/06/16.
 */
public abstract class PowerUp extends GameObject {

    float altitude;
    MyAnimation animation;
    protected BasicLevel basicLevel;

    public PowerUp(BasicLevel basicLevel) {
        this.basicLevel = basicLevel;
    }

    public void updatePos(float delta){
        pos.add(vel.cpy().scl(delta));
    }

    public abstract boolean toRemove();

    public abstract void update(float delta);
}
