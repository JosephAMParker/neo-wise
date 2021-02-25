package com.neowise.game.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.neowise.game.draw.BackgroundObject;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.player.PlayerShip;
import com.neowise.game.homeBase.HomeBase;

import java.util.Collection;
import java.util.Iterator;


public abstract class GameLevelObject extends ScreenObject implements InputProcessor {

    public final NeoWiseGame game;

    // Camera, Stage and graphics related fields
    public OrthographicCamera camera;
    public OrthographicCamera hudCamera;
    public ShapeRenderer shapeRenderer;
    public ShapeRenderer hudRenderer;
    public DrawingBoard drawingBoard;

    //input
    InputMultiplexer multi;

    // animations
    public Collection<MyAnimation> backAnimations;
    public Collection<MyAnimation> middleAnimations;
    public Collection<MyAnimation> frontAnimations;

    // back+foreground objects
    public Collection<BackgroundObject> backgroundObjects;
    public Collection<BackgroundObject> foregroundObjects;

    // useful global variables
    public float w = Gdx.graphics.getWidth();
    public float h = Gdx.graphics.getHeight();
    public Color bgColor;
    public boolean slow;
    public float totalTime = 0;
    public Vector3 homeBaseCameraPos;

    // basic game objects
    public HomeBase homeBase;
    public PlayerShip playerShip;

    public GameLevelObject(final NeoWiseGame game) {

        this.game = game;
        this.homeBase = game.getHomeBase();
        this.playerShip = game.getPlayerShip();

        initializeGlobalVariables();
        initializeCamera();
        initializeText();
        initializeRenderVariables();

        setInput();
    }

    public void initHomeBase(boolean restartHB){
        game.createNewHomeBase(restartHB);
        homeBase = game.getHomeBase();
        drawingBoard.setPixMap(homeBase.getPixmap());
        drawingBoard.setHomeBasePos(new Vector2(0,0));
    }

    public void setInput(){
        multi = new InputMultiplexer();
        multi.addProcessor(this);
        Gdx.input.setInputProcessor(multi);
    }

    public void addInput(InputProcessor processor){
        multi.addProcessor(processor);
        Gdx.input.setInputProcessor(multi);
    }

    /**
     *  set starting values for global variables
     */
    private void initializeGlobalVariables(){
        bgColor = new Color(0.01f,0.01f,0.02f,1);
        slow = false;
    }

    public void  initializeCamera() {
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, w, h);
        hudCamera.update();

        hudRenderer   = new ShapeRenderer();
        hudRenderer.setProjectionMatrix(camera.combined);
    }

    public void updateCamera(){
        camera.update();
        hudCamera.update();
        drawingBoard.updateCamera(camera, hudCamera);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    public void resetCamera(){
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, w, h);
        hudCamera.update();

        drawingBoard.updateCamera(camera, hudCamera);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    protected void toggleFullScreen(){

        Boolean fullScreen = Gdx.graphics.isFullscreen();
        Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
        if (fullScreen == true)
            Gdx.graphics.setWindowedMode(500,800);
        else
            Gdx.graphics.setFullscreenMode(currentMode);

        resetCamera();
    }

    /**
     *  set text renderer
     */
    private void initializeText() {

        Skin skin = new Skin();
        //skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        //textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        //textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        //textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        //textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);
    }

    /**
     *  initialize render variables
     */
    private void initializeRenderVariables( ) {

        shapeRenderer   = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        drawingBoard    = new DrawingBoard();
        drawingBoard.camera = camera;
        drawingBoard.hudCamera = hudCamera;
        drawingBoard.updateCamera(camera, hudCamera);
    }

    public void updateAnimations(float delta, Collection<MyAnimation> animations, Collection<Sprite> spritesCollection){
        for(Iterator<MyAnimation> ait = animations.iterator(); ait.hasNext(); ){

            MyAnimation animation = ait.next();

            animation.updateTimer(delta);
            if(animation.currentFrame > animation.length)
                ait.remove();
            else {
                DrawingBoard.addSpriteFromAtlas(animation.pos.x, animation.pos.y, animation.rotation, animation.scale, animation.currentSprite, spritesCollection);
            }
        }
    }

    public void updateFrontAnimations(float delta) {
        updateAnimations(delta, frontAnimations, DrawingBoard.getMenuSprites());
    }

    public void updateMiddleAnimations(float delta) {
        updateAnimations(delta, middleAnimations, DrawingBoard.getSprites());
    }

    public void updateBackAnimations(float delta) {
        updateAnimations(delta, backAnimations, DrawingBoard.getSprites());
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
            camera.rotateAround(Vector3.Zero, Vector3.Z, -delta * rotationSpeed * dist);
        }
        else if(dist > 1){
            //camera.rotate(-0.05f * dist);
            camera.rotateAround(Vector3.Zero, Vector3.Z,delta * rotationSpeed * dist);
        }
    }

    @Override
    public void resize(int width, int height) {

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
    public boolean scrolled(float amountX, float amountY) {
        return false;
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
