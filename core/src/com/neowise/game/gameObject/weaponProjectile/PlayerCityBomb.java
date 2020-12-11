package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;

import java.util.Collection;

/**
 * Created by tabletop on 6/16/15.
 */
public class PlayerCityBomb extends WeaponProjectile {

    float speed;

    public PlayerCityBomb(Vector2 pos, Vector2 target){

        this.pos = pos;

        speed = 8;
        this.size = 2;
        this.damage = 10;

        vel = target.sub(pos).nor().scl(speed);
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
