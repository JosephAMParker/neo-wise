package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.RandomUtil;

public class LaserBeamHostile extends LaserBeam {

    private Vector2 dir;
    private float laserGrowth, radius;
    private float delay, delayReset;

    public LaserBeamHostile(Vector2 pos, Vector2 dir, float damage) {
        super(pos, damage);
        this.dir = dir;
        color = new Color(Color.RED);
        endLaser = pos.cpy().add(dir.nor());
        radius = 6;
        laserGrowth = 100;
        length = 1;
        delayReset = 0.1f;
        delay = delayReset;
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {

        HomeBase homeBase = basicLevel.homeBase;
        basicLevel.shapeRenderer.setColor(color);
        basicLevel.shapeRenderer.circle(endLaser.x, endLaser.y, 8);
        if(delay <= 0) {
            delay = delayReset;
            if (homeBase.removePointsLava(endLaser.x, endLaser.y, 10, 30)) {
                laserGrowth = 10;
                homeBase.setCheckIntegrity();
                dir.rotateDeg(RandomUtil.nextFloat2() * 25 / length);

            } else
                laserGrowth = 100;
        } else
            delay -= delta;


        length += laserGrowth * delta;
        dir.setLength(length);
        endLaser = pos.cpy().add(dir);
    }

    public void rotateByPlanet(float rotationDelta) {
        dir.rotateRad(rotationDelta);
    }
}
