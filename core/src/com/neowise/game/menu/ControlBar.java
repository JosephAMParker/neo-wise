package com.neowise.game.menu;

import com.neowise.game.draw.DrawingBoard;

public class ControlBar {

    public static ControlButton left;
    public static ControlButton shoot;
    public static ControlButton right;

    private static boolean active = true;

    public static void init(float w, float h) {

        left  = new ControlButton(DrawingBoard.createSprite("leftButton"),
                0,0, (int) w/3, (int) (h/4));
        shoot = new ControlButton(DrawingBoard.createSprite("shootButton"),
                (int) w/3, 0,(int) w/3, (int) (h/4));
        right = new ControlButton(DrawingBoard.createSprite("rightButton"),
                (int) w*2/3, 0, (int) w/3, (int) (h/4));

        setActive();
    }

    public static void update(float delta) {
        if(active) {
            left.update(delta);
            shoot.update(delta);
            right.update(delta);
        }
    }

    public static void setActive(){
        active = true;
        left.setActive();
        shoot.setActive();
        right.setActive();
    }

    public static void setInActive(){
        active = false;
        left.setInActive();
        shoot.setInActive();
        right.setInActive();
    }
}
