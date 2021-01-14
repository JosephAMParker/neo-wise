package com.neowise.game.gameObject.player.Weapon;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.explosion.Explosion;
import com.neowise.game.gameObject.weaponProjectile.Bullet;
import com.neowise.game.gameObject.weaponProjectile.PlayerCityBomb;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CityDefender extends Weapon {

    private boolean cityBombsActive, weaponFired;
    private float bombDelay, bombDelayReset;

    public CityDefender(){
        super();
        weaponType = Constants.WEAPON_TYPES.BOMB;
        bombDelayReset = 0.2f;
        bombDelay = bombDelayReset;
    }

    private void detonateBombs(Collection<WeaponProjectile> friendlyProjectiles, Collection<Explosion> friendlyExplosions) {
        for(Iterator<WeaponProjectile> pit = friendlyProjectiles.iterator(); pit.hasNext(); ){
            WeaponProjectile bomb = pit.next();
            if(bomb.canDetonate) {
                Explosion exp = new Explosion(bomb.getPos(), 30, 3, 1);
                friendlyExplosions.add(exp);
                pit.remove();
            }
        }
    }

    @Override
    public void fire(Vector2 playerPos, boolean firePressed, BasicLevel basicLevel) {

        if (!firePressed) {
            weaponFired = false;
            return;
        }

        if(!weaponFired) {
            if (cityBombsActive) {
                detonateBombs(basicLevel.friendlyProjectiles, basicLevel.friendlyExplosions);
                cityBombsActive = false;
            } else {
                Collection<WeaponProjectile> pcb = basicLevel.friendlyProjectiles;

                for(int i = 0;i<power;i++){
                    pcb.add(new PlayerCityBomb(playerPos.cpy(), playerPos.cpy().nor().rotateDeg(RandomUtil.nextInt(10)-5), 125 + RandomUtil.nextInt(50), 2));
                }
                cityBombsActive = true;
                bombDelay = bombDelayReset;
            }
        }

        weaponFired = true;
    }

    @Override
    public Vector2 recoil(Vector2 up){
        return up.cpy().scl(-100);
    }

    @Override
    public void updateTimers(float delta) {
        if(bombDelay > 0)
            bombDelay -= delta;
    }
}
