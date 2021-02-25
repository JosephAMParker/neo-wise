package com.neowise.game.gameObject.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.LevelInfo.LevelInfo;
import com.neowise.game.gameObject.player.Weapon.ChainGun;
import com.neowise.game.gameObject.player.Weapon.CityDefender;
import com.neowise.game.gameObject.player.Weapon.LaserBeamGun;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.menu.ControlBar;
import com.neowise.game.gameObject.player.Weapon.Weapon;
import com.neowise.game.gameObject.player.Weapon.ShotGun;
import com.neowise.game.gameObject.RectangleGameObject;

import java.util.ArrayList;

public class PlayerShip extends RectangleGameObject {
	
	public static final float width = 8;
	public static final float height = 3;
	private static final Vector2 startPos = new Vector2(10,900);
	public float altitude;
	public float dis2orbit;
	public float orbitalRange;
	public float bulletCoolDown = 0;
	public boolean shootWeapon;
	public float health,mass;
	private float jetSpeed;
	private float maxJetSpeed;
	public boolean cityBombActive;
	public boolean detonateBombs;
	public boolean flyToPosition;
	private boolean accelerate;
	private boolean fireWeaponPressed;

	public Weapon currentWeapon;
	private int weaponIndex;
	private ArrayList<Weapon> weapons;

	public PlayerShip(){
		
		pos = startPos.cpy();
		vel = new Vector2(0,0);
		shootWeapon	= false;
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
		initWeapons();
	}

	private void initWeapons(){
		weaponIndex = 0;
		weapons = new ArrayList<>();
		weapons.add(new ShotGun());
		weapons.add(new ChainGun());
		weapons.add(new CityDefender());
		weapons.add(new LaserBeamGun(pos, 10));
		currentWeapon = weapons.get(0);
	}

	private void resetPlayer(){

		pos.set(startPos);// = startPos.cpy();
		vel = new Vector2(0,0);

		cityBombActive = false;
		detonateBombs = false;
		flyToPosition = false;
		accelerate = false;
		shootWeapon = false;
	}

	public void goToStart(LevelInfo levelInfo) {
		resetPlayer();
	}

	public void nextWeapon(){
		weaponIndex += 1;
		if(weaponIndex >= weapons.size())
			weaponIndex = 0;

		currentWeapon = weapons.get(weaponIndex);
	}

	public void prevWeapon(){
		weaponIndex -= 1;
		if(weaponIndex < 0)
			weaponIndex = weapons.size() - 1;

		currentWeapon = weapons.get(weaponIndex);
	}

	public void movePlayer(Vector2 moveDir, float delta){
		jetSpeed = 20;
		moveDir.scl(delta * jetSpeed);
		vel.add(moveDir);
	}

	private void goLeft(Vector2 toPlanet, float delta){
		movePlayer(toPlanet.rotate90(1), delta);
	}

	private void goRight(Vector2 toPlanet, float delta){
		movePlayer(toPlanet.rotate90(-1), delta);
	}

	private void handleFirePress(){

		fireWeaponPressed = true;

		if(!cityBombActive && bulletCoolDown <= 0)
			shootWeapon = true;

		if(cityBombActive)
			detonateBombs = true;
	}

	public void getInput(float delta){
		
		if(flyToPosition) return;
		
		Vector2 toPlanet = pos.cpy().nor();
		accelerate = false;
		fireWeaponPressed = false;
		
		if (Gdx.input.isKeyPressed(Keys.A))
			goLeft(toPlanet, delta);
		
		if (Gdx.input.isKeyPressed(Keys.D))
			goRight(toPlanet, delta);

		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			handleFirePress();
		}

		if(ControlBar.left.touched())
			goLeft(toPlanet, delta);

		if(ControlBar.right.touched())
			goRight(toPlanet, delta);

		if(ControlBar.shoot.touched())
			handleFirePress();
	}

	public void updatePos(float delta){
		Vector2 toPlanet = pos.cpy().nor().scl(-1);
		rotation = (toPlanet.angleDeg() + 90) % 360;

		pos.add(vel);
		vel.clamp(0, 50);
		pos.clamp(orbitalRange, orbitalRange);
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
		currentWeapon.updateTimers(delta);
	}

	public void fire(BasicLevel basicLevel, float delta) {
		currentWeapon.fire(pos, fireWeaponPressed, basicLevel, delta);
	}

    public void update(BasicLevel basicLevel, float delta) {
		updateTimers(delta);
		getInput(delta);
		updatePos(delta);
		drag(delta);
		fire(basicLevel, delta);
    }
}
