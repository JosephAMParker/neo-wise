package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class LavaBomb extends ParticleBomb {

    public LavaBomb(Vector2 pos){
        super(pos);
        particleCount = 500;
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.FIREBRICK);
        shapeRenderer.circle(pos.x, pos.y, size);
    }

    @Override
    protected void explode(Collection<WeaponProjectile> tempHostileProjectiles) {
        Lava lava;
        Vector2 vel = new Vector2(1,1);
        for (int i = 0;i < particleCount;i++){
            vel.setLength(10 + RandomUtil.nextInt(80));
            vel.rotateDeg(RandomUtil.nextInt(360));
            lava = new Lava(pos.cpy(), vel.cpy(),8, 1);
            tempHostileProjectiles.add(lava);
        }
    }

    @Override
    public void dispose() {

    }
}
