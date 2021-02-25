package com.neowise.game.menu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.draw.Text;

public class UIPlayerLoss extends UIOverLay {

    private UIButton backMain;
    private boolean back = false;

    public UIPlayerLoss(float w, float h){
        super();
        this.spriteLayer = DrawingBoard.getMenuSprites();
        this.textLayer   = DrawingBoard.getMenuTexts();
        alpha = 0;
        font = new BitmapFont(Gdx.files.internal("font/shoulder.fnt"), Gdx.files.internal("font/shoulder.png"), false);
        init(w,h);
    }

    public void init(float w, float h) {

        buttons.clear();
        sprites.clear();
        texts.clear();

        Text gameOverText = new Text(new BitmapFont(Gdx.files.internal("font/shoulder.fnt"), Gdx.files.internal("font/shoulder.png"), false), "GAME OVER", w/2, h*0.7f);
        gameOverText.setScale(1.8f);
        gameOverText.center();

        Sprite gameOverBackDrop = DrawingBoard.createSprite("backdrop");

        gameOverBackDrop.setBounds(0,0, gameOverText.getWidth()*2f, gameOverText.getHeight()*2f);
        gameOverBackDrop.setCenter(gameOverText.x + gameOverText.getWidth()/2,gameOverText.y - gameOverText.getHeight()/2);

        Text backToMainText = new Text(font, "BACK TO MAIN MENU", w/2, gameOverText.getYPos()-5);
        backToMainText.setScale(0.8f);
        backToMainText.center();

        Sprite backMainBackDrop = DrawingBoard.createSprite("backdrop");
        backMainBackDrop.setBounds(0,0,backToMainText.getWidth()*2f, backToMainText.getHeight()*2f);
        backMainBackDrop.setCenter(backToMainText.x + backToMainText.getWidth()/2, backToMainText.y - backToMainText.getHeight()/2);

        backMain = new UIButton(backToMainText.x, backToMainText.getYPos(), backToMainText.getWidth(), backToMainText.getHeight());
        texts.add(gameOverText);
        texts.add(backToMainText);
        sprites.add(gameOverBackDrop);
        sprites.add(backMainBackDrop);

        buttons.add(backMain);

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        Vector3 vec = new Vector3(screenX,screenY,0);
        overLayCamera.unproject(vec);
        screenX = (int) vec.x;
        screenY = (int) vec.y;

        if(backMain.isTouched(screenX, screenY, pointer)){
            back = true;
        }
        return false;
    }

    public boolean back(){
        return back;
    }

    public void update(float delta) {
        if(alpha < 1)
            alpha += delta;
        else
            alpha = 1;
    }
}
