package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.squad.Squad;

public class Ship_LargeBomber extends Ship{
	
	boolean swoop;
	float dis2orbit,dis2orbitk1,dis2orbitk2, integral, derivative;
	float KD,KI,KP;

	public Ship_LargeBomber(float x, float y) {
		
		super(x, y);
		
		mass = 2;         
		width = 45;       
		height = 34;      
		health = 150;      
		altitude = 999;   
		dead = false;    
		inSquad = false; 
		
		pos = new Vector2(x,y);
		vel = new Vector2(0,0);
		
		swoop = false;
		KI = -0.145f;
		KP = 1.5f;
		KD = 0.5f;
	
		dis2orbit = altitude;
		dis2orbitk1 = 0;
		dis2orbitk2 = 0;
		
	}

	@Override
	public void updateTargetPos(float delta) {
		targetPos = pos.cpy();
	}

	@Override
	public void joinSquad(Squad sq){
		squad = sq;
		sqPlace = sq.nextCenterPlace();
		inSquad = false;
	}
	
	@Override
	public void updateVel(Vector2 HBPos) {
		
		Vector2 toPlanet =  (HBPos).sub(pos);
		toPlanet.nor();
		rotation = (float) ((90 + toPlanet.angle()) * Math.PI / 180);
		
		if(inSquad){
			
			
			
			//vel.scl(0);
			//pos.lerp(squad.pos.cpy().add(sqPlace.pos), 0.8f);

		}
		
		else{
			
			
			altitude = pos.dst(HBPos);
			dis2orbit = altitude - 60 - 25;
			
			float PID = 0;
			
			vel.limit(2);
			if(dis2orbit < 5){
				swoop = false;
			}
	
			if (swoop){
				float scl = Math.min(10f/(dis2orbit),5); 
				vel.add(toPlanet.scl(scl).rotate(60));
	
			}
			
			else {
				
				swoop = false;
				integral = dis2orbit + dis2orbitk1 + dis2orbitk2;
				derivative = (dis2orbit - dis2orbitk1);
				PID = KP*dis2orbit + KI*integral + KD*derivative;
				vel.add(toPlanet.scl(PID/10));
				
			}
			vel.clamp(2f, 10f);
			dis2orbitk2 = dis2orbitk1;
			dis2orbitk1 = dis2orbit;
		}
		
		
	}

}
