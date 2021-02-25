package com.neowise.game.menu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.menu.NeoButton;

public class UIButton extends NeoButton {

    public UIButton(Sprite sprite, float x, float y, float width, float height) {
        super(sprite, x, y, width, height);
    }

    public UIButton(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public UIButton(Rectangle bounds) {
        super(bounds);
    }

    public void draw(){
        DrawingBoard.addSpriteFromAtlas(bounds.x, bounds.y, bounds.width, bounds.height, 1, 0, sprite, DrawingBoard.getMenuSprites());
    }

    public void setTouched(int screenX, int screenY, int pointer) {

        if(bounds.contains(screenX, screenY)){
            touched = pointer;
            return;
        }

        touched = -1;
    }

    public boolean isTouched(int x, int y, int pointer) {

        for (int i = 0;i<5;i++) {
            if (touched == pointer && bounds.contains(x, y)) {
                return true;
            }
        }

        return false;
    }
}