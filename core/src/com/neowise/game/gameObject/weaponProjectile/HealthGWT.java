package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.physics.Physics;
import com.neowise.game.util.RandomUtil;

public class HealthGWT extends Health {

    @Override
    protected void initHealth(Vector2 pos, Vector2 vel, int healCount, int healAmount) {
        super.initHealth(pos, vel, healCount, healAmount);

        this.healCount = 15;
        this.healAmount = 210;
        healRadius = 5 + RandomUtil.nextInt(2);
    }

    public HealthGWT(Vector2 pos, Vector2 vel, int healCount, int healAmount) {
        super(pos, vel, healCount, healAmount);
        initHealth(pos, vel, healCount, healAmount);
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {

        HomeBase homeBase = basicLevel.homeBase;

        updatePos(delta);

        rotateByPlanet((float) homeBase.rotationDelta * delta);

        if(!hasHit)
            Physics.Force_Gravity(this, homeBase.getPos(), delta);


        if(CollisionDetector.collisionPointPixmap(pos, homeBase, 200)) {
            firstHit = true;
            onGround = true;
        }

        if(firstHit) {
            digCounter -= delta;
            if (digCounter <= 0)
                hasHit = true;
        }

        if(hasHit) {
            healCounter -= delta;
            if(healCounter <= 0){
                heal(homeBase, basicLevel.friendlyTurrets);
                healCounter = healCounterReset;
                healAmount -= 15;
            }
        }
    }
}
