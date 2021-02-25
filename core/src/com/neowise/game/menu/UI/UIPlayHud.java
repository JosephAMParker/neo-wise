package com.neowise.game.menu.UI;

import com.neowise.game.draw.DrawingBoard;

public class UIPlayHud extends UIOverLay {

    public UIPlayHud(float w, float h) {
        init(w, h);

        this.spriteLayer = DrawingBoard.getHudSprites();
        this.textLayer   = DrawingBoard.getHudTexts();
    }

    @Override
    public void init(float w, float h) {

    }
}
