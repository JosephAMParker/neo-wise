package com.neowise.game.gameObject.player.Weapon;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.weaponProjectile.LaserBeam;
import com.neowise.game.gameObject.weaponProjectile.LaserBeamFriendly;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;

public class LaserBeamGun extends Weapon{

    private LaserBeam laserBeam;

    public LaserBeamGun(Vector2 pos, float damage){
        super();
        weaponType = Constants.WEAPON_TYPES.LASER_BEAM;
        laserBeam = new LaserBeamFriendly(pos, damage);
    }

    @Override
    public void fire(Vector2 playerPos, boolean firePressed, BasicLevel basicLevel) {

        if(firePressed){
            laserBeam.toRemove = false;
            if(!laserBeam.inUse) {
                basicLevel.friendlyProjectiles.add(laserBeam);
                laserBeam.inUse = true;
            }
        } else {
            laserBeam.toRemove = true;
        }

    }

    @Override
    public Vector2 recoil(Vector2 up) {
        return null;
    }

    @Override
    public void updateTimers(float delta) {

    }
}
