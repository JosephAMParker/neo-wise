package com.neowise.game.draw;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;


/**
 * Created by tabletop on 9/16/15.
 */
public class BackgroundObject {

    public Sprite sprite;
    public Vector2 pos;
    public float size;
    public float rotation;
    public float alpha;
    public float speed;

    public BackgroundObject(Vector2 pos, float size, float rotation, float alpha, float speed, String atlasKey) {

        this.pos = pos;
        this.size = size;
        this.rotation = rotation;
        this.alpha = alpha;
        this.speed = speed;
        this.sprite = DrawingBoard.atlas.createSprite(atlasKey);

    }


}
