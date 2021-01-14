package com.neowise.game.gameObject.player.Weapon;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.weaponProjectile.Bullet;
import com.neowise.game.gameObject.weaponProjectile.BulletFriendly;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class ChainGun extends Weapon{

    private float originalShootTimer, shootTimerMin, shootSpeedInc;

    public ChainGun(){
        super();

        weaponType = Constants.WEAPON_TYPES.CHAIN_GUN;

        shootTimerReset = 0.3f;
        shootTimerMin   = 0.03f;
        shootSpeedInc   = 0.03f;
        shootTimer = shootTimerReset;
        originalShootTimer = shootTimerReset;

    }

    @Override
    public void fire(Vector2 playerPos, boolean firePressed, BasicLevel basicLevel) {
        if(!firePressed) {
            shootTimerReset = originalShootTimer;
            return;
        }

        if(shootTimer <= 0){
            shootTimerReset -= shootSpeedInc;
            shootTimerReset = Math.max(shootTimerMin, shootTimerReset);
            shootTimer = shootTimerReset;
            Bullet bullet = new BulletFriendly(
                            playerPos.cpy(),
                            playerPos.cpy().nor().rotateDeg((RandomUtil.nextFloat()-0.5f) * 3f),
                           400 + RandomUtil.nextInt(80),
                           20,1);
            basicLevel.friendlyProjectiles.add(bullet);
        }

    }

    @Override
    public Vector2 recoil(Vector2 up) {
        return null;
    }

    @Override
    public void updateTimers(float delta) {
        if(shootTimer > 0)
            shootTimer -= delta;
    }
}
