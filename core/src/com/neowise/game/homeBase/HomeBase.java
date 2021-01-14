package com.neowise.game.homeBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.GameObject;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.LevelInfo.LevelInfo;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.util.RandomUtil;

public class HomeBase extends GameObject {

	public float mass;
	int size;
	public float distance;
	public double rotation, rotationDelta; //current rotation of the planet
	public float playerOrbitRange, enemyOrbitRange;
	public boolean checkIntegrity;
	public Pixmap pixmap;
	public HomeBaseCore core;

	HashMap<Integer, CLabel> allLabels;
	HashMap<Integer,preChunk> patterns;
	public ArrayList<Chunk> chunks;

	private Collection<Defender> friendlyTurrets;
	
	int[][] labels;
	int[] neighborArray;
	int neighborCount = 0;
	int minNeighbor = 10;
	int labelCount;
	
	int height;
	int width;
	public boolean checkingIntegrity;
	public boolean checkingFinished;

	public HomeBase(int size) {

		checkingFinished = false;
		checkingIntegrity = false;
		checkIntegrity = false;

		rotation = 0;
		playerOrbitRange = size * 0.6f;
		enemyOrbitRange  = size;
		rotationDelta = 0.0f;
		pos = new Vector2(0,0);
		vel = new Vector2(0,-0.1f);
		distance = 0;
		this.size = size;
		mass = 50;
		friendlyTurrets = new ArrayList<>();
		core = new HomeBaseCore(pos, 1000);

		initializeHomeBasePixMap();

		height = pixmap.getHeight();
		width = pixmap.getWidth();
		
		chunks = new ArrayList<Chunk>();
		labels = new int[size][size];
		neighborArray = new int[5];
	}

	public void initializeHomeBasePixMap(){

		//pixmap: the planets structure data is stored here. Cells with values (colors) greater than BLACK are considered on.
		// All others are off and not used in collision detection, and alpha is set to 0.
		pixmap = new Pixmap(size, size, Format.Intensity);
		pixmap.setBlending(Blending.None);
		setInitialPixmapCircle();
		randomizePixMapEdges();
		setCheckIntegrity();
	}

	private void randomizePixMapEdges(){

		Vector2 removePoint;
		for (int i = 0; i<360; i++){
			removePoint = new Vector2(0, size/2 + 20 + RandomUtil.nextInt(60));
			removePoint.rotateDeg(i);
			if(RandomUtil.nextInt(10) > 5)
				removePointsBomb(removePoint.x, removePoint.y, 40, 0.5f);
			else
				removePointsCircle(removePoint.x, removePoint.y, 20);
		}
	}

	private void setInitialPixmapCircle(){
		int c = (0 << 24) | (0 << 16) | (0 << 8) | 254;
		pixmap.setColor(c);
		pixmap.fillCircle(size / 2, size / 2, size / 2);
		preChunk.size = size;

		c = (100 << 24) | (10 << 16) | (0 << 8) | 255;
		pixmap.setColor(c);
		pixmap.fillCircle(size / 2, size / 2, (int) core.radius);
	}

	public boolean addHealthBombPoints(float x, float y, int bombRadius, int healAmount){

		x  -= pos.x;
		y  -= pos.y;

		double rot = 2* Math.PI - rotation ;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		if(x_*x_ + y_*y_ + 2000 >= size*size/4)
			return false;

		x_ += size/2;
		y_  = size/2 - y_;



		Boolean didHit = false;

		int a;
		for (int i = -bombRadius; i < bombRadius; i++) {
			for (int j = -bombRadius; j < bombRadius; j++) {
				float distance = ((float) (i*i + j*j)) / (bombRadius*bombRadius);
				//if(((x_ + i - size/2)*(x_ + i - size/2) + (size/2 - y_ + j)*(size/2 - y_ + j)) < size*size/4) {

					if (distance < 1) {
						a = pixmap.getPixel((int) x_ + i, (int) y_ + j) & 0x000000ff;
						didHit = true;
						a += healAmount;
						a = a < 254 ? a : 254;
						int c = (0 << 24) | (0 << 16) | (0 << 8) | a;
						pixmap.drawPixel((int) x_ + i, (int) y_ + j, c);
					}


			}
		}

		return didHit;
	}

