package com.neowise.game.util;

/**
 * Created by tabletop on 22/06/16.
 */
public class Constants {

    public static final float TIME_STEP = 1/70f;
    public static final int VELOCITY_ITERATIONS = 2;
    public static final int POSITION_ITERATIONS = 6;
    public static final float PPM = 20;

    public enum GameObjectTypes {ship_box2d, bullet_box2d, weapon_upgrader}
    public enum SquadTypes {flock}

}
