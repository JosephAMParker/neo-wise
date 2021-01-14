package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;

import java.util.Collection;
import java.util.Iterator;

public abstract class Bullet extends WeaponProjectile {

    float speed;

    public Bullet(Vector2 pos, Vector2 dir, float speed, float damage, float size) {

        this.pos = pos;
        this.speed = speed;
        this.damage = damage;
        vel = dir.scl(speed);
        this.size = size;
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(pos.x, pos.y, size);
    }

    @Override
    public void dispose() {

    }

}
