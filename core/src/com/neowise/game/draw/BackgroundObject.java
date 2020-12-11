package com.neowise.game.draw;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;


/**
 * Created by tabletop on 9/16/15.
 */
public class BackgroundObject {

    public Vector2 pos;
    String atlasKey;
    public Sprite sprite;
    public float speed;
    public float speedMul = 1, scaleMul = 1;

    public BackgroundObject(Vector2 pos, float speed, float speedMul, float scaleMul, String atlasKey) {

        this.pos = pos;
        this.atlasKey = atlasKey;
        this.speedMul = speedMul;
        this.scaleMul = scaleMul;
        this.speed = speed*speedMul;

        this.sprite = com.neowise.game.draw.DrawingBoard.atlas.createSprite(atlasKey);

    }

    public BackgroundObject(Vector2 pos, float speed, float speedMul, String atlasKey) {

        this.pos = pos;
        this.atlasKey = atlasKey;
        this.speedMul = speedMul;;
        this.speed = speed*speedMul;

        this.sprite = com.neowise.game.draw.DrawingBoard.atlas.createSprite(atlasKey);

    }

    public BackgroundObject(Vector2 pos, float speed, String atlasKey) {

        this.pos = pos;
        this.atlasKey = atlasKey;
        this.speed = speed;
        this.sprite = DrawingBoard.atlas.createSprite(atlasKey);

    }


}
