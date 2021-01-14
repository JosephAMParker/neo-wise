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
	boolean onGround;

	private void initLava(Vector2 pos, int lavaDamage, int damage) {
		this.pos = pos;
		this.lavaDamage = lavaDamage;
		this.damage = damage;
		lavaSize = 8;
		size = 1;
	}

	public Lava(Vector2 pos, int lavaDamage, int damage) {
		initLava(pos, lavaDamage, damage);
		this.vel = new Vector2(0,0);
	}
	public Lava(Vector2 pos, Vector2 vel,  int lavaDamage, int damage) {
		initLava(pos, lavaDamage, damage);
		this.vel = vel;
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
		vel.x = RandomUtil.nextInt(30)-15;
		vel.y = RandomUtil.nextInt(30)-15;
	}

	@Override
	public boolean toRemove() {
		return lavaDamage <= 0;
	}

	@Override
	public void update(BasicLevel basicLevel, float delta) {
		HomeBase homeBase = basicLevel.homeBase;
		if (CollisionDetector.collision(pos.x, pos.y, homeBase)) {
			onGround = true;
		}
		updatePos(delta);
		rotateByPlanet(homeBase.rotationDelta * delta, homeBase.pos);
		Physics.Force_Gravity(this, homeBase.getPos(), delta);
		if(CollisionDetector.collision(pos, homeBase.getPos(), homeBase.rotation, homeBase.pixmap))
			explode(homeBase, basicLevel.friendlyTurrets);
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

		homeBase.removePointsLava(pos.x, pos.y, lavaSize, (int) damage);
		homeBase.checkIntegrity = true;
		lavaDamage -= 1;
		size *= 0.99f;
		size = size < 0.5f ? 0.5f: size;
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
