package com.neowise.game.gameObject.defender;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;

import java.util.Collection;

public class OrbiterTurret extends Defender {
	
	float fireTimerMax,fireTimer;
	static int IDcount = 0;
	int ID,maxBombs,bombs;

	public OrbiterTurret(Vector2 pos) {
		
		fireTimerMax = 40;
		fireTimer = 0;
		ID = IDcount++;
		this.pos = pos;
		armed = false;
		onGround = false;
		health = 100;
		size = 4;
		maxBombs = 5;
		bombs = 0;
	}

	public void updateTimer(float delta) {
		
		fireTimer += delta;
		
		if (fireTimer >= fireTimerMax && !armed){
			armed = true;
			fireTimer = 0;
		}

		
	}

	@Override
	public void fire(Collection<WeaponProjectile> friendlyProjectiles) {

	}

	@Override
	public void updateTimers(float delta) {

	}

	@Override
	public void renderShapeRenderer(ShapeRenderer shapeRenderer) {

	}

	@Override
	public void update(BasicLevel basicLevel, float delta) {

	}

	@Override
	public void reset() {

	}
}
