package com.neowise.game.draw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.weaponProjectile.Bullet;
import com.neowise.game.gameObject.weaponProjectile.Laser;
import com.neowise.game.gameObject.weaponProjectile.PlayerCityBomb;
import com.neowise.game.gameObject.weaponProjectile.TurretCityBomb;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.homeBase.Chunk;

/**
 * 
 * @author jap18
 *	final drawing class to be used.
 *  --current
 *  Used to draw HomeBase's pixmap's texture and
 *  add rotation, get BoundingBox as needed.
 *  --to add 
 *  Used to draw...
 *  ...
 *  Ships
 *  Lasers
 *  Bullets
 *  Explosions
 *  All else
 *  ...
 */
public class DrawingBoard {
	
	private SpriteBatch batch, homeBaseBatch;
	Texture homeBaseTexture;
	Sprite homeBaseSprite;
	private Matrix4 mat = new Matrix4();
	static public TextureAtlas atlas;
	private Collection<Sprite> sprites, homeBaseSprites, spritesForeGround;
	private FileHandle vertexShader, fragmentShader;
	private ShaderProgram shader;

	public DrawingBoard(Pixmap pixmap){
		
		homeBaseTexture = new Texture(pixmap);
		homeBaseSprite = new Sprite(homeBaseTexture);
		//homeBaseSprite.setOrigin(homeBaseSprite.getHeight() / 2, homeBaseSprite.getHeight() / 2);

		vertexShader = Gdx.files.internal("vertex.glsl");
		fragmentShader = Gdx.files.internal("fragment.glsl");
		shader = new ShaderProgram(vertexShader, fragmentShader);
		shader.pedantic = false;

		if (!shader.isCompiled()) {
			Gdx.app.log("Shader", shader.getLog());
			Gdx.app.exit();
		}

		batch = new SpriteBatch();
		homeBaseBatch = new SpriteBatch();
		homeBaseBatch.setShader(shader);


		shader.begin();
		shader.setUniformi("u_texture", 0);
		shader.setUniformi("u_mask", 1);
		shader.end();

		homeBaseTexture.bind(1);

		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

		sprites = new ArrayList();
		homeBaseSprites = new ArrayList();
		spritesForeGround = new ArrayList();

		atlas = new TextureAtlas(Gdx.files.internal("TextureAtlasOut/atlas.atlas"));

	}
	
	public void updateTexture(Pixmap pixmap){
		
		homeBaseTexture.draw(pixmap, 0, 0);
		homeBaseSprite.setRegion(homeBaseTexture);
	}
	
	public void updateCamera(OrthographicCamera cam){
	//	this.cam  = cam;
		batch.setProjectionMatrix(cam.combined);
		homeBaseBatch.setProjectionMatrix(cam.combined);
	}

	public void drawHomeBase(float x, float y, double rotation) {

		homeBaseSprite.setRotation((float) (rotation * 180 / Math.PI));
		homeBaseSprites.add(homeBaseSprite);

	}

	public void setMatrix(Vector2 pos, float rotation) {
		
		mat.idt();
		mat.translate(pos.x, pos.y, 0);
		mat.rotate(1, 1, 0, (float) (rotation * 180 / Math.PI));
		
	}

	public void setHomeBasePos(Vector2 pos) {
		homeBaseSprite.setX(pos.x - homeBaseSprite.getWidth() / 2);
		homeBaseSprite.setY(pos.y - homeBaseSprite.getHeight() / 2);

	}

	public void drawChunks(ArrayList<Chunk> ch) {

		for (Iterator<Chunk> it = ch.iterator();it.hasNext();)
		{
			Chunk c = it.next();
			
			Sprite chunkSprite = new Sprite(c.texture);
			chunkSprite.setPosition(c.pos.x, c.pos.y);
			chunkSprite.setRotation((float) (c.rotation * 180 / Math.PI));

			homeBaseSprites.add(chunkSprite);

		}
		
	}

	public void draw(){

		batch.begin();
		for(Iterator<Sprite> spit = sprites.iterator(); spit.hasNext(); ){
			Sprite sp = spit.next();
			sp.draw(batch);
		}
		batch.end();
		sprites.clear();

		homeBaseBatch.begin();
		for(Iterator<Sprite> spit = homeBaseSprites.iterator(); spit.hasNext(); ){
			Sprite sp = spit.next();
			sp.draw(homeBaseBatch);
		}
		homeBaseBatch.end();
		homeBaseSprites.clear();

		batch.begin();
		for(Iterator<Sprite> spit = spritesForeGround.iterator(); spit.hasNext(); ){
			Sprite sp = spit.next();
			sp.draw(batch);
		}
		batch.end();
		spritesForeGround.clear();

	}


	public void addSpriteFromAtlas(float x, float y, String atlasKey) {

		Sprite sprite = atlas.createSprite(atlasKey);
		sprite.setPosition(x, y);
		sprites.add(sprite);

	}

	public void addSpriteFromAtlas(float x, float y, float rotation, Sprite sprite) {
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setOriginCenter();
		sprite.setRotation(0);
		sprite.rotate(rotation);
		sprite.setPosition(x, y);

		sprites.add(sprite);
	}

	public void addSpriteFromAtlas(float x, float y, float rotation,float scale,Sprite sprite) {
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setOriginCenter();
		sprite.setRotation(0);
		sprite.rotate(rotation);
		sprite.setPosition(x, y);
		sprite.setScale(scale);


		sprites.add(sprite);
	}

	public void addSpriteFromAtlasToForeGround(float x, float y, float rotation,Sprite sprite) {
		addSpriteFromAtlasToForeGround(x, y, rotation, 1, sprite);
	}

	public void addSpriteFromAtlasToForeGround(float x, float y, float rotation,float scale,Sprite sprite) {
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setOriginCenter();
		sprite.setRotation(0);
		sprite.rotate(rotation);
		sprite.setPosition(x, y);
		sprite.setScale(scale);


		spritesForeGround.add(sprite);
	}

	public void drawProjectileShapeRenderer(WeaponProjectile fprojectile, ShapeRenderer shapeRenderer) {

		if (fprojectile instanceof TurretCityBomb){

			shapeRenderer.identity();
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.circle(fprojectile.pos.x, fprojectile.pos.y, fprojectile.size);

		}

		if (fprojectile instanceof Bullet){

			shapeRenderer.identity();
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.circle(fprojectile.pos.x, fprojectile.pos.y, fprojectile.size);

		}

		if (fprojectile instanceof PlayerCityBomb){

			shapeRenderer.identity();
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.circle(fprojectile.pos.x, fprojectile.pos.y, fprojectile.size);
		}

		if (fprojectile instanceof Laser){

			shapeRenderer.identity();
			shapeRenderer.setColor(Color.CYAN);
			Vector2 endLaser = fprojectile.getPos().add(fprojectile.getVel().nor().scl(((Laser) fprojectile).length));
			shapeRenderer.rectLine(endLaser.x, endLaser.y,fprojectile.pos.x, fprojectile.pos.y, 3);

		}
	}
}
