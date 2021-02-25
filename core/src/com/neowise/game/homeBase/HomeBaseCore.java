package com.neowise.game.homeBase;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.GameObject;

public class HomeBaseCore extends GameObject {

    public int radius, radius2;
    public float health;
    public MyAnimation animation;

    public HomeBaseCore(Vector2 pos, float health){
        this.pos = pos;
        this.health = health;
        this.radius = 10;//animation.currentSprite.getWidth() / 2;
        this.radius2 = radius * radius;
    }

    public void causeDamage(float damage) {
        health -= damage;
    }
}
