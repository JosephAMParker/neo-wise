package com.neowise.game.homeBase;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.GameObject;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.main.LevelInfo;

public class HomeBase extends GameObject {

	public float mass;	//size refers to largest side of quadTree or radius of circle if no quadTree
	int size;
	public float distance;
	public double rotation, rotationDelta; 			//current rotation of the planet
	public float orbitalRange;
	public boolean checkIntegrity;
	public Collection<Defender> turrets;
	public Pixmap pixmap; //  = new Pixmap(Gdx.files.internal("data/homebase.png"));
	Pixmap lava = new Pixmap(Gdx.files.internal("Pixmaps/lava.png"));
	Pixmap bombmapsmall1 = new Pixmap(Gdx.files.internal("Pixmaps/expl.png"));
	Pixmap bombmapsmall2 = new Pixmap(Gdx.files.internal("Pixmaps/expl4.png"));


	ByteBuffer bb, bb1, bb2, bb3, bbl;
	HashMap<Integer, CLabel> allLabels;
	HashMap<Integer,preChunk> patterns;
	public ArrayList<Chunk> chunks;

	Random random;
	
	int[][] labels;
	int[] neighborArray;
	int neighborCount = 0;
	int minNeighbor = 10;
	int labelCount;
	
	int height;
	int width;
	public boolean checkingIntegrity;
	public boolean centreIntact;


	public HomeBase(float f, float g, int size) {

		centreIntact = true;
		checkingIntegrity = false;
		bb1 = bombmapsmall1.getPixels();
		bb2 = bombmapsmall2.getPixels();
		bbl = lava.getPixels();
		checkIntegrity = false;

		rotation = 0;
		orbitalRange = 60;
		rotationDelta = 0.0;
		pos = new Vector2(f,g);
		vel = new Vector2(0,-0.1f);
		distance = 0;
		this.size = size;
		mass = 50;
		turrets = new ArrayList<Defender>();


		pixmap = new Pixmap(size, size, Format.Intensity);



		//pixmap.drawPixmap(pixmapplanet,0,0);



		//pixmap: the planets structure data is stored here. Cells with values (colors) greater than BLACK are considered on.
		// All others are off and not used in collision detection, and alpha is set to 0.
		pixmap.setBlending(Blending.None);
		height = pixmap.getHeight();
		width = pixmap.getWidth();
		
		chunks = new ArrayList<Chunk>();
		labels = new int[size][size];
		neighborArray = new int[5];

		random = new Random();
		float ry = 0;
		/*
		for (float i = 0; i < Math.PI * 2 ; i+= 0.05f){
			
			
			
			float a = 1.5f*(rand.nextInt(3)-1);
			ry += a;
			removePointsCircle(240,(int)(510+ry),15);
			rotation += 0.05;
			//rand.nextFloat()
		}
		*/

		pixmap.setColor(Color.alpha(1f));
		pixmap.fillCircle(size / 2, size / 2, size / 2);
		preChunk.size = size;


	}

	//remove a circle, fading from center
	public void removePointsFadedCircle(float x, float y, float bombwidth){

		x  -= pos.x;
		y  -= pos.y;

		double rot = 2* Math.PI - rotation ;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		x_ += size/2;
		y_  = size - y_ - size/2;

		float Rpixel;
		float r = 0;
		float a = 0;

		for (int i = (int) -bombwidth; i < bombwidth; i++){

			for(int j = (int) -bombwidth; j < bombwidth; j++){

				a = new Color(pixmap.getPixel((int) x_ + i, (int) y_ + j)).a;
				float distance = ((float) (i*i + j*j)) / (bombwidth*bombwidth);
				Rpixel = distance > 1 ? 0 : 1;
				Rpixel *= (1-distance);

				a -= Rpixel;
				if (a <= 0)
					a = 0;

				pixmap.drawPixel((int) x_ + i, (int) y_ + j, Color.alpha(a));

			}
		}
	}
	
