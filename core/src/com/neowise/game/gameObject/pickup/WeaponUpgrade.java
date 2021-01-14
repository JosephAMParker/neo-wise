package com.neowise.game.gameObject.pickup;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.pickup.PowerUp;
import com.neowise.game.gameObject.player.PlayerShip;
import com.neowise.game.gameObject.weaponProjectile.HealthBomb;
import com.neowise.game.main.BasicLevel;

/**
 * Created by tabletop on 6/14/16.
 */
public class WeaponUpgrade extends PowerUp {

    public float size;
    boolean inc, collected;

    public WeaponUpgrade(Vector2 pos, BasicLevel basicLevel){
        super(basicLevel);
        this.pos = pos;
        this.vel = pos.cpy().nor().scl(-20);
        this.size = 3;
        inc = true;
        collected = false;
    }

    public void updateSize(float delta){

        if (inc){
            if (size > 7)
                inc = false;
            else
                size += 10 * delta;
        }

        else{
            if (size < 3)
                inc = true;
            else
                size -= 10 * delta;
        }

    }

    @Override
    public boolean toRemove() {
        return collected;
    }

    @Override
    public void update(float delta) {
        updateSize(delta);
        if(pos.len() > basicLevel.homeBase.playerOrbitRange)
            updatePos(delta);
        collectByPlayer(basicLevel.playerShip);
        renderShapeRenderer(basicLevel.shapeRenderer);
    }

    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.identity();
        shapeRenderer.translate(pos.x, pos.y, 0);
        shapeRenderer.setColor(Color.GOLDENROD);
        shapeRenderer.circle(0,0, size);
    }

    private void collectByPlayer(PlayerShip playerShip) {

        if(playerShip.pos.dst2(pos) < 100){
            collected = true;
            playerShip.currentWeapon.upgrade(this);
            basicLevel.friendlyProjectiles.add(new HealthBomb(pos.cpy()));
        }

    }
}
