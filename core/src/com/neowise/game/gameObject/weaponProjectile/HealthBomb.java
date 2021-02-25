package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.util.RandomUtil;
import com.neowise.game.util.Settings;

import java.util.Collection;

public class HealthBomb extends ParticleBomb{

    public HealthBomb(Vector2 pos) {
        super(pos);
        particleCount = 60;
    }

    @Override
    protected void explode(Collection<WeaponProjectile> tempHostileProjectiles) {
        Health health;
        Vector2 vel = new Vector2(1,1);
        for (int i = 0;i < particleCount;i++){
            vel.setLength(10 + RandomUtil.nextInt(40));
            vel.rotateDeg(RandomUtil.nextInt(360));
            if(Settings.isGWT)
                health = new HealthGWT(pos.cpy(), vel.cpy(),1, 6);
            else
                health = new Health(pos.cpy(), vel.cpy(),1, 6);
            tempHostileProjectiles.add(health);
        }
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(pos.x, pos.y, size);
    }
}
