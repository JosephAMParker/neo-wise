package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;

import java.util.Collection;

/**
 * Created by tabletop on 6/16/15.
 */
public class PlayerCityBomb extends WeaponProjectile {

    float speed;

    public PlayerCityBomb(Vector2 pos, Vector2 dir, float speed, float damage){

        this.pos = pos;
        this.speed = speed;
        this.damage = damage;
        this.size = 2;
        canDetonate = true;

        vel = dir.cpy().scl(speed);
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {
        updatePos(delta);

    }

    @Override
    public void dispose() {

    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.circle(pos.x, pos.y, size);
    }

}
