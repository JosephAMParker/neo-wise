package com.neowise.game.squad;

import com.badlogic.gdx.math.Vector2;

public class SquadPlace {

	public boolean filled;
	public short zone;
	public Vector2 pos;
	public float angleOffset, heightOffset;

	SquadPlace(Vector2 pos){
		this.filled = filled;
		this.pos = pos;
	}

	SquadPlace(boolean filled, float angleOffset, float heightOffset, short zone){
		this.filled = filled;
		this.angleOffset = angleOffset;
		this.heightOffset = heightOffset;
		this.zone = zone;
	}

}
