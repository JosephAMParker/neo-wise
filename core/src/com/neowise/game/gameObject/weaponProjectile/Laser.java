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

    public void aimAt(Vector2 aim){
        vel = aim.sub(pos);
        vel.nor().scl(speed);
    }

    private Vector2 endLaser(){
        return getPos().add(getVel().nor().scl(length));
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {
        updatePos(delta);
        Collection<Ship> ships = basicLevel.ships;
        Vector2 endLaser = endLaser();
        for(Iterator<Ship> shit = ships.iterator(); shit.hasNext(); ){

            Ship ship = shit.next();
            if (ship.collisionLine(pos, endLaser)) {
                ship.health -= damage;
                toRemove = true;
                return;
            }
        }
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.CYAN);
        Vector2 endLaser = endLaser();
        shapeRenderer.rectLine(endLaser.x, endLaser.y,pos.x, pos.y, 3);
    }

    @Override
    public boolean toRemove() {
        return toRemove;
    }

    @Override
    public void dispose() {

    }

}


