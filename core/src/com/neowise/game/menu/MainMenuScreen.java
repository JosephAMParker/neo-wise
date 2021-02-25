package com.neowise.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.main.GameLevelObject;
import com.neowise.game.main.NeoWiseGame;
import com.neowise.game.menu.UI.UIMainMenu;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

public class MainMenuScreen extends GameLevelObject {

    private UIMainMenu ui;
    private StarMap starMap;
    private boolean showUI, homeBaseReady;

    public MainMenuScreen(final NeoWiseGame game) {

        super(game);
        init();
    }

    private void init() {

        game.createNewStarMap();
        starMap = game.getStarMap();
        resetCamera();
        starMap.initMap();
        starMap.initializeCamera();
        showUI = true;
        ui = new UIMainMenu(hudCamera,w,h);
        ui.init(w,h);
        ui.setInput();

   }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(starMap.camera.position.y - starMap.planetRadius < starMap.earthPos)
            showUI = false;

        if(showUI)
            ui.draw();

        drawingBoard.draw();
        starMap.render(delta);

        if(ui.started()){
            if(!homeBaseReady) {
                homeBaseReady = true;
                initHomeBase(true);
            }
            starMap.start();
            starMap.setInput();
            camera.translate(0, starMap.cameraPanSpeed, 0);
        }

        updateCamera();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        init();
    }
}
