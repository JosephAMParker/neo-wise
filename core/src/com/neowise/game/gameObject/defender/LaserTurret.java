package com.neowise.game.gameObject.defender;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.weaponProjectile.Laser;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;

import java.util.Collection;

public class LaserTurret extends Defender {
	
	float range2, laserSpeed,fireTimerReset,fireTimer;
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
		
		fireTimerReset = 0.25f;
		fireTimer = 1;
		range = 400;
		range2 = range * range;
		size = 4;
		laserSpeed = 200;
		health = 800;

		animation = new MyAnimation("CityBombTurretNormal",7,pos,rotation,true,fireTimerReset/7,size);

	}

	public boolean hasTarget() {
		return target != null;
	}

	public void setTarget(Ship bird) {
		target = bird;
	}

	public void loseTarget() {
		target = null;
	}

	public Vector2 aimAtTarget(float delta) {
		
		if(hasTarget()){
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

	public boolean isArmed() {
		return fireTimer <= 0;
	}

	@Override
	public void fire(Collection<WeaponProjectile> friendlyProjectiles) {
		Laser laser = new Laser(pos.cpy(), toTarget.cpy(), laserSpeed,8);
		friendlyProjectiles.add(laser);
	}

	@Override
	public void updateTimers(float delta) {

		if(fireTimer > 0)
			fireTimer -= delta;
	}

	private void acquireTarget(HomeBase homeBase, Collection<Ship> ships){
		if (!hasTarget() ) {

			//Acquire target
			//TODO goes through this list intelligently... sort by nearest to turret, health left, ship strength etc etc
			for (Ship ship : ships) {

				if(ship.pos.dst2(pos) > range2)
					continue;

				if(CollisionDetector.collisionCircleSquare(pos.x, pos.y, range, ship.pos.x, ship.pos.y, 1)){

					//aimAtBird: Vector pointing to the bird
					toTarget = ship.getPos();
					toTarget.add(ship.getVel().scl(15));

					// if there is a clear line of sight to the bird.. set target
					if(!ship.dead && CollisionDetector.clearLineOfSight(pos,toTarget, homeBase)){
						setTarget(ship);
						break;
					}
				}
			}
		}
	}

	public void attemptToLoseTarget(HomeBase homeBase, float delta){

		if(!hasTarget())
			return;

		//Vector pointing to the ship + offset
		Vector2 toTarget = aimAtTarget(delta);

		//check if target is dead, lose target
		if(targetDead()) {
			loseTarget();
			return;
		}

		//check if target not in sight or out of range, if so lose target
		if(!CollisionDetector.collisionCirclePoint(pos.x, pos.y, range, getTargetPos().x, getTargetPos().y) || !CollisionDetector.clearLineOfSight(pos,toTarget, homeBase))
			loseTarget();
	}

	private void attemptToFire(Collection<WeaponProjectile> friendlyProjectiles){
		if(hasTarget() && isArmed()){
			fire(friendlyProjectiles);
			fireTimer += fireTimerReset;
		}
	}

	@Override
	public void update(BasicLevel basicLevel, float delta) {

		updateTimers(delta);
		HomeBase homeBase = basicLevel.homeBase;
		locateGround2(homeBase, delta);
		rotateByPlanet(homeBase.rotationDelta * delta, homeBase.pos);
		attemptToLoseTarget(homeBase, delta);
		attemptToFire(basicLevel.friendlyProjectiles);
		acquireTarget(homeBase, basicLevel.ships);
	}

	@Override
	public void renderShapeRenderer(ShapeRenderer shapeRenderer) {

		float isArmed = isArmed() ? 0.3f : 0;

		shapeRenderer.setColor(1-(health/700),health/400,health/400,1);
		//shapeRenderer.setColor(0.66f, 0.709f, 0.6f, 1);
		//if(hasTarget()) shapeRenderer.setColor(Color.GREEN);
		//if(isArmed()) shapeRenderer.setColor(Color.FIREBRICK);
		//if(isArmed() && hasTarget) shapeRenderer.setColor(Color.PURPLE);
		shapeRenderer.circle(pos.x, pos.y, size);
	}
}
