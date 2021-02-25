package com.neowise.game.draw;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
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

	final private SpriteBatch backgroundBatch, batch, homeBaseBatch, hudBatch, textBatch;
	private static ArrayList<Sprite> backgroundSprites, sprites, homeBaseSprites, menuSprites, hudSprites;
	private static ArrayList<Text> menuTexts, hudTexts;
	public OrthographicCamera camera;
	public OrthographicCamera hudCamera;
	Texture homeBaseTexture;
	Sprite homeBaseSprite;
	static public TextureAtlas atlas;

	public DrawingBoard(){

		FileHandle vertexShader = Gdx.files.internal("vertex.glsl");
		FileHandle fragmentShader = Gdx.files.internal("fragment.glsl");
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		shader.pedantic = false;

		if (!shader.isCompiled()) {
			Gdx.app.log("Shader", shader.getLog());
			Gdx.app.exit();
		}

		batch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		backgroundBatch = new SpriteBatch();
		homeBaseBatch = new SpriteBatch();
		textBatch = new SpriteBatch();
		homeBaseBatch.setShader(shader);

		shader.bind();
		shader.setUniformi("u_texture", 0);
		shader.setUniformi("u_mask", 123456789);

		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		sprites = new ArrayList<>();
		homeBaseSprites = new ArrayList<>();
		menuSprites = new ArrayList<>();
		backgroundSprites = new ArrayList<>();
		menuTexts = new ArrayList<>();

	}

	public static void addText(Text t, Collection<Text> texts) {
		texts.add(t);
	}

	public static Collection<Sprite> getHudSprites() {
		return hudSprites;
	}

	public static Collection<Text> getHudTexts() {
		return hudTexts;
	}

	public void setPixMap(Pixmap pixmap){
		homeBaseTexture = new Texture(pixmap);
		homeBaseSprite = new Sprite(homeBaseTexture);
		homeBaseTexture.bind(123456789);
	}

	public static void initAtlas(){
		atlas = new TextureAtlas(Gdx.files.internal("TextureAtlasOut/devPack.atlas"));
	}

	public static Sprite createSprite(String spriteName) {
		return atlas.createSprite(spriteName);
	}

	public void updateTexture(Pixmap pixmap){
		homeBaseTexture.draw(pixmap, 0, 0);
		homeBaseSprite.setRegion(homeBaseTexture);
	}
	
	public void updateCamera(OrthographicCamera cam, OrthographicCamera hudCam){
		batch.setProjectionMatrix(cam.combined);
		backgroundBatch.setProjectionMatrix(cam.combined);
		homeBaseBatch.setProjectionMatrix(cam.combined);
		hudBatch.setProjectionMatrix(hudCam.combined);
		textBatch.setProjectionMatrix(hudCam.combined);
	}

	public void drawHomeBase(float x, float y, double rotation) {
		homeBaseSprite.setRotation((float) (rotation * 180 / Math.PI));
		homeBaseSprites.add(homeBaseSprite);
	}

	public void setHomeBasePos(Vector2 pos) {
		homeBaseSprite.setX(pos.x - homeBaseSprite.getWidth() / 2);
		homeBaseSprite.setY(pos.y - homeBaseSprite.getHeight() / 2);
	}

	public static Collection<Sprite> getBackgroundSprites(){
		return backgroundSprites;
	}

	public static Collection<Sprite> getMenuSprites(){
		return menuSprites;
	}

	public static Collection<Text> getMenuTexts() {return menuTexts; }

	public static Collection<Sprite> getSprites(){
		return sprites;
	}

	public void drawChunks(ArrayList<Chunk> ch) {

		for (Chunk c : ch) {
			Sprite chunkSprite = new Sprite(c.texture);
			chunkSprite.setPosition(c.pos.x, c.pos.y);
			chunkSprite.setRotation((float) (c.rotation * 180 / Math.PI));

			homeBaseSprites.add(chunkSprite);
		}
	}

	public void drawBackGround(){
		backgroundBatch.begin();
		for (Sprite sp : backgroundSprites) {
			sp.draw(backgroundBatch);
		}
		backgroundBatch.end();
		backgroundSprites.clear();
	}

	public void draw(){

		homeBaseBatch.begin();
		for (Sprite sp : homeBaseSprites) {
			sp.draw(homeBaseBatch);
		}
		homeBaseBatch.end();
		homeBaseSprites.clear();

		batch.begin();
		for (Sprite sp : sprites) {
			sp.draw(batch);
		}
		batch.end();
		sprites.clear();

		hudBatch.begin();
		for (Sprite sp : menuSprites) {
			sp.draw(hudBatch);
		}
		for (Text tx : menuTexts) {
			tx.font.draw(hudBatch, tx.text, tx.x, tx.y);
		}
		hudBatch.end();
		menuSprites.clear();
		menuTexts.clear();

	}

	public static void addSprite(Sprite sprite, Collection<Sprite> spritesCollection){
		spritesCollection.add(sprite);
	}

	public static void addSpriteFromAtlas(float x, float y, float rotation, Sprite sprite, Collection<Sprite> spritesCollection) {
		addSpriteFromAtlas(x, y, rotation, 1, sprite, spritesCollection);
	}

	public static void addSpriteFromAtlas(float x, float y, float rotation, float scale, Sprite sprite, Collection<Sprite> spritesCollection) {
		sprite.setOriginCenter();
		sprite.setRotation(0);
		sprite.rotate(rotation);
		sprite.setPosition(x, y);
		sprite.setScale(scale);
		spritesCollection.add(sprite);
	}

	public static void addSpriteFromAtlas(float x, float y, float width, float height, float alpha, float rotation, Sprite sprite, Collection<Sprite> spritesCollection){
		sprite.setOriginCenter();
		sprite.setRotation(0);
		sprite.rotate(rotation);
		sprite.setPosition(x, y);
		sprite.setBounds(x,y,width,height);
		sprite.setAlpha(alpha);
		spritesCollection.add(sprite);
	}

	public SpriteBatch getTextBatch() {
		return textBatch;
	}
}
