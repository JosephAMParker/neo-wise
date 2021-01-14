package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.ScreenShake;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.physics.Physics;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;
import java.util.Iterator;

public class BulletHostile extends Bullet{

    public float explosionSize;
    private Vector2 prevPos;

    public BulletHostile(Vector2 pos, Vector2 dir, float speed, float damage, float size) {
        super(pos, dir, speed, damage, size);
        explosionSize = 5;
        shake = 0.1f;
        prevPos = pos.cpy();
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {

        prevPos = pos.cpy();
        updatePos(delta);

        Vector2 collisionSpot = CollisionDetector.collisionLinePixMap(prevPos, pos, basicLevel.homeBase);

        if(collisionSpot != null){
            explode(collisionSpot, basicLevel.homeBase, basicLevel.friendlyTurrets);
            ScreenShake.addDuration(shake);
            toRemove = true;
        }
    }

    private void explode(Vector2 _pos, HomeBase homeBase, Collection<Defender> friendlyTurrets) {

        explosionSize = damage + RandomUtil.nextInt(5);

        if(CollisionDetector.collisionCircleCircle(_pos, explosionSize, homeBase.core.pos, homeBase.core.radius))
            homeBase.core.causeDamage(damage);

        for(Iterator<Defender> itt = friendlyTurrets.iterator(); itt.hasNext(); ){
            Defender turret = itt.next();
            if (CollisionDetector.collisionCircleCircle(_pos, explosionSize, turret.pos, turret.size)){
                turret.onGround = false;
                turret.health -= damage;
            }
        }

        homeBase.checkIntegrity = true;
        homeBase.removePointsBomb(_pos.x, _pos.y, (int) explosionSize, 0.3f + RandomUtil.nextFloat()/10, true);
    }
}
