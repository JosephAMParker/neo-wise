package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.weaponProjectile.Bomb;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.squad.Squad;

import java.util.Collection;
import java.util.Random;

public class Ship_TestShip extends Ship {

	float dis2orbit;
	float speedMul;
	float targetSpeed;
	float targetTimer;
	float bombTimer, bombTimerReset;

	Random random;

	public Ship_TestShip(float x, float y, float speedMul) {
		super(x, y);

		random = new Random();
		resWorth = 50;
		mass = 6;
		width = 12;
		height = 10;
		health = 50;
		damage = 25;
		altitude = 999;
		bombTimerReset = 8;
		bombTimer = random.nextFloat()*bombTimerReset+5; //start with longer timer.
		dead = false;
		inSquad = false;
		oa = new OrbitalAngle(0.0f);
		vel = new Vector2(0,0);
		dis2orbit = altitude;
		this.speedMul = speedMul;
		this.targetSpeed = speedMul;
		targetTimer = random.nextFloat() * 25;
		rotationSpeed = 0;
		targetPos = pos.cpy();
		setNewTarget();

		animation = new MyAnimation("ShipTestShip",1,pos,rotation,true,200/6,width);

	}

	private void setNewTarget() {
		targetPos = pos.cpy().nor();
		targetPos.scl(100 + random.nextInt(25));
		targetSpeed = (4 + random.nextFloat())*4;
		targetSpeed = random.nextBoolean() ? targetSpeed : -targetSpeed;
	}

	@Override
	public void performAction() {
		if(!inSquad) {
			if (targetTimer <= 0) {
				targetTimer += random.nextFloat() * 10;
				setNewTarget();
			}
		}
	}

	@Override
	public void updateTimers(float delta){
		bombTimer -= delta;
		if(!inSquad)
			targetTimer -= delta;
	}

	@Override
	public void removeFromSquad() {

		if (inSquad){
			squad.removeShipFromSquad(this);
			inSquad = false;
			if(!squad.dead)
				squad.reorganizeZone(sqPlace.zone);
		}
	}

	@Override
	public void joinSquad(Squad sq){

		if (!sq.dead && !inSquad && !sq.full()){
			if (sq.fillNextSidePlace(this)) {
				inSquad = true;
				squad = sq;
			}
		}
	}

	@Override
	public void updateTargetPos(float delta){

		if(inSquad) {
			targetPos = squad.pos.cpy().nor();
			targetPos.rotate(sqPlace.angleOffset);
			targetPos.scl(sqPlace.heightOffset + height / 2);
		} else {
			targetPos.rotate(targetSpeed * delta);
		}
	}

	@Override
	public void updatePos(float delta) {
		prevPos = pos.cpy();
		pos.lerp(targetPos, (float) (1-Math.pow(0.1, delta)));
		rotation = pos.angle() + 90;
		vel = pos.cpy().sub(prevPos);
	}

	@Override
	public void fire(Collection<WeaponProjectile> hostileProjectiles) {
		if(bombTimer <= 0) {
			bombTimer += bombTimerReset + random.nextFloat()*bombTimerReset;
			Bomb bomb = new Bomb(pos.x, pos.y, 1, damage);
			hostileProjectiles.add(bomb);
		}
	}
}

