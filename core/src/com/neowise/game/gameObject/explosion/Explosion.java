package com.neowise.game.gameObject.explosion;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.player.Weapon.Weapon;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.weaponProjectile.CityBomb;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by tabletop on 5/28/15.
 */
public class Explosion {

    public Vector2 pos;
    public float size,maxsize, damage;
    public float duration;

    public  Explosion (Vector2 pos, float size, float damage, float duration)
    {
        this.damage = damage;
        this.duration = duration;
        this.pos = pos;
        this.maxsize = size;
        this.size = 1;
    }

    public void updateTimers(float delta){
        duration-=delta;
    }

    public void updateSize(float delta) {

        if(size < maxsize)
            size+=2;
    }

    public void collides(Collection<WeaponProjectile> hostileProjectiles, Collection<Ship> ships) {
        for (Iterator<WeaponProjectile> cit = hostileProjectiles.iterator(); cit.hasNext(); ){
            WeaponProjectile projectile = cit.next();
            if (CollisionDetector.collisionCirclePoint(pos.x,pos.y,size, projectile.pos.x,projectile.pos.y))
                projectile.toRemove = true;
        }

        for (Iterator<Ship> shit = ships.iterator(); shit.hasNext(); ){
            Ship ship = shit.next();
            if(ship.collisionCircle(pos, size)){
                ship.health -= damage;
            }

        }
    }

    public void update(BasicLevel basicLevel, float delta) {

        updateTimers(delta);
        updateSize(delta);
        collides(basicLevel.hostileProjectiles, basicLevel.ships);
    }
}
