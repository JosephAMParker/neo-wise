package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.CityBombTurret;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;

import java.util.Collection;

/**
 * Created by tabletop on 6/19/15.
 */
public class TurretCityBomb extends WeaponProjectile {

    private float distToTarget;
    private float speed;
    public Vector2 startPos,targetPos;
    CityBombTurret turret;

    public TurretCityBomb(CityBombTurret ctb, Vector2 toTarget) {

        turret = ctb;
        targetPos = toTarget.cpy();
        distToTarget = ctb.pos.dst2(toTarget);
        startPos = ctb.pos.cpy();
        this.pos = ctb.pos.cpy();
        mass = 100;
        size = 2;
        this.speed = ctb.speed;
        vel = toTarget.sub(pos).nor().scl(speed);
    }

    public void updatePos(float delta) {

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

    public boolean willDetonate(){
        if(startPos.dst2(pos) > distToTarget) {
            turret.bombActive = false;
            return true;
        }
        return false;
    }
}
