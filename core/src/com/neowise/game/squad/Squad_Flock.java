package com.neowise.game.squad;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;

import java.util.Collection;

public class Squad_Flock extends Squad {

    Collection<Ship> ships;

    public Squad_Flock(Vector2 pos) {
        super(pos);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public boolean canJoinSquad(Ship ship) {
        return false;
    }

    @Override
    public void removeShipFromSquad(Ship ship) {

    }

    @Override
    public void joinSquad(Ship ship) {

    }

    @Override
    public boolean toRemove() {
        return ships.isEmpty();
    }
}
