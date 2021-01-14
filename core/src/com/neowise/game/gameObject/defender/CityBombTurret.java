package com.neowise.game.gameObject.defender;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.weaponProjectile.CityBomb;
import com.neowise.game.gameObject.weaponProjectile.CityBombGravity;
import com.neowise.game.gameObject.weaponProjectile.CityBombStraight;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;

import java.util.Collection;

/**
 * Created by tabletop on 6/19/15.
 */
public class CityBombTurret extends Defender {

    float fireTimerMax,fireTimer;
    public boolean hasTarget, bombActive;
    public float range;
    public CityBomb target;
    public float speed;

    public CityBombTurret(Vector2 pos) {

        this.pos = pos;
        speed=2;
        armed = false;
        hasTarget = false;
        onGround = false;
        bombActive = false;

        fireTimerMax = 100;
        fireTimer = 0;
        range = 150;
        size = 3;
        health = 100;

        //animation = new MyAnimation("CityBombTurretNormal",7,pos,rotation,true,fireTimerMax/7,20);
    }

    public void setTarget(CityBomb target){
        this.target = target;
        hasTarget = true;
    }

    public void updateTimer(float delta) {


        fireTimer += delta;

        if (fireTimer >= fireTimerMax && !armed){
            armed = true;
            fireTimer = 0;
        }


    }

    public void loseTarget() {
        target = null;
        hasTarget = false;

    }

    public boolean targetDead(){
        return target.dead;
    }

    public Vector2 getTargetPos() {

        return target.getPos();
    }

    public Vector2 aimAtTarget(Vector2 HBPos, float delta, ShapeRenderer shapeRenderer,float theMod) {

        if(hasTarget) {
            /*float distanceDS = pos.dst(target.getPos());
			Vector2 offset = target.getVel().scl(distanceDS/laserSpeed);
			Vector2 width = new Vector2(target.width/2,0).rotate(target.getRotationDegrees());
			*/ //crazy return explained above

            if(target instanceof CityBombStraight)
                return target.getPos().add(target.getVel().scl(0.75f * pos.dst(target.getPos()) / speed));

            if(target instanceof CityBombGravity){
                // approximate the target by an ellipse similar to its path.
                float theta = (float) (-60 *  Math.PI / 180);


                Vector2 toPlanet = HBPos.cpy().sub(target.startPos.cpy());
                float a = 25;
                float b = 22 + toPlanet.len() / 2;

                Vector2 ellipse = target.startPos.cpy().add(toPlanet.scl(0.5f));
                float time2hit = Math.max(target.startPos.dst(pos)*0.5f,7);
                //float the = (float) (Math.log(((CityBombGravity) target).lifespan) + time2hit);
                float the = target.lifespan + time2hit;

                if (target.lifespan > 60)
                    the = (target.lifespan)/6 + time2hit+28;
                else if (target.lifespan > 27)
                    the = (target.lifespan)/3 + time2hit+18;


                //the -= (theMod);
                the -= 40;

                // System.out.println(" the: " + the);
                //System.out.println(" theMod1: " + theMod);
                //System.out.println("ttl: " + target.lifespan + "\n");

                if(((CityBombGravity) target).left) {
                    theta += (float) (the *  Math.PI / 180);
                    toPlanet.rotate90(1).nor().scl(-9);
                }
                else{
                    theta -= (float) ((the+60) *  Math.PI / 180);
                    toPlanet.rotate90(1).nor().scl(9);
                }

                ellipse.add(toPlanet);

                //shapeRenderer.circle(ellipse.x, ellipse.y, 5);

                float x = (float) (a * Math.cos(theta));
                float y = (float) (b * Math.sin(theta));

                double rot = ellipse.cpy().sub(HBPos).angleRad() + Math.PI / 2;

                for (float t = 1f; t < Math.PI * 2 - 1; t += 0.01f){
                    x = (float) (a * Math.cos(t));
                    y = (float) (b * Math.sin(t));

                    float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
                    float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

                    // shapeRenderer.circle(ellipse.x + x_,ellipse.y + y_, 1);
                }

                x = (float) (a * Math.cos(theta));
                y = (float) (b * Math.sin(theta));

                float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
                float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

                //shapeRenderer.circle(ellipse.x + x_,ellipse.y + y_, 8);


                return  new Vector2(ellipse.x + x_,ellipse.y + y_);

                //float r = b*b / (a - c*Math.cos(theta));


            }
        }
        return new Vector2(0,0);

    }

    @Override
    public void fire(Collection<WeaponProjectile> friendlyProjectiles) {

    }

    @Override
    public void updateTimers(float delta) {

    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {

    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {

    }
}
