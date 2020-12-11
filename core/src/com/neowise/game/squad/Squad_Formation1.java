package com.neowise.game.squad;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;

public class Squad_Formation1 extends Squad {

	float flipTimer = 0;
	float flipTimerReset = 5;

	int numL,numR,numC;
	SquadPlace l1,l2,l3,l4,a,r1,r2,r3,r4;
	Ship[] ships;

	public Squad_Formation1(Vector2 pos) {
		super(pos);
		float scale = 2f;
		rotationSpeed = 10;
		maxRotationSpeed = 10;
		ships = new com.neowise.game.gameObject.ship.Ship[9];
		a  = new SquadPlace(false,scale * 0  ,scale * 65, (short) 1);
		l1 = new SquadPlace(false,scale * -7.5f,scale * 65, (short) 0);
		l2 = new SquadPlace(false,scale * -15,scale * 65, (short) 0);
		l3 = new SquadPlace(false,scale * -7.5f ,scale * 75, (short) 0);
		l4 = new SquadPlace(false,scale * -15,scale * 75, (short) 0);
		r1 = new SquadPlace(false,scale * 7.5f, scale * 65, (short) 2);
		r2 = new SquadPlace(false,scale * 15 ,scale * 65, (short) 2);
		r3 = new SquadPlace(false,scale * 7.5f, scale * 75, (short) 2);
		r4 = new SquadPlace(false,scale * 15 ,scale * 75, (short) 2);
	}
	
	@Override
	public boolean empty(){
		return numR == 0 && numL == 0 && numC == 0;
	}
	
	@Override 
	public boolean full() {
		return numR == 4 && numL == 4;
	}

	@Override
	public void updateTimers(float delta) {
		flipTimer -= delta;
	}

	@Override
	public void performAction(float delta) {

		if(flipTimer <= 0) {
			flipTimer += flipTimerReset;
			rotationSpeed *= -1;
		}
	}

	@Override
	public void updatePos(float delta){

		Vector2 toPlanet = pos.cpy().nor().scl(-1);
		oa.angle = 360-toPlanet.angle();
		altitude = pos.len();
		float dis2orbit = altitude - 60;
		toPlanet.nor();

		pos.lerp(pos.cpy().add(toPlanet.cpy().scl(dis2orbit)), 0.025f);

		toPlanet.rotate(90);
		toPlanet.scl(delta * rotationSpeed);

		pos.add(toPlanet);
	}
	
	@Override
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
				return;
			}
		}
	}
	
	@Override
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
	
	@Override
	public void reorganizeZone(int zone){
		
		Ship tmpShip;
		switch (zone) {
			case 0: {
				if (ships[3] != null){
					tmpShip = ships[3];
					removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}

				if (ships[2] != null){
					tmpShip = ships[2];
					removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}

				if (ships[1] != null){
					tmpShip = ships[1];
					removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}
			} break;

			case 2: {
				if (ships[8] != null){
					tmpShip = ships[8];
					removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}

				if (ships[7] != null){
					tmpShip = ships[7];
					removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}

				if (ships[6] != null){
					tmpShip = ships[6];
					removeShipFromSquad(tmpShip);
					tmpShip.joinSquad(this);
					return;
				}
			} break;
		}
	}
	
	@Override
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
	
	@Override
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
	
	@Override
	public SquadPlace nextSidePlace() {
		
		if (numL <= numR) {
			
			numL++;
			if (!l1.filled) {
				l1.filled = true;
				return l1;
			}
			
			if (!l2.filled) {
				l2.filled = true;
				return l2;
			}
			
			if (!l3.filled) {
				l3.filled = true;
				return l3;
			}
			
			if (!l4.filled) {
				l4.filled = true;
				return l4;
			}
		}
		
		else {
			
			numR++;
			if (!r1.filled) {
				r1.filled = true;
				return r1;
			}
			
			if (!r2.filled) {
				r2.filled = true;
				return r2;
			}
			
			if (!r3.filled) {
				r3.filled = true;
				return r3;
			}
			
			if (!r4.filled) {
				r4.filled = true;
				return r4;
			}
			
		}
		return null;

	}


}
