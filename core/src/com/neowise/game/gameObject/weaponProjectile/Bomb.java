package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.physics.Physics;

import java.util.Collection;
import java.util.Iterator;

public class Bomb extends WeaponProjectile {

	public float explosionSize;
	private boolean toRemove;

	public Bomb(float x, float y, float size, float damage) {
		this.size = size;
		this.damage = damage;
		pos = new Vector2(x,y);
		vel = new Vector2(0,0);
		mass = size*20;
		explosionSize = size * 12;
		toRemove = false;
	}
	
	public void updatePos(float delta) {
		pos.add(vel.cpy().scl(delta));
	}

	@Override
	public void update(float delta, HomeBase homeBase, Collection<Defender> friendlyTurrets, Collection<MyAnimation> animations) {

		updatePos(delta);

		//if bullet collides with homeBase pixmap, remove
		if (CollisionDetector.collision(pos, homeBase.pos, homeBase.rotation, homeBase.pixmap)){

			for(Iterator<Defender> itt = friendlyTurrets.iterator(); itt.hasNext(); ){
				Defender turret = itt.next();
				if (CollisionDetector.collisionCircleCircle(pos.x,pos.y, explosionSize, turret.pos.x,turret.pos.y,turret.size)){
					turret.onGround = false;
					turret.health -= damage;
				}
			}

			homeBase.checkIntegrity = true;
			Vector2 toPlanet = pos.cpy().nor();
			MyAnimation animation = new MyAnimation("bombtest",8, new Vector2(pos.x, pos.y).add(toPlanet.nor().scl(6)),toPlanet.angle()-90,false,0.08f,20);
			animations.add(animation);
			homeBase.removePointsBomb(pos.x, pos.y, 2);

			toRemove = true;
		}

		Physics.Force_Gravity(this, homeBase.getPos(), delta);
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean toRemove() {
		return toRemove;
	}

	@Override
	public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(1, 1, 1, 1);
		shapeRenderer.circle(pos.x, pos.y, size);
	}

	public Vector2 getPos() {
		return pos.cpy();
	}
	

}
