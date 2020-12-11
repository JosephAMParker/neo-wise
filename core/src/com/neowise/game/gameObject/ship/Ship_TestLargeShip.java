package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.weaponProjectile.Lava;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.squad.Squad;

import java.util.Collection;

public class Ship_TestLargeShip extends Ship{
	
	boolean swoop;
	float dis2orbit;
	public float lavaTimer,lavaTimerReset;
	int lava;

	public Ship_TestLargeShip(float x, float y) {
		super(x, y);

		resWorth = 80;
		mass = 2;         
		width = 30;       
		height = 20;      
		health = 150;
		damage = 50;
		altitude = 999;
		dead = false;
		inSquad = false;
		oa = new OrbitalAngle(0.0f);
		vel = new Vector2(0,0);
		lava = 0;
		lavaTimerReset = 10;
		lavaTimer = 3;
		targetPos = pos.cpy();
		swoop = false;
		dis2orbit = altitude;
		animation = new MyAnimation("ShipTestShip",1,pos,rotation,true,200/6,width);
	}
	
	@Override
	public void removeFromSquad() {
		
		if (inSquad){
			squad.dead = true;
			squad.removeAllShips();
			inSquad = false;
		}
	}
	
	@Override
	public void joinSquad(Squad sq){
		
		if (!inSquad){
			squad = sq;
			if(sq.fillNextCenterPlace(this))
				inSquad = true;
			else
				squad = null;
		}
	}
	
	@Override
	public void updateTimers(float delta){
		lavaTimer -= delta;
	}

	@Override
	public void updateTargetPos(float delta) {
		if(inSquad) {
			targetPos = squad.pos.cpy().nor();
			targetPos.rotate(sqPlace.angleOffset);
			targetPos.scl(sqPlace.heightOffset + height / 2);
		}
	}

	@Override
	public void updatePos(float delta) {
		prevPos = pos.cpy();
		pos = targetPos.cpy();
		rotation = pos.angle() + 90;
		vel = pos.cpy().sub(prevPos);
	}

	@Override
	public void performAction() {
		if(lavaTimer <= 0) {
			lava = 60;
			lavaTimer += lavaTimerReset;
		}
	}

	@Override
	public void fire(Collection<WeaponProjectile> hostileProjectiles) {
		if(lava > 0){
			hostileProjectiles.add(new Lava(pos.x, pos.y, 2));
			lava--;
		}
	}
}

