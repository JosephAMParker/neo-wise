package com.neowise.game.util;

/**
 * Created by tabletop on 22/06/16.
 */
public class Constants {

    public enum GAME_STATES {PAUSED, LOADING, IN_GAME, PLAYER_WIN, IN_MENU, EXIT_MENU, PLAYER_LOSS}
    public enum GAME_MODE {CITY_DEFENDER, SPACE_INVADERS}
    public enum SHIP_TYPES {UPGRADE_SHIP, BASIC_SPACE_INVADER, LASER_CANNON_SHIP, ORBITER, LARGE_SPACE_INVADER}
    public enum WEAPON_TYPES {FLAK, CHAIN_GUN, LASER_BEAM, BOMB}

    public final static int REMOVE_DISTANCE2 = 1000000; // distance to remove projectiles and chunks and others (squared)
    public final static int w = 500, h = 800;           // standard window size
    public final static int startingHomeSize = 250;

}
