package com.neowise.game.gameObject.particle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.DrawingBoard;

import java.util.Random;

/**
 * Created by tabletop on 6/21/15.
 */
public class Smoke {

    Vector2 pos;
    Random random;
    //float color;
    String atlasKey;
    Sprite sprite;
    int gridx,gridy;
    boolean outOfGrid = false;
    static int[][] grid;
    static int gridWidth, gridHeight, gridDiv;

    public Smoke (Vector2 pos) {

        this.pos = pos;
        random = new Random();
        float color = random.nextFloat() % 0.3f + 0.2f;
        this.sprite = DrawingBoard.atlas.createSprite("smoke2");


        gridx = (int) Math.floor(pos.x / gridWidth);
        gridy = (int) Math.floor(pos.y / gridHeight);

        grid[gridx][gridy]++;

    }

    public void jitter(){
        long pop = grid[gridx][gridy];
        random.setSeed(pop + 100);
        float ran = (random.nextFloat() - 0.75f) * 10/pop;
        pos.add(0, 0);
        float color = random.nextFloat() % 0.3f + 0.2f;


    }

    public static void initGrid(int w, int h){
        gridDiv = 100;
        gridWidth = w/gridDiv;
        gridHeight = h/gridDiv;
        grid = new int[gridDiv][gridDiv];
    }

    public void updateGrid(){

        grid[gridx][gridy]--;
        gridx = (int) Math.floor(pos.x / gridWidth);
        gridy = (int) Math.floor(pos.y / gridHeight);
        try {
            grid[gridx][gridy]++;
        }
        catch (Exception e){
            outOfGrid = true;
        }


    }
}
