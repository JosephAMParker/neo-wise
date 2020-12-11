package com.neowise.game.gameObject.ship.enemy;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.util.AnimationKeys;
import com.neowise.game.gameObject.weaponProjectile.Bullet;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.squad.Squad_Flock;

import java.util.Collection;

public class Enemy_Dart extends Enemy{

    private boolean dive;
    private float bulletCoolDown, bulletTimer;
    Vector2 ahead, ahead2;
    float maxLookAhead = 200;

    public Enemy_Dart(Vector2 pos) {

        super(pos);
        health = 1000;
        speed = 100;
        dive = false;
        height = 20;
        width = 20;
        animation = new MyAnimation(AnimationKeys.TESTSHIP,1,pos,rotation,true,1/60f,width);
        bulletCoolDown = 0.1f;
        bulletTimer = bulletCoolDown;
        oa = new OrbitalAngle(0.0f);

    }

    @Override
    public void performAction(){

        com.neowise.game.squad.Squad_Flock sq = (com.neowise.game.squad.Squad_Flock) squad;
        if(sq.dive)
            dive = true;
    }

    public void turnAround(){



    }

    /*
        returns the arrive force ,  https://gamedevelopment.tutsplus.com/tutorials/understanding-steering-behaviors-collision-avoidance--gamedev-7777
     */
    private void arrive(){

        Vector2 targetOffset = targetPos.cpy().sub(pos);
        Vector2 desiredVelocity;
        float distance = targetOffset.len();
        targetOffset.nor();

        float slowRad = 20;
        speed = 110;

        if(distance < slowRad)
            desiredVelocity = targetOffset.scl(speed * distance / slowRad);
        else
            desiredVelocity = targetOffset.scl(speed);

        acc = desiredVelocity.sub(vel);
    }

    private void avoidHomeBase(){

        Vector2 avoid = new Vector2();

        ahead = vel.cpy().nor();
        ahead2 = ahead.cpy();

        float dynamicLength = vel.len() / speed;
        ahead  = vel.cpy().nor().scl(dynamicLength).add(pos);
        ahead2 = vel.cpy().nor().scl(dynamicLength/2).add(pos);

        if(ahead.len2() < 2500 || ahead2.len2() < 2500){
            avoid.add(ahead.nor().scl(200));
        }

        acc.add(avoid);
    }

    private Vector2 avoid(){

        ahead = vel.cpy().nor();
        ahead2 = ahead.cpy();

        ahead.scl(maxLookAhead);
        ahead2.scl(maxLookAhead/2);

        //CollisionDetector.collisionCirclePoint();

        return null;
    }

    @Override
    public void updateAcc(float delta) {
        /*
        if(distance > 150)
            speed = distance - 150 + 150;
        else
        */

        if(!impulse.isZero()) {
            acc.add(impulse);
            impulse.scl(0.3f);
            if (impulse.len2() < 10)
                impulse.setZero();
            return;
        }


        float decelBoost = 10;

        arrive();
        avoidHomeBase();

        System.out.print(acc);
        System.out.print("\t");
        System.out.print(vel);
        System.out.print("\t");
        System.out.println(pos);

        if(acc.hasOppositeDirection(vel))
            acc.scl(decelBoost);

    }

    private void rotate(float delta){

        float desiredRotation = look.angle();
        float rotationStep = (desiredRotation - rotation);

        if(rotationStep >= 180)
            rotationStep = rotationStep-360;
        if(rotationStep <= -180)
            rotationStep = -(360+rotationStep);

        if(impulse.isZero())
            rotation += rotationStep * delta * 5;
        else
            rotation -= delta * 150;

        if(rotation > 360)
            rotation -= 360;
        if(rotation < 0)
            rotation += 360;
    }

    public void updatePos(float delta) {

        look = pos.cpy();

        Vector2 newVel = vel.cpy();
        newVel.add(acc.cpy().scl(delta));
        Vector2 averageVel = vel.add(newVel); averageVel.scl(0.5f);
        pos.add(averageVel.scl(delta));
        vel = newVel;

        altitude = pos.len();
        pos = targetPos.cpy();

        look.sub(pos);
        rotate(delta);
        //rotation = look.angle();

    }

    private float getHorizontalOffset(){
        return ((com.neowise.game.squad.Squad_Flock.Flock_SquadPlace) sqPlace).horizontalOffset;
    }

    @Override
    public void updateTargetPos(float delta){
        com.neowise.game.squad.Squad_Flock sq = (com.neowise.game.squad.Squad_Flock) squad;
        float targetAltitude = sq.getTargetAltitude((com.neowise.game.squad.Squad_Flock.Flock_SquadPlace) sqPlace);
        float horizontalOffset = getHorizontalOffset();

        //adjust vertical offset
        targetPos = sq.pos.cpy();
        targetPos.rotate(horizontalOffset);
        Vector2 toPlanet = targetPos.cpy().nor();
        targetPos.add(toPlanet.scl(targetAltitude));
    }

    @Override
    public void updateTimers(float delta){
        if (bulletTimer > 0)
            bulletTimer -= delta;
    }

    @Override
    public void fire(Collection<WeaponProjectile> hostileProjectiles){
        com.neowise.game.squad.Squad_Flock sq = (Squad_Flock) squad;
        if(altitude < 200 && sq.diving){
            if(bulletTimer <= 0){
                Bullet bb = new Bullet(pos.cpy(), look.cpy().nor().rotate(-14), 180f,1,1);
                bulletTimer = bulletCoolDown;
                hostileProjectiles.add(bb);
            }
        }
    }
}
