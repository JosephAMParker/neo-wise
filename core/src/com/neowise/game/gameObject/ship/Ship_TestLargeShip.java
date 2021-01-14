package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.weaponProjectile.Lava;
import com.neowise.game.gameObject.weaponProjectile.LavaBomb;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.squad.Squad;

import java.util.Collection;

public class Ship_TestLargeShip extends ShipRectangle{
	
	boolean swoop;
	float dis2orbit;
	public float lavaTimer,lavaTimerReset;
	int lava;

	public Ship_TestLargeShip(Vector2 pos) {

		super(pos);

		this.shipType = Constants.SHIP_TYPES.LARGE_SPACE_INVADER;

		this.width = 30;
		this.height = 20;
		this.health = 150;
		damage = 50;
		lava = 0;
		lavaTimerReset = 10;
		lavaTimer = 3;
		targetPos = pos.cpy();
		swoop = false;
		dis2orbit = altitude;
		animation = new MyAnimation("ShipTestShip",1,pos,rotation,true,200/6,width);
	}

	@Override
	public void update(BasicLevel basicLevel, float delta) {
		updateTimers(delta);
		updateTargetPos(delta);
		updatePos(delta);
		fire(basicLevel.hostileProjectiles);
		if(!inSquad)
			attemptToJoinSquad(basicLevel.enemySquads);
	}

	@Override
	public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
		shapeRenderer.identity();
		shapeRenderer.translate(pos.x, pos.y, 0);
		shapeRenderer.setColor(1-(health/50),0,health/50,1);
		shapeRenderer.rotate(0, 0, 1, rotation);
		shapeRenderer.rect(-width/2,-height/2, width, height);
	}

	public void updateTimers(float delta){
		lavaTimer -= delta;
	}

	public void updateTargetPos(float delta) {
		if(inSquad) {
			targetPos = squad.pos.cpy().nor();
			targetPos.rotateDeg(sqPlace.angleOffset);
			targetPos.scl(sqPlace.heightOffset + height / 2);
		}
	}

	public void updatePos(float delta) {
		prevPos = pos.cpy();
		pos.lerp(targetPos, (float) (1-Math.pow(0.1, delta)));
		rotation = pos.angleDeg() + 90;
		vel = pos.cpy().sub(prevPos);
	}

	public void fire(Collection<WeaponProjectile> hostileProjectiles) {
		if(lavaTimer <= 0){
			lavaTimer += lavaTimerReset;
			hostileProjectiles.add(new LavaBomb(pos.cpy()));
		}
	}
}

