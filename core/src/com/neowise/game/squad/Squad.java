package com.neowise.game.squad;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.gameObject.ship.Ship;

public abstract class Squad {
	
	public Vector2 pos, vel;
	protected float width,height,altitude,rotation,rotationSpeed, maxRotationSpeed;
	public boolean dead,joinable;
	public OrbitalAngle oa;
	public boolean loose;
	
	public Squad(float x,float y){
		this.oa = new OrbitalAngle(0);
		this.dead = false;
		this.joinable = true;
		this.pos = new Vector2(x,y);
		this.rotation = 0;
	}

	public Squad(Vector2 pos){
		this.oa = new OrbitalAngle(0);
		this.dead = false;
		this.joinable = true;
		this.pos = pos;
		this.rotation = 0;
	}

	public abstract void updatePos(float delta);

	public void updateTimers(float delta) {}
	public void performAction(float delta){

	}

	public Vector2 getVel() {
		return vel.cpy();
	}
	
	public Vector2 getPos() {
		return pos.cpy();
	}

	public SquadPlace nextCenterPlace() {
		
		return null;
	}
	public SquadPlace nextSidePlace() {
		
		return null;
	}
	public boolean empty() {
		
		return false;
	}
	public boolean full() {
		
		return false;
	}

	public void openPlace(com.neowise.game.gameObject.ship.Ship ship) {
		
		
	}


	public void reorganizeZone(int zone) {
		// TODO Auto-generated method stub
		
	}

	public boolean fillNextPlace(com.neowise.game.gameObject.ship.Ship ship){

		return false;
	}

	public boolean fillNextSidePlace(com.neowise.game.gameObject.ship.Ship ship) {
		return false;
	}

	public boolean fillNextCenterPlace(com.neowise.game.gameObject.ship.Ship ship) {
		return false;
		
	}

	public void removeShipFromSquad(Ship ship) {
		// TODO Auto-generated method stub
		
	}

	public void removeAllShips() {
		
	}

}
