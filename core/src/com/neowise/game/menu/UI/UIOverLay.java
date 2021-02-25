package com.neowise.game.menu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.draw.Text;

import java.util.ArrayList;
import java.util.Collection;

public abstract class UIOverLay implements InputProcessor {

    OrthographicCamera overLayCamera;
    Collection<UIButton> buttons;
    Collection<Sprite> sprites;
    Collection<Text> texts;

    Collection<Sprite> spriteLayer;
    Collection<Text>   textLayer;

    BitmapFont font;

    protected float alpha = 1;

    public UIOverLay(){
        buttons = new ArrayList<>();
        sprites = new ArrayList<>();
        texts   = new ArrayList<>();
    }

    public abstract void init(float w, float h);

    public void draw() {

        for(Sprite s : sprites){
            s.setAlpha(alpha);
            DrawingBoard.addSprite(s, spriteLayer);
        }
        for(Text t : texts){
            t.setAlpha(alpha);
            DrawingBoard.addText(t, textLayer);
        }
    }

    public void setInput(){
        InputMultiplexer mult = new InputMultiplexer();
        mult.addProcessor(this);
        Gdx.input.setInputProcessor(mult);
    }
    
    //
    //InputProcessor
    //

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

        if(alpha < 0.3f) return false;

        if(overLayCamera != null){
            Vector3 vec = new Vector3(screenX,screenY,0);
            overLayCamera.unproject(vec);
            screenX = (int) vec.x;
            screenY = (int) vec.y;
        }

        System.out.println(screenX + " " + screenY);

        for(UIButton b : buttons){
            b.setTouched(screenX, screenY, pointer);
        }

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
}
