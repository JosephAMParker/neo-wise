package com.neowise.game.draw;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Text extends Drawable {

    public float x,y;
    public String text;
    public BitmapFont font;
    private GlyphLayout gl;

    public Text(BitmapFont font, String text, float x, float y){
        this.font = font;
        this.text = text;
        this.x = x;
        this.y = y;

        gl = new GlyphLayout(font, text);
    }

    public void setAlpha(float a){
        Color c = font.getColor();
        c.a = a;
        font.setColor(c);
    }

    public void setLineHeight(float height) {
        font.getData().setScale(height * font.getScaleY() / font.getLineHeight());
    }

    public float getWidth() {
        return gl.width;
    }

    public float getHeight() {
        return gl.height;
    }

    public void setScale(float scale) {
        font.getData().setScale(scale);
        gl = new GlyphLayout(font, text);
    }

    public void center() {
        x -= gl.width/2;
        y -= gl.height/2;
    }

    public float getYPos() {
        return y - getHeight();
    }
}
