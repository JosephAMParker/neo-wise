package com.neowise.game.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.neowise.game.LevelInfo.LevelInfo;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.menu.StarMap;
import com.neowise.game.gameObject.player.PlayerShip;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.menu.MainMenuScreen;
import com.neowise.game.util.Constants;

public class NeoWiseGame extends Game {

    protected SpriteBatch batch;
    protected BitmapFont font;
    private HomeBase homeBase;
    private StarMap starMap;
    private PlayerShip playerShip;
    private AssetManager manager;
    private Constants.GAME_STATES gameState;
    //GameOptions options;

    @Override
    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        manager = new AssetManager();
        DrawingBoard.initAtlas();

        createNewPlayer();
        createNewStarMap();

        this.setScreen(new MainMenuScreen(this));

    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    public void createNewStarMap(){
        starMap = new StarMap(this);
    }

    public void createNewPlayer(){
        playerShip = new PlayerShip();
    }

    public void createNewHomeBase(boolean restartHB){
        if(homeBase == null || restartHB)
            homeBase = new HomeBase(200);
    }

    public PlayerShip getPlayerShip() {
        return playerShip;
    }

    public StarMap getStarMap() {
        return starMap;
    }

    public HomeBase getHomeBase(){
        return homeBase;
    }

    public LevelInfo getCurrentLevelInfo() {
        return starMap.getCurrentLevelInfo();
    }

    public void setGameState(Constants.GAME_STATES gameState) {
        this.gameState = gameState;
    }

    public Constants.GAME_STATES getGameState() {
        return gameState;
    }
}
