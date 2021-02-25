package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.weaponProjectile.Health;
import com.neowise.game.gameObject.weaponProjectile.HealthGWT;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.OrbitalAngle;
import com.neowise.game.gameObject.RectangleGameObject;
import com.neowise.game.squad.Squad;
import com.neowise.game.squad.SquadPlace;
import com.neowise.game.util.RandomUtil;
import com.neowise.game.util.Settings;

import java.util.Collection;
import java.util.Iterator;

public abstract class Ship extends RectangleGameObject {
	
	public float mass,health,maxHealth,altitude,damage, orbitalRange;
	public Vector2 targetPos, prevPos, impulse;
	public boolean dead, inSquad;
	public Constants.SHIP_TYPES shipType;
	public Squad squad;
	public SquadPlace sqPlace;
	public OrbitalAngle oa;
	public int reward;
	public float speed;
	public float rotationSpeed;
	public MyAnimation animation;

	protected BasicLevel basicLevel;

	public Ship(Vector2 pos){
		this.pos = pos;
		this.vel = new Vector2();
		this.impulse = new Vector2();
		this.altitude = pos.len();
		inSquad = false;
		dead    = false;
		reward = 0;
	}

	public Vector2 getVel() {
		return vel.cpy();
	}
	
	public Vector2 getPos() {
		return pos.cpy();
	}

	public void removeFromSquad() {
		if (inSquad){
			squad.removeShipFromSquad(this);
			inSquad = false;
			squad = null;
			sqPlace = null;
		}
	}

	public void joinSquad(Squad sq){
		if (!inSquad)
			sq.joinSquad(this);
	}

	public void attemptToJoinSquad(Collection<Squad> squads) {
		for(Iterator<Squad> squad_it = squads.iterator(); squad_it.hasNext();){
			Squad squad = squad_it.next();
			float distanceToSquad = OrbitalAngle.distance(pos.angleDeg(), squad.pos.angleDeg());
			if (squad.canJoinSquad(this) && Math.abs(distanceToSquad) < 30) {
				joinSquad(squad);
				return;
			}
		}
	}

	public abstract void update(BasicLevel basicLevel, float delta);

	public boolean toRemove(){
		return health <= 0;
	}

	public void dispose(){

	}

	public void kill(BasicLevel basicLevel){
		if(!Settings.isGWT)
			spawnReward(basicLevel);
		removeFromSquad();
		dead = true;
	}

	private void spawnReward(BasicLevel basicLevel) {
		Health health;
		Vector2 vel = new Vector2(1,1);
		for (int i = 0;i < reward;i++){
			vel.setLength(10 + RandomUtil.nextInt(40));
			vel.rotateDeg(RandomUtil.nextInt(360));

			if(Settings.isGWT)
				health = new HealthGWT(pos.cpy(), vel.cpy(),1, 6);
			else
				health = new Health(pos.cpy(), vel.cpy(),1, 6);

			basicLevel.tempHostileProjectiles.add(health);
		}
	}

	public abstract void renderShapeRenderer(ShapeRenderer shapeRenderer);

    public abstract boolean collisionLine(Vector2 pos, Vector2 endLaser);

	public abstract boolean collisionPoint(Vector2 pos);

	public abstract boolean collisionCircle(Vector2 pos, float radius);
}