	public boolean removePointsLava(float x, float y, int bombRadius, int lavaDamage){

		x  -= pos.x;
		y  -= pos.y;

		double rot = 2* Math.PI - rotation ;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		x_ += size/2;
		y_  = size - y_ - size/2;

		Boolean didHit = false;

		for (int i = -bombRadius; i < bombRadius; i++) {
			for (int j = -bombRadius; j < bombRadius; j++) {
				int a = pixmap.getPixel((int) x_ + i, (int) y_ + j) & 0x000000ff;

				if (a > 0 && a < 255) {
					float distance = ((float) (i*i + j*j)) / (bombRadius*bombRadius);
					if(distance < 1) {
						didHit = true;
						a -= lavaDamage * (1-distance);
						a = a > 0 ? a : 0;
						int c = (0 << 24) | (0 << 16) | (0 << 8) | a;
						pixmap.drawPixel((int) x_ + i, (int) y_ + j, c);
					}
				}
			}
		}

		return didHit;
	}

	public void removePointsBomb(float x, float y, int bombRadius, float centerHoleRadius, boolean removeCenter){

		x  -= pos.x;
		y  -= pos.y;

		double rot = 2* Math.PI - rotation ;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		x_ += size/2;
		y_  = size/2 - y_;

		for (int i = -bombRadius; i < bombRadius; i++) {
			for (int j = -bombRadius; j < bombRadius; j++) {
				float distance = ((float) (i*i + j*j)) / (bombRadius*bombRadius);
				int a = pixmap.getPixel((int) x_ + i, (int) y_ + j) & 0x000000ff; //alpha value of pixel

				if (a > 0 && a < 255) {

					//cut out a hole (set alpha to 0 in a ring) of radius centerholeradius
					if(distance < centerHoleRadius)
						a -= 255;

					// cause damage closer to center, fading further out.
					if (distance > centerHoleRadius && distance < 1){
						distance = (1 - distance);
						distance += (RandomUtil.nextFloat() * 0.5f) * distance;

						a -= distance * 254;// + random.nextInt(5);
					}

					//ensure no negative alpha;
					//create new color, RGBA8888 => 000a
					a = a > 0 ? a : 0;
					int c = (0 << 24) | (0 << 16) | (0 << 8) | a;
					pixmap.drawPixel((int) x_ + i, (int) y_ + j, c);
				}
			}
		}
	}

	public boolean removePointsBomb(float x, float y, int bombRadius, float centerHoleRadius){

		if(CollisionDetector.collisionCircleCircle(core.pos.x, core.pos.y, core.radius, x,y,centerHoleRadius * bombRadius)) {
			removePointsBomb(x, y, bombRadius, centerHoleRadius, true);
			return true;
		}

		Boolean didHit = false;

		x  -= pos.x;
		y  -= pos.y;

		double rot = 2* Math.PI - rotation ;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		x_ += size/2;
		y_  = size - y_ - size/2;

		for (int i = -bombRadius; i < bombRadius; i++) {
			for (int j = -bombRadius; j < bombRadius; j++) {
				float distance = ((float) (i*i + j*j)) / (bombRadius*bombRadius);
				int a = pixmap.getPixel((int) x_ + i, (int) y_ + j) & 0x000000ff; //alpha value of pixel
				if (a > 0 && a < 255) {

					//cut out a hole (set alpha to 0 in a ring) of radius centerholeradius
					if(distance < centerHoleRadius && distance > centerHoleRadius*0.5) {
						a -= 255;
						didHit = true;
					}

					//for the rest, cause damage closer to center, fading further out.
					else if (distance > centerHoleRadius && distance < 1){
						distance = (1 - distance);
						distance += (RandomUtil.nextFloat() * 0.5f) * distance;
						didHit = true;
						a -= distance * 250;// + random.nextInt(5);
					}

					//ensure no negative alpha;
					//create new color, RGBA8888 => 000a
					a = a > 0 ? a : 0;
					int c = (0 << 24) | (0 << 16) | (0 << 8) | a;
					pixmap.drawPixel((int) x_ + i, (int) y_ + j, c);
				}
			}
		}

		return didHit;
	}

	public void removePointsCircle(float x, float y, int radius) {

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
		return pos.cpy();
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

				chunks.add(new
						Chunk(new Texture(chunkmap),  //texture
						      new Vector2(chx_ - chwidth / 2,chy_ - chheight / 2), //pos
						      new Vector2(RandomUtil.nextFloat2() * 5, 25+RandomUtil.nextInt(25)), //vel
						      rotation,
						      RandomUtil.nextFloat2()));
				chunkmap.dispose();
			}

		}
		checkingIntegrity = false;
		checkingFinished  = true;
	}


	public void goToStart(LevelInfo levelInfo) {

		vel = new Vector2(0,-0.1f);
		distance = 0;
	}

	public Collection<Defender> getDefences() {
		return friendlyTurrets;
	}

	public void setCheckIntegrity() {
		checkIntegrity = true;
	}

	public boolean checkIntegrity() {
		return checkIntegrity && !checkingIntegrity;
	}
}
