package com.neowise.game.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.neowise.game.draw.BackgroundObject;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.player.PlayerShip;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.physics.Physics;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public abstract class GameLevelObject implements Screen, InputProcessor {

    public final NeoWiseGame game;

    // Camera, Stage and graphics related fields
    public OrthographicCamera camera;
    public ShapeRenderer shapeRenderer;
    public ShapeRenderer hudRenderer;
    public SpriteBatch batch;
    public com.neowise.game.draw.DrawingBoard drawingBoard;
    public Stage stage;

    // animations
    public Collection<com.neowise.game.draw.MyAnimation> backAnimations;
    public Collection<com.neowise.game.draw.MyAnimation> middleAnimations;
    public Collection<com.neowise.game.draw.MyAnimation> frontAnimations;

    // back+foreground objects
    public Collection<com.neowise.game.draw.BackgroundObject> backgroundObjects;
    public Collection<BackgroundObject> foregroundObjects;

    // useful global variables
    public float w = Gdx.graphics.getWidth();
    public float h = Gdx.graphics.getHeight();
    public float playTop, playBottom, playLeft, playRight;
    public Random random;
    public Color bgColor;
    public boolean slow;
    public float maxDelta = 0;
    public boolean gameOver = false;

    // check wholeness of planet
    public boolean updateBoundingBox = true;
    public boolean checkIntegrity = false;

    // basic game objects
    public HomeBase homeBase;
    public Pixmap pixmap;
    public PlayerShip playerShip;
    public CollisionDetector collisionDetector;

    public GameLevelObject(final NeoWiseGame game) {

        this.game = game;
        this.homeBase = game.getHomeBase();
        this.playerShip = game.getPlayerShip();
        Physics.mass = homeBase.mass;

        initializeCamera();
        initializeGlobalVariables();
        initializeText();
        initializeRenderVariables();
    }

    public void setInput(){
        stage = new Stage();
        InputMultiplexer mult = new InputMultiplexer();
        mult.addProcessor(stage);
        mult.addProcessor(this);
        Gdx.input.setInputProcessor(mult);
    }

    /**
     *  set starting values for global variables
     */
    private void initializeGlobalVariables(){

        random = new Random();
        playTop    =  (h * 1.5f) + w * 0.5f;
        playBottom = -h*0.2f;
        playRight  =  h * 1.5f;
        playLeft   =  -h * 1.5f;

        bgColor = new Color(0.01f,0.01f,0.02f,1);
        slow = false;

        setInput();

        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
    }


    /**
     *  set camera to game view
     *  move to subclass
     */
    public void initializeCamera() {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();
        hudRenderer   = new ShapeRenderer();
        hudRenderer.setProjectionMatrix(camera.combined);

        camera.translate(0, h * 0.2f);
        camera.update();
    }

    /**
     *  set text renderer
     */
    private void initializeText() {

        Skin skin = new Skin();
        skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);
    }

    /**
     *  initialize render variables
     */
    private void initializeRenderVariables() {

        batch           = new SpriteBatch();
        shapeRenderer   = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        Pixmap pixmap   = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        drawingBoard    = new DrawingBoard(homeBase.pixmap);
        drawingBoard.updateCamera(camera);
        drawingBoard.setHomeBasePos(homeBase.pos);

    }

    public void updateFrontAnimations(float delta) {

        for(Iterator<com.neowise.game.draw.MyAnimation> ait = frontAnimations.iterator(); ait.hasNext(); ){

            com.neowise.game.draw.MyAnimation animation = ait.next();

            animation.updateTimer(delta);
            if(animation.currentFrame > animation.length)
                ait.remove();
            else {
                drawingBoard.addSpriteFromAtlasToForeGround(animation.pos.x, animation.pos.y, animation.rotation, animation.currentSprite);
            }
        }

    }

    public void updateMiddleAnimations(float delta) {

        for(Iterator<com.neowise.game.draw.MyAnimation> ait = middleAnimations.iterator(); ait.hasNext(); ){

            com.neowise.game.draw.MyAnimation animation = ait.next();

            animation.updateTimer(delta);
            if(animation.currentFrame > animation.length || !animation.alive)
                ait.remove();
            else {
                drawingBoard.addSpriteFromAtlas(animation.pos.x, animation.pos.y, animation.rotation, animation.currentSprite);
            }
        }

    }

    public void updateBackAnimations(float delta) {

        for(Iterator<com.neowise.game.draw.MyAnimation> ait = backAnimations.iterator(); ait.hasNext(); ){

            MyAnimation animation = ait.next();

            if (!animation.alive){
                animation = null;
                ait.remove();
                continue;
            }

            animation.updateTimer(delta);
            if(animation.currentFrame > animation.length)
                ait.remove();
            else
                drawingBoard.addSpriteFromAtlas(animation.pos.x, animation.pos.y, animation.rotation, animation.currentSprite);
        }

    }

    public void rotateCameraWithPlayer(float delta){

        double cameraAngle = Math.atan2(camera.up.x,camera.up.y) * 180 / Math.PI;
        cameraAngle = 180 - cameraAngle;
        cameraAngle = cameraAngle < 0 ? 360+cameraAngle : cameraAngle;
        boolean isClockWise;
        float rotationSpeed = 5.4f;

        if (cameraAngle < 180)
            isClockWise = (playerShip.rotation > cameraAngle && playerShip.rotation < cameraAngle + 180);
        else
            isClockWise = !(playerShip.rotation > cameraAngle - 180 && playerShip.rotation < cameraAngle);

        float dist = (float) Math.abs(180-Math.abs(playerShip.rotation-cameraAngle));

        if (isClockWise && dist > 1){
            //camera.rotate(0.05f * dist);
            camera.rotateAround(new Vector3(0,0, 0), new Vector3(0, 0, 1), -delta * rotationSpeed * dist);
        }
        else if(dist > 1){
            //camera.rotate(-0.05f * dist);
            camera.rotateAround(new Vector3(0,0, 0), new Vector3(0, 0, 1), delta * rotationSpeed * dist);
        }


        camera.update();
        drawingBoard.updateCamera(camera);
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {

    }
}
