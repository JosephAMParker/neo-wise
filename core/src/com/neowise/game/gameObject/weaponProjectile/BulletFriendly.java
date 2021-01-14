package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;

import java.util.Collection;
import java.util.Iterator;

public class BulletFriendly extends Bullet{

    public BulletFriendly(Vector2 pos, Vector2 dir, float speed, float damage, float size) {
        super(pos, dir, speed, damage, size);
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {
        updatePos(delta);

        Collection<Ship> ships = basicLevel.ships;

        for(Iterator<Ship> shit = ships.iterator(); shit.hasNext(); ){

            Ship ship = shit.next();
            if(ship.collisionPoint(pos)){
                ship.health -= damage;
                toRemove = true;
                return;
            }
        }
    }
}
