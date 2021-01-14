package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.main.BasicLevel;

import java.util.Iterator;

public class LaserBeamFriendly extends LaserBeam{

    public LaserBeamFriendly(Vector2 pos, float damage) {
        super(pos, damage);
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {

        endLaser = pos.cpy().nor().scl(length);

        for(Iterator<Ship> shit = basicLevel.ships.iterator(); shit.hasNext(); ){

            Ship ship = shit.next();
            if (ship.collisionLine(pos, endLaser)) {
                ship.health -= damage;
                length = ship.pos.len() - ship.height/2 + 5;
                endLaser.scl(0.99f);
                basicLevel.shapeRenderer.circle(endLaser.x, endLaser.y, 4);
                return;
            }
        }

        length = originalLength;
    }
}
