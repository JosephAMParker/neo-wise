package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.gameObject.RectangleGameObject;
import com.neowise.game.squad.Squad;
import com.neowise.game.squad.SquadPlace;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;

import java.util.Collection;

public class Ship extends RectangleGameObject {
	
	public float mass,health,altitude,damage;
	public Vector2 targetPos, prevPos, look;
	public boolean dead, inSquad, wantsToJoin;
	public Squad squad;
	public SquadPlace sqPlace;
	public OrbitalAngle oa;
	public int resWorth;
	public float stunnedTimer;
	public float speed;
	public float rotationSpeed;
	static float PPM;
	public MyAnimation animation;
	
	public Ship(float x,float y){
		pos = new Vector2(x,y);
		prevPos = pos.cpy();
		rotation = 0;
		resWorth = 0;
	}

	public Ship(Vector2 pos){
		vel = new Vector2();
		acc = new Vector2();
		impulse = new Vector2();
		altitude = pos.len();
		this.pos = pos;
	}
	
	public void joinSquad(Squad squad){
		
	}
	
	public boolean isLavaArmed(){
		return false;
	}
	
	public void updateTimers(float delta){
		
	}

	public void updateAcc(float delta) {

	}

	public void updateVel(float delta) {

	}

	public void updatePos(float delta) {

	}

	public void updateTargetPos(float delta){

	}

	public void updateVel(Vector2 HBPos) {
		
	}

	public void updatePos(Vector2 HBPos) {

	}

	public Vector2 getVel() {
		return vel.cpy();
	}
	
	public Vector2 getPos() {
		return pos.cpy();
	}

	public void removeFromSquad() {
		// TODO Auto-generated method stub
		
	}

	public void performAction(){

	}

	public void fire(Collection<WeaponProjectile> hostileProjectiles){

	}

	public float getRotationDegrees() {
		return (float) (rotation * 180 / Math.PI);
	}




}
