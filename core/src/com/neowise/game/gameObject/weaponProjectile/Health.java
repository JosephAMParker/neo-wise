package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.physics.Physics;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class Health extends WeaponProjectile {

    protected Boolean onGround = false;
    protected int healCount, healAmount, healRadius;
    protected float digCounter, healCounter, healCounterReset;
    protected boolean hasHit, firstHit;

    protected void initHealth(Vector2 pos, Vector2 vel, int healCount, int healAmount) {
        this.pos = pos;
        this.vel = vel;
        this.healCount  = healCount;
        this.healAmount = healAmount;
        this.size = 1;
        healCounterReset = 0.1f;
        healCounter = 0;
        digCounter = RandomUtil.nextFloat() * 0.041f;
        hasHit = false;
        firstHit = false;

        this.healCount = 10;
        this.healAmount = 10;
        healRadius = 10 + RandomUtil.nextInt(2);
    }

    public Health(Vector2 pos, Vector2 vel, int healCount, int healAmount){
        initHealth(pos, vel, healCount, healAmount);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean toRemove() {
        return healCount <= 0;
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        if(!firstHit) {
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.circle(pos.x, pos.y, size);
        }
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {
        HomeBase homeBase = basicLevel.homeBase;
        updatePos(delta);
        rotateByPlanet((float) homeBase.rotationDelta * delta);
        Physics.Force_Gravity(this, homeBase.getPos(), delta);
        if(CollisionDetector.collisionPointPixmap(pos, homeBase, 80)) {
            heal(homeBase, basicLevel.friendlyTurrets);
            onGround = true;
            firstHit = true;
        }
    }

    public void jiggle() {
        Vector2 r = pos.cpy().nor().scl(-1);
        vel = r.cpy().scl(RandomUtil.nextInt(10) + 10);
        vel.x = RandomUtil.nextInt(50)-25;
        vel.y = RandomUtil.nextInt(50)-25;
    }

    protected void heal(HomeBase homeBase, Collection<Defender> friendlyTurrets) {
        healCount -= 1;
        homeBase.addHealthBombPoints(pos.x, pos.y, healRadius, healAmount);
        homeBase.setCheckIntegrity();
        jiggle();
    }

    public void rotateByPlanet(float rotationDelta) {

        if(!onGround)
            return;

        pos.rotateRad(rotationDelta);

    }
}
