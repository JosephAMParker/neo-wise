package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;

import java.util.Collection;

public class Bullet extends WeaponProjectile {

    float speed;

    public Bullet(Vector2 pos, Vector2 dir, float damage, float size) {

        this.pos = pos;
        speed = 8;
        this.damage = damage;
        vel = dir.scl(speed);
        this.size = size;
        this.isAlive = true;
        mass = 1;
    }

    public Bullet(Vector2 pos, Vector2 dir, float speed, float damage, float size) {

        this.pos = pos;
        this.speed = speed;
        this.damage = damage;
        vel = dir.scl(speed);
        this.size = size;
        this.isAlive = true;
        mass = 1;
    }

    public void updatePos(float delta){
        pos.add(vel.cpy().scl(delta));
    }

    @Override
    public void update(float delta, HomeBase homeBase, Collection<Defender> friendlyTurrets, Collection<MyAnimation> animations) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean toRemove() {
        return false;
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {

    }

}
