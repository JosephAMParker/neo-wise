package com.neowise.game.homeBase;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Chunk {

	Chunk(Texture texture, Vector2 pos, Vector2 vel, double rotation, double rotationDelta){
		this.texture = texture;
		this.rotation = rotation;
		this.rotationDelta = rotationDelta;
		this.pos = pos;
		this.vel = vel;
	}

	public Texture texture;
	public double rotation, rotationDelta;
	public Vector2 pos, vel;
}

class preChunk {

	public static int size;

	preChunk(){
		l = new ArrayList<Vector2>();
		minx = 500;
		miny = 500;
		maxx = 0;
		maxy = 0;
		main = false;
	}

	public void add(Vector2 p){

		if (p.x > maxx)
			maxx = (int) p.x;
		if (p.x < minx)
			minx = (int) p.x;

		if (p.y > maxy)
			maxy = (int) p.y;
		if (p.y < miny)
			miny = (int) p.y;

		if (p.x == size/2 && p.y == size/2)
			main = true;

		l.add(p);

	}

	boolean main;
	int minx,maxx,miny,maxy;
	ArrayList<Vector2> l;
}
