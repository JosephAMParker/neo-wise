package com.neowise.game.squad;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.Ship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Squad_Flock extends Squad {

    int capacity;
    float flockDegree;
    float flockSpeed, riseSpeed;
    float flockHeight, originalHeight;

    public boolean dive, rising, diving, resetting;
    float diveTimer, diveTimerReset, targetAltitude;
    int diveTimerResetRandom;

    public float heightAmplitude;
    Collection<com.neowise.game.gameObject.ship.Ship> ships;
    Collection<Flock_SquadPlace> sqPlaces;
    Random random;

    public class Flock_SquadPlace extends SquadPlace {

        public float heightOffset;  //between 0-360. Get this places current height with heightAmplitude*sin(flockHeight + heightOffset)
        public float horizontalOffset;

        public Flock_SquadPlace(Vector2 pos){
            super(pos);
            heightOffset = random.nextInt(359);
            horizontalOffset = random.nextInt(45) + random.nextInt(45);
        }
    }

    public Squad_Flock(Vector2 pos,int capacity) {
        super(pos);
        this.capacity = capacity;
        random = new Random();
        loose = true;
        dive = false;
        ships = new ArrayList<com.neowise.game.gameObject.ship.Ship>(capacity);
        sqPlaces = new ArrayList<Flock_SquadPlace>();
        flockSpeed = 80;
        riseSpeed = 0;
        flockDegree = 0;
        heightAmplitude = 40;
        diveTimerReset  = 5;
        diveTimerResetRandom = 1;
        flockHeight = 0;
        originalHeight = flockHeight;
        targetAltitude = 0;
        rising = false;
        diving = false;
        resetting = false;
        resetDiveTimer();
    }

    @Override
    public void updateTimers(float delta){
        diveTimer -= delta;
    }

    @Override
    public void performAction(float delta) {

        if (dive) dive = false;
        if (diveTimer <= 0) {
            dive = true;
            rising = true;
            diveTimer = 500f;
        }

        if(rising) {
            riseSpeed += (riseSpeed > 50 ? 0 : 2f);
            flockHeight += riseSpeed * delta;
            //flockHeight += 30 * delta;
            if (flockHeight > 200) {
                diving = true;
                rising = false;
            }
        }

        if(diving){
            riseSpeed -= (riseSpeed < -60 ? 0 : 3);
            //flockHeight -= delta * 75;
            flockHeight += riseSpeed * delta;
            if(flockHeight < -50) {
                resetting = true;
                diving = false;
            }
        }

        if(resetting){
            riseSpeed += (riseSpeed > 30 ? 0 : 2);
            //flockHeight += 10*delta;
            flockHeight += riseSpeed * delta;
            if(flockHeight > originalHeight){
                resetDiveTimer();
                resetting = false;
                flockHeight = originalHeight;
            }
        }

    }

    private void resetDiveTimer() {
        diveTimer = diveTimerReset + random.nextInt(diveTimerResetRandom);
    }

    @Override
    public boolean empty(){
        return ships.isEmpty();
    }

    @Override
    public boolean full() {
        return ships.size() >= capacity;
    }

    @Override
    public void updatePos(float delta){

        if(!diving && !rising)  //only adjust rotating height if not in dive motion
            flockDegree += delta;
        flockDegree = flockDegree > 360 ? flockDegree - 360 : flockDegree;
        flockDegree = flockDegree < 0 ? 360 - flockDegree : flockDegree;

        Vector2 toPlanet = pos.cpy().scl(-1).nor(); //vector pointing to planet center.

        if(diving)
            toPlanet.rotate90(1).scl(flockSpeed*delta/12);
        else
            toPlanet.rotate90(1).scl(flockSpeed*delta);

        pos.add(toPlanet);
        oa.angle = pos.angle();

    }

    @Override
    public void openPlace(com.neowise.game.gameObject.ship.Ship ship) {

        ship.sqPlace.filled = false;
        ships.remove(ship);

    }

    @Override
    public void removeShipFromSquad(com.neowise.game.gameObject.ship.Ship ship) {

        ship.sqPlace.filled = false;
        ship.inSquad = false;

        ships.remove(ship);

    }

    @Override
    public void removeAllShips() {

        ships = new ArrayList<com.neowise.game.gameObject.ship.Ship>(capacity);

    }

    @Override
    public boolean fillNextPlace(Ship ship){

        if (ships.size() >= capacity)
            return false;

        ship.inSquad = true;
        ship.sqPlace = new Flock_SquadPlace(pos.cpy());
        ship.squad   = this;
        ships.add(ship);

        return true;

    }

    public float getTargetAltitude(Flock_SquadPlace sqPlace){
        return (float) (heightAmplitude * Math.sin(sqPlace.heightOffset + flockDegree)) + flockHeight;
    }

}
