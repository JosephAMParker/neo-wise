package com.neowise.game.gameObject.ship.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.ShipTriangle;
import com.neowise.game.gameObject.weaponProjectile.Bomb;
import com.neowise.game.gameObject.weaponProjectile.Bullet;
import com.neowise.game.gameObject.weaponProjectile.BulletHostile;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class Dart extends ShipTriangle {

    float targetSpeed, targetHeight, diveHeight;
    float angle, dropTimer, dropTimerMax, healthMax;
    int clockwise, shots;
    boolean dropping, toRemove;

    private Bomb bomb;

    public void initDart(float startAngle, float orbitalRange, int clockwise){
        this.width  = 12;
        this.height = 10;
        this.health = 25;
        this.healthMax = health;
        this.damage = 25;
        this.orbitalRange = orbitalRange;
        this.targetSpeed = 0;
        this.clockwise = clockwise;
        targetHeight = 0;
        diveHeight   = 1;
        angle = startAngle;
        dropping = false;
        dropTimer = RandomUtil.nextInt(10)+5;
        dropTimerMax = dropTimer;
        shots = 3;
        bomb = new Bomb(null, null, 3,20,2);
        bomb.setColor(Color.CLEAR);

        initTarget();
    }

    public Dart(Vector2 pos, float orbitalRange, int clockwise) {
        super(pos);
        initDart(0, orbitalRange, clockwise);
    }

    public Dart(Vector2 pos, float orbitalRange, int clockwise, float startAngle) {
        super(pos);
        initDart(startAngle, orbitalRange, clockwise);
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
        fire(basicLevel.hostileProjectiles);
        if(!inSquad)
            attemptToJoinSquad(basicLevel.enemySquads);

    }

    private void fire(Collection<WeaponProjectile> hostileProjectiles) {
        //attach a bomb to the ship by sharing pos and allow the bomb to drop as normal,
        //giving the appearance of the ship going kamikaze
        if(!dropping && dropTimer < 0){
            dropping = true;
            bomb.pos = pos;
            bomb.vel = vel.scl(50);
            hostileProjectiles.add(bomb);
        }
    }

    private void updateOffsetAngle(float delta){
        angle += delta * 1f;
        if(angle > 2*Math.PI)
            angle -= 2*Math.PI;
    }

    private void updateTimers(float delta) {
        dropTimer -= delta;
    }

    /**
     *
     * @param delta
     */
    private void updateTargetPos(float delta){
        targetHeight = (float) Math.cos(angle);
        rotationSpeed = (10 + targetHeight * -1) * clockwise;

        updateOffsetAngle(delta);

        targetPos.rotateDeg(rotationSpeed * delta);
        targetPos.setLength(targetHeight * 25 + orbitalRange + 25 + diveHeight);
    }

    private void updatePos(float delta) {
        prevPos = pos.cpy();
        if(!dropping) {
            pos.lerp(targetPos, (float) (1 - Math.pow(0.1, delta)));
            vel = pos.cpy().sub(prevPos);
        }
        rotation = vel.angleDeg();
    }


    private void initTarget(){
        targetPos = pos.cpy().nor().scl(orbitalRange);
    }

    @Override
    public boolean toRemove() {
        return toRemove;
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.identity();
        shapeRenderer.translate(pos.x, pos.y, 0);
        shapeRenderer.setColor(1-0.3f*health/healthMax,0.1f,health/healthMax,1);
        shapeRenderer.rotate(0, 0, 1, rotation);
        shapeRenderer.triangle(0,0,  -width,   height/2,  - width,  - height/2);
        shapeRenderer.setColor(1-dropTimer/dropTimerMax,dropTimer/dropTimerMax,dropTimer/dropTimerMax,1);
        shapeRenderer.circle(-width * 2 / 3, 0, 2);
        shapeRenderer.circle(-width * 2 / 3, 0, 1);

    }

}
