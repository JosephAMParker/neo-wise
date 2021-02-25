package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.pickup.WeaponUpgrade;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

public class WeaponUpgradeShip extends ShipCircle {

    public WeaponUpgradeShip(Vector2 pos, BasicLevel basicLevel) {
        super(pos);

        shipType = Constants.SHIP_TYPES.UPGRADE_SHIP;

        this.health = 10;
        this.radius = 4;
        this.orbitalRange = orbitalRange;
        width = radius * 2; height = radius * 2;

        this.basicLevel = basicLevel;
        this.orbitalRange = basicLevel.homeBase.enemyOrbitRange;
        setNewTarget();
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {
        updatePos(delta);
    }

    public void updatePos(float delta) {
        pos.lerp(targetPos, (float) (1-Math.pow(0.1f, delta)));
        float dis2Target = pos.dst2(targetPos);
        rotation = pos.angleDeg() + 90;

        if(dis2Target < 100) {
            setNewTarget();
        }
    }

    private void setNewTarget() {
        targetPos = pos.cpy().nor();
        targetPos.scl(orbitalRange + RandomUtil.nextInt(50));
        targetPos.rotateDeg(RandomUtil.nextInt(80) - 40);
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.identity();
        shapeRenderer.translate(pos.x, pos.y, 0);
        shapeRenderer.setColor(Color.GOLDENROD);
        shapeRenderer.rotate(0, 0, 1, rotation);
        shapeRenderer.circle(0,0, radius);
    }

    @Override
    public void kill(BasicLevel basicLevel) {
        super.kill(basicLevel);
        this.basicLevel.powerUps.add(new WeaponUpgrade(pos.cpy(), this.basicLevel));
    }
}
