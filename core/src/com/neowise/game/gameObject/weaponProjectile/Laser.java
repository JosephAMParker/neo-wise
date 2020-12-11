package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;

import java.util.Collection;

public class Laser extends WeaponProjectile {

    float speed;
    public float length = 8f;

    public Laser(Vector2 pos, Vector2 aim, float speed, int damage) {
        this.damage = damage;
        this.pos = pos;
        this.speed = speed;
        mass = 0;
        aimAt(aim);
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

    public void aimAt(Vector2 aim){
        vel = aim.sub(pos);
        vel.nor().scl(speed);
    }

    public Vector2 getPos() {
        return pos.cpy();
    }

}


