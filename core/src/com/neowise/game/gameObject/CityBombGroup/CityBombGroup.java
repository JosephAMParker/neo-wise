package com.neowise.game.gameObject.CityBombGroup;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.weaponProjectile.CityBombStraight;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class CityBombGroup {

    public float bombTimer, bombTimerReset, angle;
    public int bombDropCount;

    public CityBombGroup(float angle, int bombDropCount) {

        this.angle=angle;
        this.bombDropCount=bombDropCount;
        bombTimerReset = 1;
        bombTimer = bombTimerReset;
    }

    public boolean empty() {
        return bombDropCount <= 0;
    }

    public void updateTimers(float delta) {
        bombTimer -= delta;
    }

    public void spawnBombs(Collection<WeaponProjectile> hostileProjectiles) {
        if(bombTimer <= 0){
            bombTimer += bombTimerReset;
            bombDropCount-=1;

            Vector2 bombPos = new Vector2(0,800);
            bombPos.rotateDeg(angle + RandomUtil.nextFloat() * 40 - 20);
            hostileProjectiles.add(new CityBombStraight(bombPos));
        }
    }
}
