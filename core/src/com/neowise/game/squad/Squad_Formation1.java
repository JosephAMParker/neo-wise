package com.neowise.game.squad;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.util.Constants;

import java.util.HashSet;
import java.util.Set;

public class Squad_Formation1 extends Squad {

	float flipTimer = 0;
	float flipTimerReset = 5;
	private final Set<Constants.SHIP_TYPES> ALLOWED_SHIPS = new HashSet<>();

	int numL,numR,numC;
	SquadPlace l1,l2,l3,l4,a,r1,r2,r3,r4;
	Ship[] ships;

	public Squad_Formation1(Vector2 pos, float orbitalRange) {

		super(pos);

		float scale = orbitalRange/200;
		rotationSpeed = 10;
		maxRotationSpeed = 10;
		this.orbitalRange = orbitalRange;
		ships = new Ship[9];

		a  = new SquadPlace(false,scale * 0  ,scale * 1 + orbitalRange, (short) 1);
		l1 = new SquadPlace(false,scale * -7.5f,scale * 1 + orbitalRange, (short) 0);
		l2 = new SquadPlace(false,scale * -15,scale * 1 + orbitalRange, (short) 0);
		l3 = new SquadPlace(false,scale * -7.5f ,scale * 15 + orbitalRange, (short) 0);
		l4 = new SquadPlace(false,scale * -15,scale * 15 + orbitalRange, (short) 0);
		r1 = new SquadPlace(false,scale * 7.5f, scale * 1 + orbitalRange, (short) 2);
		r2 = new SquadPlace(false,scale * 15 ,scale * 1 + orbitalRange, (short) 2);
		r3 = new SquadPlace(false,scale * 7.5f, scale * 15 + orbitalRange, (short) 2);
		r4 = new SquadPlace(false,scale * 15 ,scale * 15 + orbitalRange, (short) 2);

		ALLOWED_SHIPS.add(Constants.SHIP_TYPES.BASIC_SPACE_INVADER);
		ALLOWED_SHIPS.add(Constants.SHIP_TYPES.LARGE_SPACE_INVADER);
	}

	@Override
	public void update(float delta) {
		updateTimers(delta);
		updatePos(delta);
		changeDirection();
	}

	@Override
	public boolean toRemove() {
		return dead || empty();
	}

	@Override
	public boolean canJoinSquad(Ship ship) {
		return !dead && !full() && ALLOWED_SHIPS.contains(ship.shipType);
	}

	@Override
	public void joinSquad(Ship ship) {

		switch (ship.shipType){
			case BASIC_SPACE_INVADER : {
				if(fillNextSidePlace(ship)) {
					ship.inSquad = true;
					ship.squad = this;
				}
				break;
			}

			case LARGE_SPACE_INVADER : {
				if(fillNextCenterPlace(ship)) {
					ship.inSquad = true;
					ship.squad = this;
				}
				break;
			}
		}
	}

	public boolean empty(){
		return numR == 0 && numL == 0 && numC == 0;
	}

	public boolean full() {
		return numR == 4 && numL == 4;
	}

	public void updateTimers(float delta) {
		flipTimer -= delta;
	}

	public void changeDirection() {

		if(flipTimer <= 0) {
			flipTimer += flipTimerReset;
			rotationSpeed *= -1;
		}
	}

	public void updatePos(float delta){
		Vector2 toPlanet = pos.cpy().nor().scl(-1);
		toPlanet.rotateDeg(90);
		toPlanet.scl(delta * rotationSpeed);
		pos.add(toPlanet);
		pos.clamp(orbitalRange, orbitalRange);
	}

	public void openPlace(Ship ship) {
		
		ship.sqPlace.filled = false;
		
		switch (ship.sqPlace.zone) {
			case 0 : numL--; break;
			case 1 : numC--; break;
			case 2 : numR--; break;
		}
	}
	
	@Override
	public void removeShipFromSquad(Ship ship) {
		_removeShipFromSquad(ship);
		reorganizeZone(ship.sqPlace.zone);
		if(ship.sqPlace.zone == 1)
			removeAllShips();

		if(empty())
			dead = true;
	}

	public void _removeShipFromSquad(Ship ship) {


		ship.sqPlace.filled = false;
		ship.inSquad = false;
		switch (ship.sqPlace.zone) {
			case 0 : numL--; break;
			case 1 : numC--; break;
			case 2 : numR--; break;
		}

		for (int i = 0;i < ships.length; i++){
			if (ships[i] == ship) {
				ships[i] = null;
				break;
			}
		}
	}

	public void removeAllShips() {
		
		for (int i = 0;i < ships.length; i++){
			
			if(ships[i] != null){
				ships[i].inSquad = false;
				ships[i] = null;
			}
		}
		
		numL = 0;
		numR = 0;
		numC = 0;
	}

	public void reorganizeZone(int zone){
		
		Ship tmpShip;
		switch (zone) {
			case 0: {
				if (ships[3] != null){
					tmpShip = ships[3];
					_removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}

				if (ships[2] != null){
					tmpShip = ships[2];
					_removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}

				if (ships[1] != null){
					tmpShip = ships[1];
					_removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}
			} break;

			case 2: {
				if (ships[8] != null){
					tmpShip = ships[8];
					_removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}

				if (ships[7] != null){
					tmpShip = ships[7];
					_removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}

				if (ships[6] != null){
					tmpShip = ships[6];
					_removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}
			} break;
		}
	}

	public boolean fillNextCenterPlace(Ship ship) {
		
		if (numC == 0){
			numC++;
			a.filled = true;
			ship.sqPlace = a;
			ships[4] = ship;
			return true;
		}
		
		return false;
	}

	public boolean fillNextSidePlace(Ship ship) {
		
		if (numL <= numR) {
			
			numL++;
			if (!l1.filled) {
				l1.filled = true;
				ship.sqPlace = l1;
				
				ships[0] = ship;
				return true;
			}
			
			if (!l2.filled) {
				l2.filled = true;
				ship.sqPlace = l2;
				ships[1] = ship;
				return true;
			}
			
			if (!l3.filled) {
				l3.filled = true;
				ship.sqPlace = l3;
				ships[2] = ship;
				return true;
			}
			
			if (!l4.filled) {
				l4.filled = true;
				ship.sqPlace = l4;
				ships[3] = ship;
				return true;
			}
		}
		
		else {
			
			numR++;
			if (!r1.filled) {
				r1.filled = true;
				ship.sqPlace = r1;
				ships[5] = ship;
				return true;
			}
			
			if (!r2.filled) {
				r2.filled = true;
				ship.sqPlace = r2;
				ships[6] = ship;
				return true;
			}
			
			if (!r3.filled) {
				r3.filled = true;
				ship.sqPlace = r3;
				ships[7] = ship;
				return true;
			}
			
			if (!r4.filled) {
				r4.filled = true;
				ship.sqPlace = r4;
				ships[8] = ship;
				return true;
			}
		}
		return false;
	}
}
