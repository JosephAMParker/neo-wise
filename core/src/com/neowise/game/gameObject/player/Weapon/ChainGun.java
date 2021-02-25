package com.neowise.game.gameObject.player.Weapon;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.gameObject.weaponProjectile.Bullet;
import com.neowise.game.gameObject.weaponProjectile.BulletFriendly;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

public class ChainGun extends Weapon{

    private float originalShootTimer, shootTimerMin, shootSpeedInc;

    public ChainGun(){
        super();

        weaponType = Constants.WEAPON_TYPES.CHAIN_GUN;

        shootTimerReset = 0.3f;
        shootTimerMin   = 0.03f;
        shootSpeedInc   = 0.1f;
        shootTimer = shootTimerReset;
        originalShootTimer = shootTimerReset;
        sprite = DrawingBoard.createSprite("chaingunIcon");
        weaponName = "Chain Gun";

    }

    @Override
    public void fire(Vector2 playerPos, boolean firePressed, BasicLevel basicLevel, float delta) {

        System.out.println(shootTimerReset);

        if(!firePressed) {
            if(shootTimerReset < originalShootTimer)
                shootTimerReset += shootSpeedInc * delta;
            return;
        } else
            shootTimerReset -= shootSpeedInc * delta;

        if(shootTimer <= 0){
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
