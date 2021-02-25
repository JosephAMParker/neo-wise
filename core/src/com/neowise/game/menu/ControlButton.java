package com.neowise.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.util.Settings;

public class ControlButton extends NeoButton{

    public enum ButtonType {LEFT, SHOOT, RIGHT}
    private float touchedTimer, touchedTimerReset;
    private static boolean active = true;

    public ControlButton(Sprite sprite, int x, int y, int width, int height) {
        super(sprite, x, y, width, height);
        touchedTimerReset = 2 + 20;
        touchedTimer = touchedTimerReset;
    }

    public boolean touched() {

        if(!active)
            return false;

        for (int i = 0;i<5;i++) {
            if (Gdx.input.isTouched(i) &&
                    bounds.contains(Gdx.input.getX(i), Gdx.graphics.getHeight() - Gdx.input.getY(i)))
                return true;
        } return false;
    }

    public void updateSprite() {
        if(Settings.showControls) {
            float a = Math.min(255, Math.max(0, (touchedTimer - 20) / (touchedTimerReset - 20)));
            DrawingBoard.addSpriteFromAtlas(bounds.x, bounds.y, bounds.width, bounds.height, a, 0, sprite, DrawingBoard.getMenuSprites());
        }
    }

    public void update(float delta) {

        updateSprite();

        if(touched() && touchedTimer > 0) {
            touchedTimer -= delta;
        }
        else if (touchedTimer < touchedTimerReset)
            touchedTimer += delta / 10;
    }

    public void setActive(){
        active = true;
    }

    public void setInActive(){
        active = false;
    }
}
