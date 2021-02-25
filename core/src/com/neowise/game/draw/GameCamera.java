package com.neowise.game.draw;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class GameCamera {

    /*
    Camera and Viewport that operate the main game-screen.
    Uses a StretchViewport to stretch / fit the screen to the
    VIRTUAL_WIDTH and VIRTUAL_HEIGHT.
     */
    public OrthographicCamera camera;
    private StretchViewport viewport;

    //virtual width & height
    public final int VIRTUAL_WIDTH = 500;
    public final int VIRTUAL_HEIGHT = 800;


    /*
    Constructor.
     */
    public GameCamera () {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0f);
        camera.update();
    }

    public void updateSize (int width, int height) {
        viewport.update(width, height);
    }

}