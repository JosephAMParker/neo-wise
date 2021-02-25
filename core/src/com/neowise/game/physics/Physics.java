package com.neowise.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.GameObject;

public class Physics {
	
	static float G = 120f;

	public static void Force_Gravity(GameObject obj, Vector2 pos, float delta) {
		obj.vel.add(obj.pos.cpy().nor().scl(-G * delta));
	}

}
