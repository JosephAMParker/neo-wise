package com.neowise.game.menu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.draw.Text;

public class UIMainMenu extends UIOverLay{

    private UIButton newGame;
    private boolean gameStarted = false;

    public UIMainMenu(OrthographicCamera camera, float w, float h){
        super();
        this.overLayCamera = camera;
        this.spriteLayer = DrawingBoard.getMenuSprites();
        this.textLayer   = DrawingBoard.getMenuTexts();
        font = new BitmapFont(Gdx.files.internal("font/shoulder.fnt"), Gdx.files.internal("font/shoulder.png"), false);
        init(w,h);
    }

    @Override
    public void init(float w, float h){

        buttons.clear();
        sprites.clear();
        texts.clear();

        Sprite logo = DrawingBoard.createSprite("logo");

        float logoScale = w/logo.getWidth();
        logo.setRotation(0);
        logo.rotate(0);
        logo.setBounds(0, h, w, logo.getHeight() * logoScale);
        logo.setCenterY(h-logo.getHeight()/2);


        Text newGameText = new Text(font, "NEW GAME", 30, h-logo.getHeight());
        newGameText.setLineHeight(h/10);
        newGame  = new UIButton(newGameText.x, newGameText.getYPos(), newGameText.getWidth(), newGameText.getHeight());

        texts.add(newGameText);
        buttons.add(newGame);
        sprites.add(logo);

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        Vector3 vec = new Vector3(screenX,screenY,0);
        overLayCamera.unproject(vec);
        screenX = (int) vec.x;
        screenY = (int) vec.y;

        if(newGame.isTouched(screenX, screenY, pointer)){
            startGame();
        }
        return false;
    }

    private void startGame() {
        gameStarted = true;
    }

    public boolean started() {
        return gameStarted;
    }
}
