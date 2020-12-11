package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Weapon_ShotGun extends Weapon {

    public Weapon_ShotGun() {
        super();

        random = new Random();
    }

    public Collection<Bullet> fire(Vector2 playerPos, Vector2 up) {

        Collection<Bullet> friendlyProjectiles = new ArrayList<Bullet>();

        for(int i=0;i<8;i++){
            Bullet bullet = new Bullet(playerPos.cpy(), up.cpy().rotate((random.nextFloat()-0.5f) * 15f), 400 + random.nextFloat() * 50,20,1);
            friendlyProjectiles.add(bullet);
        }

        return friendlyProjectiles;
    }

    public Vector2 recoil(Vector2 up){
        return up.scl(-100);
    }
}
