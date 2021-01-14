package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.Physics;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public abstract class ParticleBomb extends WeaponProjectile {

    protected int particleCount;

    public ParticleBomb(Vector2 pos){
        this.pos = pos;
        this.vel = new Vector2();
        size = 6;
        particleCount = 1;
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {

        updatePos(delta);

        HomeBase homeBase = basicLevel.homeBase;
        Physics.Force_Gravity(this, homeBase.getPos(), delta);

        if(pos.len() < homeBase.playerOrbitRange){
            explode(basicLevel.tempHostileProjectiles);
            toRemove = true;
        }
    }

    protected abstract void explode(Collection<WeaponProjectile> tempHostileProjectiles);

    @Override
    public void dispose() {

    }

}
