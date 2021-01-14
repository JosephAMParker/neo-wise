package com.neowise.game.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.neowise.game.LevelInfo.LevelInfo;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.menu.StarMap;
import com.neowise.game.gameObject.player.PlayerShip;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.menu.MainMenuScreen;

public class NeoWiseGame extends Game {

    protected SpriteBatch batch;
    protected BitmapFont font;
    private HomeBase homeBase;
    private StarMap starMap;
    private PlayerShip playerShip;
    private AssetManager manager;

    public float w;
    public float h;
    //GameOptions options;

    @Override
    public void create() {

        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont();
        manager = new AssetManager();
        DrawingBoard.initAtlas();
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
        playerShip = new PlayerShip(10,250);
    }

    public void createNewHomeBase(){
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
}
