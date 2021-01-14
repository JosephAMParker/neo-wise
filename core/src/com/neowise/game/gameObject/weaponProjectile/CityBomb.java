package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;

import java.util.Collection;

/**
 * Created by tabletop on 6/16/15.
 */
public abstract class CityBomb extends WeaponProjectile {

    public float explosionSize;
    float speed;
    public Vector2 startPos;
    public boolean dead;
    public int resWorth;
    public boolean targeted;
    public float lifespan, immune;

    public CityBomb(Vector2 pos) {
        startPos = pos.cpy();
        lifespan = 0;
        this.pos = pos;
        targeted = false;
        this.size = 2;
        resWorth = 10;
        speed = 1.1f;
        this.explosionSize = 20;
        dead = false;
        this.damage = 50;
        this.mass = 10;
        immune = 0;

    }

    @Override
    public abstract void update(BasicLevel basicLevel, float delta);

    @Override
    public abstract void dispose();

    @Override
    public abstract void renderShapeRenderer(ShapeRenderer shapeRenderer);
}
