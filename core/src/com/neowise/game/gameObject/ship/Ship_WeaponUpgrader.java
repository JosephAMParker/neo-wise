package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.squad.Squad;
import com.neowise.game.util.OrbitalAngle;

/**
 * Created by tabletop on 6/9/16.
 */
public class Ship_WeaponUpgrader extends ShipCircle{

    boolean down;
    float downTimer;
    float speedMul;
    float orbitOffset;
    float dts = 60;

    public Ship_WeaponUpgrader(Vector2 pos){
        super(pos);

        down = true;
        mass = 2;
        width = 30;
        height = 20;
        health = 4;
        damage = 50;
        altitude = 999;
        downTimer = dts/2;
        orbitOffset = 250;
        dead = false;
        inSquad = false;
        speedMul = 2;
        oa = new OrbitalAngle(0.0f);
        vel = new Vector2(0,0);

//        animation = new MyAnimation("Ship_WeaponUpgrader",1,pos,rotation,true,5,width);
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {

    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {

    }

    public void updateVel(Vector2 HBPos,Vector2 vel){

        /*
        this.vel    = vel;
        down        = vel.cpy().rotate(-90);
        down.scl(0.03f);

        float h     = pos.dst(HBPos);
        float theta = HBPos.cpy().sub(pos).angle() - vel.angle();
        Vector2 C   = vel.cpy().nor().scl((float) (h*Math.cos(theta)));
        down        = HBPos.cpy().sub(P);
        down.nor().scl(80);
        */

    }

    public void updatePos(Vector2 HBPos){

        if (down){
            orbitOffset += 1f;
        }
        else
            orbitOffset -= 1f;

        Vector2 toPlanet =  (HBPos.cpy()).sub(pos);
        toPlanet.nor();
        rotation = (float) ((90 + toPlanet.angle()) * Math.PI / 180);


        float dis2orbit = pos.dst(HBPos) - orbitOffset;
        float lerp = 0.02f;

        pos.lerp(pos.cpy().add(toPlanet.cpy().scl(dis2orbit)), lerp);

        toPlanet.rotate90(1).scl(0.01f);

        vel.add(toPlanet);
        vel.clamp(0,0.5f);
        pos.add(vel);


    }

    public void updateTimers(float delta){

        downTimer -= delta;

        if(downTimer <= 0) {
            down = !down;
            downTimer = dts;
        }

    }

}
