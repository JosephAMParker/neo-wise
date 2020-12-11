package com.neowise.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.GameObject;

public class Physics {
	
	static float G = 7f;
	public static float mass;

	public static void Force_Gravity(GameObject obj, Vector2 pos, float delta) {

		float r = Math.min(10,obj.getPos().dst(pos));
		float force = G * obj.mass * mass/r/r;
		Vector2 acc = pos.sub(obj.getPos()).nor();
		acc.scl(force*delta);
		obj.vel.add(acc);

	}

}
