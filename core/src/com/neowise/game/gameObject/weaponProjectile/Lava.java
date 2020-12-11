package com.neowise.game.gameObject.weaponProjectile;

import java.util.Collection;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.physics.Physics;

public class Lava extends WeaponProjectile {

	public Lava(float x, float y, int damage) {

		this.pos = new Vector2(x,y);
		this.vel = new Vector2(0,0);
		this.damage = damage;
		mass = 10;
		
	}
	
	public void updatePos(float delta) {
		
		pos.add(vel.cpy().scl(delta));
	}

	public void jiggle(Vector2 HBPos) {

		Random rand = new Random();
		int x = rand.nextInt(3);
		int y = rand.nextInt(3);
		
		vel.x += ((float) (x-1)) / 3;
		vel.y += ((float) (y-1)) / 3;
	}

	@Override
	public boolean toRemove() {
		return damage <= 0;
	}

	@Override
	public void update(float delta, HomeBase homeBase, Collection<Defender> friendlyTurrets, Collection<MyAnimation> animations){
		updatePos(delta);
		Physics.Force_Gravity(this, homeBase.getPos(), delta);
		if(CollisionDetector.collision(pos, homeBase.getPos(), homeBase.rotation, homeBase.pixmap)){
			homeBase.removePointsLava(pos.x, pos.y, 2);
			homeBase.checkIntegrity = true;
			damage -= 1;
			vel = new Vector2(0,0);
			jiggle(homeBase.getPos());
		}
	}

	@Override
	public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(pos.x, pos.y, 1);
	}

	@Override
	public void dispose() {

	}
}
