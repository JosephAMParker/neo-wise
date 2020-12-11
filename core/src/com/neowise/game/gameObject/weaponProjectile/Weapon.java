package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.math.Vector2;

import java.util.Collection;
import java.util.Random;

public abstract class Weapon {

    Random random;


    public Weapon() {

    }

    public abstract Collection<Bullet> fire(Vector2 playerPos, Vector2 up);

    public abstract Vector2 recoil(Vector2 up);
}
