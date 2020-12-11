package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.GameObject;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;

import java.util.Collection;

public abstract class WeaponProjectile extends GameObject {

	public int gridPos = -1;
	public float damage;
	public float size;
	public boolean isAlive;
	
	public Vector2 getPos() {
		return pos.cpy();
	}

	
	public Vector2 getVel() {
		return vel.cpy();
	}

	public void updatePos(float delta) {
		pos.add(vel.cpy().scl(delta));
	}

	public abstract void update(float delta,
								HomeBase homeBase,
								Collection<Defender> friendlyTurrets,
								Collection<MyAnimation> animations
								);
	
	public void updateVel(float delta){
		
	}

	public abstract void dispose();

	public abstract boolean toRemove();

	public abstract void renderShapeRenderer(ShapeRenderer shapeRenderer);


}
