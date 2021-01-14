package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.draw.ScreenShake;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.physics.Physics;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;
import java.util.Iterator;

public class Bomb extends WeaponProjectile {

	public float explosionSize;

	private void initBomb(Vector2 pos, Vector2 vel, float size, float damage, float shake){
		this.pos = pos;
		this.vel = vel;
		this.explosionSize = damage;
		this.size = size;
		this.damage = damage;
		this.shake = shake;
		color = Color.WHITE;
	}
	public Bomb(Vector2 pos, float size, float damage, float shake){
			initBomb(pos, Vector2.Zero.cpy(), size, damage, shake);
	}
	public Bomb(Vector2 pos, Vector2 vel, float size, float damage, float shake){
		initBomb(pos, vel, size, damage, shake);
	}

	@Override
	public void updatePos(float delta) {
		pos.add(vel.cpy().scl(delta));
	}

	@Override
	public void update(BasicLevel basicLevel, float delta) {

		updatePos(delta);
		HomeBase homeBase = basicLevel.homeBase;
		Physics.Force_Gravity(this, homeBase.getPos(), delta);

		if(CollisionDetector.collision(pos, homeBase.pos, homeBase.rotation, homeBase.pixmap)){
			explode(homeBase, basicLevel.friendlyTurrets);
			ScreenShake.addDuration(shake);
			addAnimation(basicLevel.frontAnimations);
			toRemove = true;
		}
	}

	@Override
	public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(color);
		shapeRenderer.circle(pos.x, pos.y, size);
	}

	private void addAnimation(Collection<MyAnimation> animations){
		Vector2 toPlanet = pos.cpy().nor();
		MyAnimation animation = new MyAnimation("bombtest",8, new Vector2(pos.x, pos.y).add(toPlanet.nor().scl(6)),toPlanet.angleDeg()-90,false,0.08f,1);
		animations.add(animation);
	}

	private void explode(HomeBase homeBase, Collection<Defender> friendlyTurrets) {

		explosionSize += RandomUtil.nextInt(10);

		if(CollisionDetector.collisionCircleCircle(pos, explosionSize, homeBase.core.pos, homeBase.core.radius))
			homeBase.core.causeDamage(damage);

		for(Iterator<Defender> itt = friendlyTurrets.iterator(); itt.hasNext(); ){
			Defender turret = itt.next();
			if (CollisionDetector.collisionCircleCircle(pos, explosionSize, turret.pos, turret.size)){
				turret.onGround = false;
				turret.health -= damage;
			}
		}

		homeBase.checkIntegrity = true;
		homeBase.removePointsBomb(pos.x, pos.y, (int) explosionSize, 0.3f + RandomUtil.nextFloat()/10);
	}

	@Override
	public void dispose() {

	}
}
