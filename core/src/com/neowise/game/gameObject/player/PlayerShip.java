package com.neowise.game.gameObject.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.main.LevelInfo;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.gameObject.weaponProjectile.Weapon;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.gameObject.weaponProjectile.Weapon_ShotGun;
import com.neowise.game.gameObject.RectangleGameObject;

import java.util.Collection;

public class PlayerShip extends RectangleGameObject {
	
	public static final float width = 6;
	public static final float height = 3;
	private static final Vector2 startPos = new Vector2(10,900);
	public float altitude;
	public float dis2orbit;
	public float orbitalRange;
	public float bulletCoolDown = 0;
	public boolean shootWeapon;
	public float health,mass;
	public OrbitalAngle oa;
	private float jetSpeed;
	private float maxJetSpeed;
	public boolean shootBullet,shootRocket,shootCityBomb;
	public boolean cityBombActive;
	public boolean detonateBombs;
	public boolean flyToPosition;
	private boolean accelerate;

	private Weapon currentWeapon;

	public PlayerShip(float x, float y){
		
		pos = startPos.cpy();
		vel = new Vector2(0,0);
		oa = new OrbitalAngle(0.0f);
		shootWeapon	= false;
		shootBullet = false;
		shootRocket = false;
		shootCityBomb = false;
		cityBombActive = false;
		detonateBombs = false;
		flyToPosition = false;
		health = 100;
		rotation = 0;
		orbitalRange = 60;
		dis2orbit = 999;
		altitude = 34;
		mass = 9;
		jetSpeed = 50f;
		maxJetSpeed = 75f;
		accelerate = false;

		currentWeapon = new Weapon_ShotGun();
		
	}

	private void resetPlayer(){

		pos = startPos.cpy();
		vel = new Vector2(0,0);
		oa = new OrbitalAngle(0.0f);

		shootBullet = false;
		shootRocket = false;
		shootCityBomb = false;
		cityBombActive = false;
		detonateBombs = false;
		flyToPosition = false;
		accelerate = false;
		shootWeapon = false;
	}

	public void goToStart(LevelInfo levelInfo) {
		resetPlayer();
	}

	public void movePlayer(Vector2 moveDir, float delta){

		//vel.add(toPlanet.cpy().rotate(-90).scl(jetSpeed * delta));
		//accelerate = true;

		jetSpeed = 20;
		moveDir.scl(delta * jetSpeed);
		vel.add(moveDir);
	}

	public void goLeft(Vector2 toPlanet, float delta){
		movePlayer(toPlanet.rotate90(1), delta);
	}

	public void goRight(Vector2 toPlanet, float delta){
		movePlayer(toPlanet.rotate90(-1), delta);
	}

	public void getInput(float delta){

		if(flyToPosition) return;
		
		Vector2 toPlanet = pos.cpy().nor();
		accelerate = false;
		
		if (Gdx.input.isKeyPressed(Keys.A))
			goLeft(toPlanet, delta);
		
		if (Gdx.input.isKeyPressed(Keys.D))
			goRight(toPlanet, delta);

		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			if(bulletCoolDown <= 0) {
				shootWeapon = true;
			}
		}
		
		for (int i = 0;i<5;i++){
			if(Gdx.input.isTouched(i)){
				float x = Gdx.input.getX(i);
				float y = Gdx.input.getY(i);
				if (y > 400){
					float w = Gdx.graphics.getWidth();
					
					if (x < w/3)
						goLeft(toPlanet, delta);
					
					else if (x > 2*w/3)
						goRight(toPlanet, delta);
					
					else{
						if(bulletCoolDown <= 0) {
							shootWeapon = true;
						}
					}
				}
			}
		}
	}

	public void updatePos(float delta){
		Vector2 toPlanet = pos.cpy().nor().scl(-1);
		rotation = (toPlanet.angle() + 90) % 360;

		pos.add(vel);

		pos.clamp(orbitalRange, orbitalRange);
	}
	
	/**
	 * rotates the player in space to match the rotation of the planet its resting on
	 * @param rotation
	 */
	public void rotateByPlanet(double rotation, Vector2 PlanetPos) {
		
		pos.sub(PlanetPos);
		Vector2 newPos = new Vector2();
		newPos.x  = (float) (pos.x * Math.cos(rotation - Math.PI*2) - pos.y  * Math.sin(rotation - Math.PI*2));
		newPos.y = (float) (pos.x * Math.sin(rotation - Math.PI*2) + pos.y * Math.cos(rotation - Math.PI*2));
		pos = newPos.add(PlanetPos);
	}
	
	public Vector2 getVel() {
		return vel.cpy();
	}
	
	public Vector2 getPos() {
		return pos.cpy();
	}

	public void drag(float delta) {
		vel.scl(0.88f);
		if( accelerate ) {
			if (jetSpeed < maxJetSpeed)
				jetSpeed += 160 * delta;
		}
		else {
			if (jetSpeed > 0.5f)
				jetSpeed -= 110 * delta;
		}

		if (jetSpeed > maxJetSpeed)
			jetSpeed = maxJetSpeed;

		if (jetSpeed < 0)
			jetSpeed = 0;
		
	}

	public void updateTimers(float delta) {

		if (bulletCoolDown > 0)
			bulletCoolDown -= delta;

	}

	public Collection<? extends WeaponProjectile> fire(Vector2 up, float delta) {
		shootWeapon = false;
		vel.add(currentWeapon.recoil(up.cpy().scl(delta)));
		return currentWeapon.fire(pos,up.cpy());
	}
}
