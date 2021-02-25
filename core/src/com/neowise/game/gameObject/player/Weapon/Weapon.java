package com.neowise.game.gameObject.player.Weapon;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.pickup.PowerUp;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;

public abstract class Weapon {

    public Sprite sprite;
    protected float shootTimer, shootTimerReset;
    public Constants.WEAPON_TYPES weaponType;
    public int power = 1;
    protected String weaponName;

    public abstract void fire(Vector2 playerPos, boolean firePressed, BasicLevel basicLevel, float delta);

    public abstract Vector2 recoil(Vector2 up);

    public abstract void updateTimers(float delta);

    public void upgrade(PowerUp powerUp){
        power += 1;
    }

    public Sprite getSprite(){
        return sprite;
    }

    public String getName(){
        return weaponName;
    }
}
