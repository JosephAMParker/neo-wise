package com.neowise.game.squad;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship_TestShip;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.gameObject.ship.Ship;

public abstract class Squad {
	
	public Vector2 pos, vel;
	protected float width,height,altitude,rotation,rotationSpeed,maxRotationSpeed,orbitalRange;
	public boolean dead;

	public Squad(Vector2 pos){
		this.pos = pos;
		this.rotation = 0;
	}

	public Vector2 getVel() {
		return vel.cpy();
	}
	
	public Vector2 getPos() {
		return pos.cpy();
	}

	public abstract void update(float delta);
	public abstract boolean toRemove();
	public abstract boolean canJoinSquad(Ship ship);
	public abstract void removeShipFromSquad(Ship ship);
	public abstract void joinSquad(Ship ship);
}
