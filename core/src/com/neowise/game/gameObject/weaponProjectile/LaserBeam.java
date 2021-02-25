package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;

import java.util.Iterator;

public abstract class LaserBeam extends WeaponProjectile{

    public float length;
    protected float originalLength;
    protected Color color;
    protected Vector2 endLaser;
    public boolean inUse;

    public LaserBeam(Vector2 pos, float damage){
        this.pos = pos;
        this.damage = damage;
        length = 1000;
        originalLength = length;
        inUse = false;
        color = Color.LIME;
    }

    @Override
    public void dispose() {
        inUse = false;
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(pos, endLaser, 3);
    }

}
