package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.gameObject.defender.OrbiterTurret;
import com.neowise.game.homeBase.HomeBase;

import java.util.Collection;

public class PlayerOrbiterBomb extends WeaponProjectile {
	
	public float explosionSize;
	public int turretID;
	public OrbiterTurret turret;

	public PlayerOrbiterBomb(OrbiterTurret turret, Vector2 HBPos, float x, float y, int size, int damage) {
		
		this.size = size;
		this.mass = size*20;
		this.damage = damage;
		this.pos = new Vector2(x,y);
		this.turret = turret;
		explosionSize = size * 12;
		
		Vector2 toPlanet = (HBPos).sub(pos);
		float dist = toPlanet.len();
		toPlanet.nor().rotate(90f).scl(dist*0.04f);
		
		vel = toPlanet;
		
	}
	
	public void updatePos(Vector2 HBPos, float delta) {
		
		float dist = pos.dst2(HBPos);
		if(dist < 29000){
			Vector2 toPlanet = (HBPos).sub(pos);
			
			toPlanet.nor().scl(delta * (29000 - dist) / 20000);  
			pos.sub(toPlanet);
		}
		
		pos.add(vel.cpy().scl(delta));
	}

	@Override
	public void update(float delta, HomeBase homeBase, Collection<Defender> friendlyTurrets, Collection<MyAnimation> animations) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean toRemove() {
		return false;
	}

	@Override
	public void renderShapeRenderer(ShapeRenderer shapeRenderer) {

	}
}
