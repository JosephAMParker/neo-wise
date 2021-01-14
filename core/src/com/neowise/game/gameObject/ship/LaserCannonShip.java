package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.weaponProjectile.LaserBeamHostile;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class LaserCannonShip extends ShipCircle{

    private float shootTimer, shootTimerReset;
    private float dis2Target, prevDist;
    private LaserBeamHostile laser;
    private Boolean firingPosition;

    public LaserCannonShip(Vector2 pos, float radius, float orbitalRange) {

        super(pos);
        this.radius = radius;
        this.orbitalRange = orbitalRange;
        health = 2000;
        maxHealth = health;

        width = radius * 2;
        height = radius * 2;
        firingPosition = false;

        laser = new LaserBeamHostile(getShootPos(), new Vector2(), 0);

        shootTimerReset = 1;
        shootTimer = shootTimerReset;

        setNewTarget();

        dis2Target = pos.dst2(targetPos);
        prevDist = 0;
    }

    private Vector2 getShootPos(){
        return pos.cpy().add(pos.cpy().nor().scl(-radius/2.5f));
    }

    private void setNewTarget() {
        targetPos = pos.cpy().nor();
        targetPos.scl(orbitalRange + RandomUtil.nextInt(25));
        targetPos.rotateDeg(RandomUtil.nextInt(60) - 30);
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {
        updateTimers(delta);
        updatePos(delta);
        rotateByPlanet((float) basicLevel.homeBase.rotationDelta * delta);
        fire(basicLevel.hostileProjectiles);
    }

    private void resetLaser(){

        Vector2 laserDir = pos.cpy().nor().scl(-1);
        laserDir.rotateDeg((5 + RandomUtil.nextInt(15)) * RandomUtil.nextOne());
        laser = new LaserBeamHostile(getShootPos(), laserDir, 10);

    }

    private void fire(Collection<WeaponProjectile> hostileProjectiles) {

        if(shootTimer < 0){
            if(!laser.inUse){
                hostileProjectiles.add(laser);
                laser.inUse = true;
            }
        }

        if(firingPosition && laser.length > 600) {
            firingPosition = false;
            laser.toRemove = true;
            laser.inUse    = false;
            shootTimer = shootTimerReset;
            setNewTarget();
        }

        if(laser.inUse)
            laser.pos = getShootPos();

    }

    private void updateTimers(float delta){
        if(firingPosition && shootTimer > 0)
            shootTimer -= delta;
    }

    public void updatePos(float delta) {

        prevDist = dis2Target;
        pos.lerp(targetPos, (float) (1-Math.pow(0.7f, delta)));
        dis2Target = pos.dst2(targetPos);

        rotation = pos.angleDeg() + 90;

        if(!firingPosition && (prevDist - dis2Target) < 1) {
            firingPosition = true;
            resetLaser();
        }
    }

    public void rotateByPlanet(float rotationDelta) {

        targetPos.rotateRad(rotationDelta);
        laser.rotateByPlanet(rotationDelta);

    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.identity();
        shapeRenderer.translate(pos.x, pos.y, 0);
        shapeRenderer.setColor(1-(health/maxHealth),0,health/maxHealth,1);
        shapeRenderer.rotate(0, 0, 1, rotation);
        shapeRenderer.circle(0,0, radius);
        shapeRenderer.setColor(1-(shootTimer/shootTimerReset),0,shootTimer/shootTimerReset,1);
        shapeRenderer.circle(0, radius/2.5f, radius/4);
    }

    @Override
    public void kill() {
        super.kill();
        laser.toRemove = true;
        laser.inUse    = false;
    }
}
