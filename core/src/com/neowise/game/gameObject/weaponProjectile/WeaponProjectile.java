package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.GameObject;
import com.neowise.game.main.BasicLevel;

public abstract class WeaponProjectile extends GameObject {

	public int gridPos = -1;
	public float damage;
	public float size, shake;
	public boolean toRemove = false;
	public boolean canDetonate;
	protected Color color;
	
	public Vector2 getPos() {
		return pos.cpy();
	}

	public Vector2 getVel() {
		return vel.cpy();
	}

	public void updatePos(float delta){
		pos.add(vel.cpy().scl(delta));
		if(pos.len2() > 1000000)
			toRemove = true;
	}

	public boolean toRemove(){
		return toRemove;
	}

	public abstract void dispose();

	public abstract void renderShapeRenderer(ShapeRenderer shapeRenderer);

	public abstract void update(BasicLevel basicLevel, float delta);

	public void setColor(Color color){
		this.color = color;
	}
}
