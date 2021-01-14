package com.neowise.game.draw;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by tabletop on 9/17/15.
 */
public class MyAnimation {

    public Sprite currentSprite;
    String atlasKey;
    public float scale, rotation, frameTimer,frameRate;
    public int currentFrame, length;
    public Vector2 pos;
    public boolean repeat,alive;

    public MyAnimation(String atlasKey, int length, Vector2 pos, float rotation, boolean repeat, float frameRate, float scale){

        currentFrame = 0;
        this.atlasKey = atlasKey;
        this.currentSprite = DrawingBoard.atlas.createSprite(atlasKey , currentFrame);
        this.length = length - 1;
        this.repeat = repeat;
        this.frameRate = frameRate;
        this.scale = scale;
        frameTimer = 0;
        this.pos = pos.cpy().sub(currentSprite.getWidth()/2,currentSprite.getWidth()/2);
        this.rotation = rotation;
        alive = true;
    }

    public void updateTimer(float delta) {
        frameTimer += delta;

        if (frameTimer >= frameRate){
            frameTimer -= frameRate;
            currentFrame += 1;
            if (currentFrame <= length)
                this.currentSprite = DrawingBoard.atlas.createSprite(atlasKey ,currentFrame);
            else if (repeat){
                currentFrame = 0;
                this.currentSprite = DrawingBoard.atlas.createSprite(atlasKey ,currentFrame);
            }
        }
    }

    public void updatePos(Vector2 pos){
        this.pos = pos.cpy().sub(currentSprite.getWidth()/2,currentSprite.getWidth()/2);
    }


}
