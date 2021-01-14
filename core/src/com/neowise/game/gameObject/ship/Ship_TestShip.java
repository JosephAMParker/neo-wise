package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.weaponProjectile.Bomb;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class Ship_TestShip extends ShipRectangle {

	float targetSpeed;
	float targetTimer;
	float bombTimer, bombTimerReset;

	public Ship_TestShip(Vector2 pos, float orbitalRange) {

		super(pos);

		this.shipType = Constants.SHIP_TYPES.BASIC_SPACE_INVADER;

		this.width  = 12;
		this.height = 10;
		this.health = 60;
		this.damage = 10;
		this.orbitalRange = orbitalRange;
		this.bombTimerReset = 8;
		this.bombTimer = RandomUtil.nextFloat()*bombTimerReset;
		this.targetTimer = RandomUtil.nextFloat() * 25;

		setNewTarget();

		animation = new MyAnimation("ShipTestShip",1,pos,rotation,true,200/6,width);

	}

	@Override
	public void update(BasicLevel basicLevel, float delta) {
		updateTimers(delta);
		updateTargetPos(delta);
		updatePos(delta);
		performAction();
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

	private void setNewTarget() {
		targetPos = pos.cpy().nor();
		targetPos.scl(orbitalRange + RandomUtil.nextInt(25));
		targetSpeed = 7 + RandomUtil.nextFloat2()*2;
		targetSpeed = RandomUtil.nextBoolean() ? targetSpeed : -targetSpeed;
	}

	public void performAction() {
		if(!inSquad) {
			if (targetTimer <= 0) {
				targetTimer += RandomUtil.nextFloat() * 10;
				setNewTarget();
			}
		}
	}

	public void updateTimers(float delta){
		bombTimer -= delta;
		if(!inSquad)
			targetTimer -= delta;
	}

	public void updateTargetPos(float delta){

		if(inSquad) {
			targetPos = squad.pos.cpy().nor();
			targetPos.rotateDeg(sqPlace.angleOffset);
			targetPos.scl(sqPlace.heightOffset + height / 2);
		} else {
			targetPos.rotateDeg(targetSpeed * delta);
		}
	}

	public void updatePos(float delta) {
		prevPos = pos.cpy();
		pos.lerp(targetPos, (float) (1-Math.pow(0.1, delta)));
		rotation = pos.angleDeg() + 90;
		vel = pos.cpy().sub(prevPos);
	}

	public void fire(Collection<WeaponProjectile> hostileProjectiles) {
		if(bombTimer <= 0) {
			bombTimer += bombTimerReset + RandomUtil.nextFloat()*bombTimerReset;
			Bomb bomb = new Bomb(pos.cpy(),1, damage, 0.7f);
			hostileProjectiles.add(bomb);
		}
	}
}

