package com.neowise.game.gameObject.defender;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.GameObject;
import com.neowise.game.homeBase.HomeBase;

import java.util.Collection;

public abstract class Defender extends GameObject {

	public float size,health,rotation;
	public boolean onGround,armed;
	MyAnimation animation;
	private boolean toRemove;

	public Defender(){
		
	}

	public void locateGround2(HomeBase homeBase, float delta) {

		if(onGround)
			return;

		Vector2 toPlanet = pos.cpy().nor().scl(-1);
		Vector2 b = pos.cpy().add(toPlanet.cpy().scl(size/2));
		Vector2 t = pos.cpy().sub(toPlanet.cpy().scl(size/2));
		//pos.sub(toPlanet.scl(size/2));

		if (CollisionDetector.collision(b.x, b.y, homeBase)) {

			if(CollisionDetector.collision(t.x, t.y, homeBase)){

				// step towards the sky
				pos.sub(toPlanet.scl(250*delta));
				return;
			}
			onGround = true;
			return;
		}

		else {
			// step towards the ground
			pos.add(toPlanet.scl(100*delta));
		}
		//pos.add(toPlanet);

	}
	
	public void locateGround(HomeBase homeBase, float delta) {
		
		if(onGround)
			return;
			
		Vector2 toPlanet = pos.cpy().nor().scl(-1);
		if (CollisionDetector.collision(pos.x, pos.y, homeBase)) {
			onGround = true;
			while(CollisionDetector.collision(pos.x, pos.y, homeBase)){
				pos.sub(toPlanet.scl(1f));
			}
			pos.sub(toPlanet.scl(1f * size / 3));
		}
		
		else {
			// step towards the ground
			pos.add(toPlanet.scl(100*delta));
			
		}
		
	}
	
	/**
	 * rotates the turret in space to match the rotation of the planet its resting on
	 */
	public void rotateByPlanet(double rotationDelta, Vector2 PlanetPos) {
		
		if(!onGround)
			return;
		
		pos.sub(PlanetPos);
		Vector2 newPos = new Vector2();
		newPos.x  = (float) (pos.x * Math.cos(rotationDelta - Math.PI*2) - pos.y  * Math.sin(rotationDelta - Math.PI*2));
		newPos.y = (float) (pos.x * Math.sin(rotationDelta - Math.PI*2) + pos.y * Math.cos(rotationDelta - Math.PI*2));
		pos = newPos.add(PlanetPos);

		//this.rotation += rotation;
	}

	public void dispose(){
		//animation.currentSprite.getTexture().dispose();

	}

	public boolean toRemove(){
		return health <= 0;
	}

    public abstract void fire(Collection<WeaponProjectile> friendlyProjectiles);
	public abstract void updateTimers(float delta);
	public abstract void renderShapeRenderer(ShapeRenderer shapeRenderer);
	public abstract void update(BasicLevel basicLevel, float delta);
}
