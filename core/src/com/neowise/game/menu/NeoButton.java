package com.neowise.game.menu;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.neowise.game.draw.Text;

public abstract class NeoButton {

    public Rectangle bounds;
    protected Sprite sprite;
    protected Text text;
    public int touched = -1;

    public NeoButton(Sprite sprite, float x, float y, float width, float height) {
        this.bounds = new Rectangle(x,y,width,height);
        this.sprite = sprite;

        sprite.setBounds(x,y,width,height);
    }

    public NeoButton(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x,y,width,height);
    }

    public NeoButton(Rectangle bounds) {
        this.bounds = bounds;
    }

    public Sprite getSprite() {
        return sprite;
    }
    public Text getText() {
        return text;
    }
}
