package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.weaponProjectile.Bomb;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class Orbiter extends ShipCircle {

    Ship orbitTarget;
    Vector2 orbitVector;
    private Boolean selfDestruct;
    private float dropTimer, dropTimerMax;
    Bomb bomb;
    private boolean toRemove;

    public Orbiter(Vector2 pos, Ship orbitTarget, float radius, float angle, float distance) {
        super(pos);

        shipType = Constants.SHIP_TYPES.ORBITER;

        health = 500;
        maxHealth = health;
        this.radius = radius;
        selfDestruct = false;
        this.orbitTarget = orbitTarget;
        orbitVector = new Vector2(1,1);
        orbitVector.setLength(distance);
        orbitVector.setAngleDeg(angle);
        bomb = new Bomb(pos,60, 3, 24, 3);
        bomb.setColor(Color.RED);

        dropTimer = 3 + RandomUtil.nextFloat() * 12;
        dropTimerMax = dropTimer;

        width = radius * 2;
        height = radius * 2;

        reward = 6;
    }

    private void checkAlive(){
        if(bomb.toRemove())
            toRemove = true;
        if(health < 0){
            bomb.toRemove = true;
        }
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {
        checkAlive();
        updateTimers(delta);
        updateTargetPos(delta);
        updatePos(delta);
        checkParent();
        drop(basicLevel.hostileProjectiles);
    }

    private void drop(Collection<WeaponProjectile> hostileProjectiles) {
        if(dropTimer <= 0){
            hostileProjectiles.add(bomb);
            selfDestruct = false;
        }
    }

    private void updateTimers(float delta) {
        if(selfDestruct)
            dropTimer -= delta;
    }

    private void checkParent() {
        if(orbitTarget == null || orbitTarget.toRemove())
            selfDestruct = true;
    }

    public void updateTargetPos(float delta) {
        targetPos = orbitTarget.pos.cpy();
        orbitVector.rotateDeg(delta * 20);
        targetPos.add(orbitVector);
    }

    public void updatePos(float delta) {
        prevPos = pos.cpy();
        pos.lerp(targetPos, (float) (1 - Math.pow(0.005f, delta)));
        rotation = pos.angleDeg() + 90;
        vel = pos.cpy().sub(prevPos);
    }

    @Override
    public boolean toRemove() {
        return toRemove;
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.identity();
        shapeRenderer.setColor(1-(health/maxHealth),0,health/maxHealth,1);
        shapeRenderer.circle(pos.x, pos.y, radius);

        if(dropTimer < dropTimerMax){
            shapeRenderer.setColor(1-(dropTimer/dropTimerMax),0,dropTimer/dropTimerMax,1);
            shapeRenderer.circle(pos.x, pos.y, radius/2);
        }
    }
}
