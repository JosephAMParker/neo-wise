package com.neowise.game.homeBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.neowise.game.gameObject.GameObject;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.LevelInfo.LevelInfo;
import com.neowise.game.gameObject.weaponProjectile.Bomb;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

public class HomeBase extends GameObject {

	public float mass;
	public int size;
	public float distance;
	public double rotation, rotationDelta; //current rotation of the planet
	public float playerOrbitRange, enemyOrbitRange;
	public boolean checkIntegrity;
	public Pixmap pixmap;
	public HomeBaseCore core;
	public boolean alive = true;		// is core health > 0
	public boolean destroyed = false;   // after core dies, destroy asteroid for effect, then end level.

	private float destroyTimer, destroyTimerReset;

	HashMap<Integer, CLabel> allLabels;
	HashMap<Integer,preChunk> patterns;
	public ArrayList<Chunk> chunks;

	private Collection<Defender> friendlyTurrets;
	PixMapController pixmapController;
	
	int[][] labels;
	int[] neighborArray;
	int neighborCount = 0;
	int minNeighbor = 10;
	int labelCount;

	public boolean checkingIntegrity;
	public boolean checkingFinished;

	public HomeBase(int size) {

		pixmapController = new PixMapController(this);

		checkingFinished = false;
		checkingIntegrity = false;
		checkIntegrity = false;


		rotation = 0;
		playerOrbitRange = size * 0.6f;
		enemyOrbitRange  = size;
		rotationDelta = 0.0f;
		pos = new Vector2(0,0);
		vel = new Vector2(0,-1);
		distance = 0;
		this.size = size;
		mass = 50;
		friendlyTurrets = new ArrayList<>();
		core = new HomeBaseCore(pos, 1000);

		//initializeHomeBasePixMap();
		pixmapController.init();
		
		chunks = new ArrayList<Chunk>();
		labels = new int[size][size];
		neighborArray = new int[5];

		pixmap = pixmapController.getPixmap();

		destroyTimerReset = 0.5f;
		destroyTimer = 0;
	}

	public Pixmap getPixmap(){
		return pixmapController.getPixmap();
	}

	public void addHealthBombPoints(float x, float y, int bombRadius, int healAmount){

		double rot = 2* Math.PI - rotation ;

		double cos = Math.cos(rot), sin = Math.sin(rot);

		float x_ = (float) (x * cos - y * sin);
		float y_ = (float) (x * sin + y * cos);

		if(x_*x_ + y_*y_ + 2000 >= size*size/4)
			return;

		x_ += size/2;
		y_  = size/2 - y_;

		pixmapController.addHealthBombPoints((int) x_, (int) y_, bombRadius, healAmount);
	}

	public void removePointsLava(float x, float y, int bombRadius){

		x  -= pos.x;
		y  -= pos.y;

		double rot = 2* Math.PI - rotation ;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		x_ += size/2 - bombRadius/2;
		y_  = size/2 - y_ - bombRadius/2;

 		pixmapController.removePointsLava(x_, y_, bombRadius);
	}

	public void removePointsBomb(float x, float y, int bombRadius, int centerHoleRadius, boolean clearCenter){

		double rot = 2* Math.PI - rotation ;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		x_ += size/2 ;
		y_  = size/2 - y_ ;

		if(clearCenter || CollisionDetector.collisionCircleCircle(core.pos.x, core.pos.y, core.radius, x,y,centerHoleRadius)) {
			pixmapController.removePointsBomb(pixmapController.p2, x_, y_, bombRadius, centerHoleRadius, true);
			return;
		}

		pixmapController.removePointsBomb(pixmapController.p2, x_, y_, bombRadius, centerHoleRadius, false);

	}
	
	public void rotate(float delta) {
		
		rotation = (float) ((rotation + rotationDelta * delta) % (Math.PI*2));
	}

	public void updateChunks(float delta) {

		for (Iterator<Chunk> it = chunks.iterator();it.hasNext();) {
			Chunk c = it.next();

			if(c.pos.len2() > Constants.REMOVE_DISTANCE2){
				it.remove();
				continue;
			}

			c.pos.add(c.vel.cpy().scl(delta));
			c.rotation = (float) ((c.rotation + c.rotationDelta * delta) % (Math.PI*2));
		}
	}


	public Vector2 getPos() {
		return pos.cpy();
	}

	public void checkIntegrityStage1() throws NullPointerException{
		//pass one
		//label each component

		for (int[] line : labels)
			Arrays.fill(line,0);

		allLabels = new HashMap<Integer, CLabel>();

		labelCount = 1;
		Color c;

		for (int j = 0;j<size;j++){
			for (int i = 0;i<size;i++){

				if ((pixmap.getPixel(i,j) & 0x000000ff) == 255)
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
		if(y - 1 > 0 && x + 1 < size && labels[x + 1][y - 1] != 0){

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
		
	    for (int j = 0; j < size; j++)
	    {
	        for (int i = 0; i < size; i++)
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
				chunkmap.setColor(Color.alpha(1));
				chunkmap.fill();
				chunkmap.setBlending(Pixmap.Blending.None);
				
				for (Iterator<Vector2> lit = prechunk.l.iterator(); lit.hasNext();){
					
					Vector2 v = lit.next();

					chunkmap.setColor(pixmap.getPixel((int) v.x, (int) v.y));
					chunkmap.drawPixel((int) (v.x - prechunk.minx), (int) (v.y - prechunk.miny));

					pixmap.setColor(Color.alpha(1));
					pixmap.drawPixel((int) v.x, (int) v.y);

				}

				//texture.
				float chx = prechunk.minx - size/ 2 + chwidth/2;
				float chy = size/2 - prechunk.maxy + chheight/2;

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

	public void resetHomeBasePixMap() {
		pixmapController.resetHomeBasePixmap();
		pixmap = pixmapController.getPixmap();
	}

	public void update(BasicLevel basicLevel, float delta) {

		if(!basicLevel.playerShip.flyToPosition)
			speedUpHomeBase(delta);

		distance += vel.y * delta;
		rotate(delta);
		updateChunks(delta);
		pixmapController.update();

		//updateCore();
		if(core.health <= 0){
			destroy(basicLevel, delta);
			alive = false;
		}

		if(checkingFinished){
			checkingFinished = false;
			basicLevel.drawingBoard.updateTexture(getPixmap());
		}
	}

	private void destroy(BasicLevel basicLevel, float delta) {

		destroyTimer -= delta;

		if(destroyTimer < 0) {
			destroyTimer = RandomUtil.nextFloat() * 0.8f;
			Vector2 randomSpot;
			for (int i = 0; i < 100; i++) {
				randomSpot = new Vector2(0, RandomUtil.nextInt(size) + core.radius * 3);
				randomSpot.rotateDeg(RandomUtil.nextInt(360));

				if (CollisionDetector.collisionPointPixmap(randomSpot.cpy(), this)) {
					basicLevel.hostileProjectiles.add(new Bomb(randomSpot, 50, 0, 0, 3));
					return;
				}
			}
		}
	}

	public void resetDefences() {
		for(Defender d : friendlyTurrets){
			d.reset();
		}
	}

	/**
	 * Speed up homebase to cruising speed.
	 * @param delta
	 */
	private void speedUpHomeBase(float delta){

		if(vel.len2() < 80000)
			vel.add(0,- 300 * delta);
	}

	public boolean isAlive() {
		return alive;
	}
}
