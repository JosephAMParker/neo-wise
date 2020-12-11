package com.neowise.game.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.neowise.game.menu.StarMap;
import com.neowise.game.gameObject.player.PlayerShip;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.menu.MainMenuScreen;

public class ScorchedEarthGame extends Game {

    protected SpriteBatch batch;
    protected BitmapFont font;
    private com.neowise.game.homeBase.HomeBase homeBase;
    private StarMap starMap;
    private com.neowise.game.gameObject.player.PlayerShip playerShip;
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
        playerShip = new com.neowise.game.gameObject.player.PlayerShip(10,250);
    }

    public void createNewHomeBase(){
        homeBase = new com.neowise.game.homeBase.HomeBase(0,0,100);
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
