package com.neowise.game.gameObject.ship.enemy;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.squad.Squad;

public abstract class Enemy extends Ship {

    public Enemy(Vector2 pos){
        super(pos);
    }

}
