package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.util.RandomUtil;

public class LaserBeamHostile extends LaserBeam {

    private Vector2 dir;
    private float laserGrowth, radius;
    private float delay, delayReset, hitTimer, hitTimerLimit;
    boolean firstHit;

    public LaserBeamHostile(Vector2 pos, Vector2 dir, float damage) {
        super(pos, damage);
        this.dir = dir;
        color = new Color(Color.RED);
        endLaser = pos.cpy().add(dir.nor());
        radius = 6;
        laserGrowth = 60;
        length = 1;
        delayReset = 0.08f;
        delay = delayReset;
        hitTimerLimit = 0.5f;
        hitTimer = hitTimerLimit;
        firstHit = false;
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {

        HomeBase homeBase = basicLevel.homeBase;
        basicLevel.shapeRenderer.setColor(color);
        basicLevel.shapeRenderer.circle(endLaser.x, endLaser.y, 4);
        //homeBase.removePointsLava(endLaser.x, endLaser.y, 20);
        if(delay <= 0) {
            delay = delayReset;
            if (CollisionDetector.collisionPointPixmap(endLaser, homeBase)) {
                firstHit = true;
                homeBase.removePointsBomb(endLaser.x, endLaser.y,10, 0, false);
                laserGrowth = 0;
                dir.rotateDeg(RandomUtil.nextFloat2() * 25 / length);
                hitTimer = 0;
                homeBase.setCheckIntegrity();
                spawnLava(basicLevel);
            } else {
                laserGrowth = 5;
                hitTimer += delta;
                if(hitTimer > hitTimerLimit)
                    laserGrowth = 150;
                if(!firstHit)
                    laserGrowth = 60;
            }
        } else
            delay -= delta;


        length += laserGrowth * delta;
        dir.setLength(length);
        endLaser = pos.cpy().add(dir);
    }

    private void spawnLava(BasicLevel basicLevel) {
        for(int i = 0; i < 10; i++){
            int v = 45;
            Vector2 vel = new Vector2(RandomUtil.nextInt(v) - v/2, RandomUtil.nextInt(v) - v/2);
            basicLevel.tempHostileProjectiles.add(new Lava(endLaser.cpy(), vel, 0, 4 + RandomUtil.nextFloat() * 2, false));
        }
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(pos, endLaser, 8);
        shapeRenderer.circle(pos.x, pos.y, 4);
    }

    public void rotateByPlanet(float rotationDelta) {
        dir.rotateRad(rotationDelta);
    }
}