	public void removePointsBomb(float x, float y, int type) {
		
		x  -= pos.x; 
		y  -= pos.y;

		double rot = 2* Math.PI - rotation ;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));
		
		x_ += size/2;
		y_  = size - y_ - size/2;

		float Rpixel;
		float r = 0;
		float a = 0;
		int bombwidth;
		
		switch (type) {

			case 1 : bb = bbl; bombwidth = 5;  break;
			case 2 : bb = bb1; bombwidth = 30; break;
			case 3 : bb = bb2; bombwidth = 40; break;

			default: return;
		
		}

		for (int i = 0;i < bombwidth; i++){
			
			for(int j = 0; j < bombwidth;j++){
				

				a = new Color(pixmap.getPixel((int) x_ + i - bombwidth / 2, (int) y_ + j - bombwidth / 2)).a;
				Rpixel = (bb.getInt() >>> 24) / 255f;
				
				a -= Rpixel;
				if (a <= 0)
					a = 0;

				pixmap.drawPixel((int) x_ + i - bombwidth/2, (int) y_ + j - bombwidth/2, Color.alpha(a));

			}
		}

		bb.clear();
		
		

	}
	
	public void rotate(float delta) {
		
		rotation = (float) ((rotation + rotationDelta * delta) % (Math.PI*2));
	}

	public void updateChunks(float delta) {

		for (Iterator<Chunk> it = chunks.iterator();it.hasNext();) {
			Chunk c = it.next();

			c.pos.add(c.vel.cpy().scl(delta));
			//c.pos.sub(vel);
			c.rotation = (float) ((c.rotation + c.rotationDelta * delta) % (Math.PI*2));
		}
	}


	public Vector2 getPos() {
		// TODO Auto-generated method stub
		return pos.cpy();
	}

	public void removePointsCircle(int x, int y, int radius) {
		
		x  -= pos.x; 
		y  -= pos.y;

		double rot = 2* Math.PI - rotation ;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));
		
		x_ += size/2;
		y_  = size - y_ - size/2;

		pixmap.setColor(Color.CLEAR);
		pixmap.fillCircle((int) x_, (int) y_, radius);
		
	}

	public void checkIntegrityStage1() throws NullPointerException{
		//pass one
		//label each component
		checkingIntegrity = true;

		for (int[] line : labels)
			Arrays.fill(line,0);

		allLabels = new HashMap<Integer, CLabel>();

		labelCount = 1;
		Color c;

		for (int j = 0;j<height;j++){
			for (int i = 0;i<width;i++){
				
				c = new Color(pixmap.getPixel(i,j));

				if (c.a  == 0)
					continue;

				neighborLabels(i,j);
				int currentLabel;
				
				if (neighborCount == 0){
					currentLabel = labelCount;
					allLabels.put(currentLabel, new CLabel(currentLabel));
					labelCount ++;
				}
				else
				{

					currentLabel = minNeighbor;
					CLabel root = allLabels.get(currentLabel).getRoot();


					for(int k = 0; k < neighborCount; k++){

						if (root.name != allLabels.get(neighborArray[k]).getRoot().name){
							allLabels.get(neighborArray[k]).Join(allLabels.get(currentLabel));
						}

					}
					
				}
				
				labels[i][j] = currentLabel;

			}
		}	
	}

	private void neighborLabels(int x, int y) {
		
		minNeighbor = 90;
		neighborCount = 0;


		if(y - 1 > 0 && x - 1 > 0 && labels[x - 1][y - 1] != 0){

			if(labels[x-1][y-1] < minNeighbor)
				minNeighbor = labels[x-1][y-1];

			neighborArray[neighborCount++] = labels[x-1][y-1];

		}

		if(y - 1 > 0 && labels[x][y - 1] != 0){

			if(labels[x][y-1] < minNeighbor)
				minNeighbor = labels[x][y-1];

			neighborArray[neighborCount++] = labels[x][y-1];

		}

		if(y - 1 > 0 && x + 1 < width && labels[x + 1][y - 1] != 0){

			if(labels[x+1][y-1] < minNeighbor)
				minNeighbor = labels[x+1][y-1];

			neighborArray[neighborCount++] = labels[x+1][y-1];

		}

		if(x - 1 > 0 && labels[x - 1][y] != 0){

			if(labels[x-1][y] < minNeighbor)
				minNeighbor = labels[x-1][y];

			neighborArray[neighborCount++] = labels[x-1][y];

		}

	}


	public void checkIntegrityStage2() {
		//pass two
		//label each component with equivalent labels
		patterns = new HashMap<Integer, preChunk>();
		
	    for (int j = 0; j < height; j++)
	    {
	        for (int i = 0; i < width; i++)
	        {
	        	
	        	 int patternNumber = labels[i][j];
	        	 
	        	 if (patternNumber != 0) {
	        		 
	        		 patternNumber = allLabels.get(patternNumber).getRoot().name;
	        		 
	        		 if ( !patterns.containsKey(patternNumber) ) {
	        			 patterns.put(patternNumber, new preChunk());
	        		 }
	        		 
	        		 patterns.get(patternNumber).add(new Vector2(i,j));
	        	 }
	    
	        	
	        }
	    }
	        
	}



	public void checkIntegrityStage3() {
		//create the pixmaps, send them on their way
		Pixmap chunkmap;
		Texture texture;
		centreIntact = false;

		for (Iterator<Integer> it = patterns.keySet().iterator(); it.hasNext();)
		{
			preChunk prechunk = patterns.get(it.next());

			float chwidth = prechunk.maxx-prechunk.minx;
			float chheight = prechunk.maxy-prechunk.miny;
			
			if(!prechunk.main) {

				chunkmap = new Pixmap(prechunk.maxx-prechunk.minx, prechunk.maxy-prechunk.miny, Format.Intensity);
				
				for (Iterator<Vector2> lit = prechunk.l.iterator(); lit.hasNext();){
					
					Vector2 v = lit.next();

					chunkmap.setColor(new Color(pixmap.getPixel((int) v.x, (int) v.y)));
					chunkmap.drawPixel((int) (v.x - prechunk.minx), (int) (v.y - prechunk.miny));

					pixmap.setColor(Color.CLEAR);
					pixmap.drawPixel((int) v.x, (int) v.y);

					//debuggin

				}



				//texture.
				float chx = prechunk.minx - width/ 2 + chwidth/2;
				float chy = height/2 - prechunk.maxy + chheight/2;

				double rot = -(2 * Math.PI - rotation);
				//rot = rotation;
				float chx_ = (float) (chx * Math.cos(rot) - chy * Math.sin(rot));
				float chy_ = (float) (chx * Math.sin(rot) + chy * Math.cos(rot));

				chx_ += pos.x;
				chy_ += pos.y;

				chunks.add(new Chunk(new Texture(chunkmap), new Vector2(chx_ - chwidth / 2,chy_ - chheight / 2),new Vector2((float) (random.nextFloat()*0.5-0.25),(float) (random.nextFloat()*0.5-0.25)).sub(vel),rotation,0.03f));
				chunkmap.dispose();
			}

			else
				centreIntact = true;

		}
		checkingIntegrity = false;
	}


	public void goToStart(LevelInfo levelInfo) {

		vel = new Vector2(0,-0.1f);
		distance = 0;
	}
}
