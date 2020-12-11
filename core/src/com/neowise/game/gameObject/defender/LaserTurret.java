package com.neowise.game.gameObject.defender;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.weaponProjectile.Laser;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;

import java.util.Collection;

public class LaserTurret extends Defender {
	
	float laserSpeed,fireTimerReset,fireTimer;
	public boolean hasTarget;
	private Ship target;
	public float range;
	Vector2 toTarget;
	
	public LaserTurret(Vector2 pos){
		
		this.pos = pos;
		armed = false;
		hasTarget = false;
		toTarget = new Vector2(0,0);
		onGround = false;
		
		fireTimerReset = 0.2f;
		fireTimer = 0;
		range = 200;
		size = 4;
		laserSpeed = 200;
		health = 100;

		animation = new MyAnimation("CityBombTurretNormal",7,pos,rotation,true,fireTimerReset/7,size);

	}



	public void fireLaser() {
		// TODO Auto-generated method stub
		
	}

	public boolean hasTarget() {
		
		return hasTarget;
	}

	public void setTarget(com.neowise.game.gameObject.ship.Ship bird) {
		target = bird;
		hasTarget = true;
	}

	public Ship getTarget() {
		return target;
	}

	public void loseTarget() {
		target = null;
		hasTarget = false;
		
	}

	public Vector2 aimAtTarget(float delta) {
		
		if(hasTarget){
			float distance = pos.dst(target.getPos());
			Vector2 offset = target.getVel().scl(distance/laserSpeed/delta);
			toTarget = target.getPos().add(offset);
			return toTarget;
		}
		
		return new Vector2(0,0);
	}

	public Vector2 getTargetPos() {
		return target.getPos();
	}

	public Vector2 getPos() {
		return pos.cpy();
	}

	public boolean targetDead() {
		return target.dead;
	}

	public void updateTimer(float delta) {

		if(!armed){
			fireTimer -= delta;
			if (fireTimer <= 0 && !armed) {
				setArmed(true);
				fireTimer += fireTimerReset;
			}
		}
	}

	public boolean isArmed() {
		return armed;
	}

	public void setArmed(boolean armed) {
		this.armed = armed;
	}

	@Override
	public void fire(Collection<WeaponProjectile> friendlyProjectiles) {

		Laser laser = new Laser(pos.cpy(), toTarget.cpy(),laserSpeed,8);
		friendlyProjectiles.add(laser);
		setArmed(false);
	}
}
