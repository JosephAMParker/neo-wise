package com.neowise.game.gameObject.player.Weapon;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.weaponProjectile.Bullet;
import com.neowise.game.gameObject.weaponProjectile.BulletFriendly;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;
import java.util.Collection;

public class ShotGun extends Weapon {

    public ShotGun(){
        super();
        shootTimerReset = 0.33f;
        shootTimer = shootTimerReset;
        weaponType = Constants.WEAPON_TYPES.FLAK;
    }

    @Override
    public void fire(Vector2 playerPos, boolean firePressed, BasicLevel basicLevel) {

        if(shootTimer > 0 || !firePressed)
            return;

        shootTimer += shootTimerReset;

        Collection<WeaponProjectile> friendlyProjectiles = basicLevel.friendlyProjectiles;
        Vector2 up = playerPos.cpy().nor();

        for(int i=0;i<power*2 + 4;i++){
            Bullet bullet = new BulletFriendly(playerPos.cpy(), up.cpy().rotateDeg((RandomUtil.nextFloat()-0.5f) * 15f), 400 + RandomUtil.nextFloat() * 50,20,1);
            friendlyProjectiles.add(bullet);
        }
    }

    @Override
    public Vector2 recoil(Vector2 up){
        return up.cpy().scl(-100);
    }

    @Override
    public void updateTimers(float delta) {
        if(shootTimer > 0)
            shootTimer -= delta;
    }
}
