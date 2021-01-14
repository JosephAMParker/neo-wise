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

    private Boolean onGround;
    private int healCount, healAmount;

    private void initHealth(Vector2 pos, Vector2 vel, int healCount, int healAmount) {
        this.pos = pos;
        this.vel = vel;
        this.healCount  = healCount;
        this.healAmount = healAmount;
        size = 1;
        onGround = false;
    }

    public Health(Vector2 pos, int healCount, int healAmount){
        initHealth(pos, new Vector2(), healCount, healAmount);
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
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(pos.x, pos.y, size);
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {
        HomeBase homeBase = basicLevel.homeBase;
        if (CollisionDetector.collision(pos.x, pos.y, homeBase)) {
            onGround = true;
        }
        updatePos(delta);
        rotateByPlanet((float) homeBase.rotationDelta * delta);
        Physics.Force_Gravity(this, homeBase.getPos(), delta);
        if(CollisionDetector.collision(pos, homeBase.getPos(), homeBase.rotation, 0.9f, homeBase.pixmap))
            heal(homeBase, basicLevel.friendlyTurrets);
    }

    public void jiggle() {
        vel.x = RandomUtil.nextInt(30)-15;
        vel.y = RandomUtil.nextInt(30)-15;
    }

    private void heal(HomeBase homeBase, Collection<Defender> friendlyTurrets) {
        healCount -= 1;
        homeBase.addHealthBombPoints(pos.x, pos.y, 8, 50);
        homeBase.setCheckIntegrity();
        jiggle();
    }

    public void rotateByPlanet(float rotationDelta) {

        if(!onGround)
            return;

        pos.rotateRad(rotationDelta);

    }
}
