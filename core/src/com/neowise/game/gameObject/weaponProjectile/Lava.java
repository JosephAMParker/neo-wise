package com.neowise.game.gameObject.weaponProjectile;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.physics.Physics;
import com.neowise.game.util.RandomUtil;

public class Lava extends WeaponProjectile {

	int lavaSize;
	int lavaDamage;
	boolean onGround, gravity;
	float hitDelay, hitDelayReset, lifeSpan;

	private void initLava(Vector2 pos, int lavaDamage, float lifeSpan) {
		this.pos = pos;
		this.lavaDamage = lavaDamage;
		this.lifeSpan = lifeSpan;
		this.gravity = true;
		lavaSize = 12;
		size = 2;
		damage = 1;

		hitDelayReset = 0.8f;
		hitDelay = 0;
	}

	public Lava(Vector2 pos, int lavaDamage, float lifeSpan) {
		initLava(pos, lavaDamage, lifeSpan);
		this.vel = new Vector2(0,0);
	}

	public Lava(Vector2 pos, Vector2 vel,  int lavaDamage, float lifeSpan) {
		initLava(pos, lavaDamage, lifeSpan);
		this.vel = vel;
	}

	public Lava(Vector2 pos, Vector2 vel,  int lavaDamage, float lifeSpan, boolean gravity) {
		initLava(pos, lavaDamage, lifeSpan);
		this.vel = vel;
		this.gravity = gravity;
	}

	/**
	 * rotates the turret in space to match the rotation of the planet its resting on
	 */
	public void rotateByPlanet(double rotationDelta, Vector2 PlanetPos) {

		if(!onGround)
			return;

		pos.sub(PlanetPos);
		Vector2 newPos = new Vector2();
		newPos.x  = (float) (pos.x * Math.cos(rotationDelta - Math.PI*2) - pos.y  * Math.sin(rotationDelta - Math.PI*2));
		newPos.y = (float) (pos.x * Math.sin(rotationDelta - Math.PI*2) + pos.y * Math.cos(rotationDelta - Math.PI*2));
		pos = newPos.add(PlanetPos);

	}
	
	public void updatePos(float delta) {
		pos.add(vel.cpy().scl(delta));
	}

	public void jiggle() {
		vel.x = RandomUtil.nextInt(20)-10;
		vel.y = RandomUtil.nextInt(20)-10;
	}

	@Override
	public boolean toRemove() {
		return lifeSpan <= 0;
	}

	private void updateTimers(float delta){
		if(hitDelay > 0)
			hitDelay -= delta;
		lifeSpan -= delta;
	}
	@Override
	public void update(BasicLevel basicLevel, float delta) {

		updateTimers(delta);

		HomeBase homeBase = basicLevel.homeBase;
		if (CollisionDetector.collisionPointPixmap(pos, homeBase)) {
			onGround = true;
		}
		updatePos(delta);
		rotateByPlanet(homeBase.rotationDelta * delta, homeBase.pos);

		if(gravity)
			Physics.Force_Gravity(this, homeBase.getPos(), delta);

		if(CollisionDetector.collisionPointPixmap(pos, homeBase))
			explode(homeBase, basicLevel.friendlyTurrets);

		size *= 0.99f;
		size = Math.max(size, 0.5f);
	}

	private void explode(HomeBase homeBase, Collection<Defender> friendlyTurrets){

		if(pos.len() < homeBase.core.radius + lavaSize)
			homeBase.core.causeDamage(damage);

		for(Iterator<Defender> itt = friendlyTurrets.iterator(); itt.hasNext(); ){
			Defender turret = itt.next();
			if (CollisionDetector.collisionCircleCircle(pos, lavaSize, turret.pos, turret.size)){
				turret.onGround = false;
				turret.health -= damage;
			}
		}

		if(hitDelay <= 0) {
			if(lavaDamage > 0)
				homeBase.removePointsLava(pos.x, pos.y, lavaSize);
			lavaDamage -= 1;
			hitDelay = hitDelayReset;
		}
		jiggle();
	}

	@Override
	public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(pos.x, pos.y, size);
	}

	@Override
	public void dispose() {

	}
}
