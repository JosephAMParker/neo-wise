package com.neowise.game.menu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.draw.Text;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.menu.ControlBar;
import com.neowise.game.menu.ControlButton;
import com.neowise.game.util.Constants;

public class UIPlayerMenu extends UIOverLay {

    private UIButton openButton, closeButton, weaponLeftButton,
            weaponRightButton, secondaryLeftButton, secondaryRightButton;
    private Sprite weaponSlot, weaponIcon;
    private Text weaponSelectText, weaponText;
    private BasicLevel basicLevel;
    private float slideSpeed, w, h;

    public UIPlayerMenu(BasicLevel basicLevel, float w, float h){
        super();
        this.spriteLayer = DrawingBoard.getMenuSprites();
        this.textLayer   = DrawingBoard.getMenuTexts();
        this.basicLevel = basicLevel;
        this.slideSpeed = 0.1f;
        this.overLayCamera = basicLevel.hudCamera;
        font = new BitmapFont(Gdx.files.internal("font/shoulder.fnt"), Gdx.files.internal("font/shoulder.png"), false);
        this.w = w;
        this.h = h;
        init(w,h);
    }

    private void setWeaponIcon(){
        sprites.remove(weaponIcon);
        texts.remove(weaponText);

        weaponText       = new Text(font, basicLevel.playerShip.currentWeapon.getName(),
                weaponSelectText.x + weaponSelectText.getWidth() + 5, weaponSelectText.y);
        weaponIcon = basicLevel.playerShip.currentWeapon.getSprite();
        weaponIcon.setBounds(weaponSlot.getX(), weaponSlot.getY(),
                weaponSlot.getWidth(), weaponSlot.getHeight());

        sprites.add(weaponIcon);
        texts.add(weaponText);
    }

    @Override
    public void init(float w, float h) {

        buttons.clear();
        sprites.clear();
        texts.clear();

        float innerPadding = 30;
        float outerPadding = 15;

        //open close buttons
        Sprite openSprite  = DrawingBoard.createSprite("openPlayerMenu");
        Sprite closeSprite = DrawingBoard.createSprite("openPlayerMenu");
        float openButtonWidth = h/12;
        openButton = new UIButton(openSprite,
                w-openButtonWidth-5,h/2 - openButtonWidth/2,
                openButtonWidth,openButtonWidth);
        closeButton = new UIButton(closeSprite,
                w+5,h/2 - openButtonWidth/2,
                openButtonWidth,openButtonWidth);
        //open close buttons

        //menu background
        Sprite playMenu  = DrawingBoard.createSprite("playMenu");
        playMenu.setBounds(w,0,w,h);
        //menu background




        //weapon select
        Sprite weaponBox = DrawingBoard.createSprite("menuBox");
        Sprite weaponLeft = DrawingBoard.createSprite("menuArrow");
        Sprite weaponRight = DrawingBoard.createSprite("menuArrow");
        weaponSlot = DrawingBoard.createSprite("weaponSlot");

        float boxHeight = h/5;
        float boxWidth  = w - outerPadding*2;
        float arrowSize = boxWidth/5;
        float boxCenterY;

        weaponBox.setBounds(w+outerPadding, h-boxHeight-outerPadding, boxWidth, boxHeight);
        boxCenterY = weaponBox.getY() + weaponBox.getHeight()/2 - arrowSize/2;
        weaponLeft.setBounds(weaponBox.getX() + innerPadding, boxCenterY, arrowSize, arrowSize);
        weaponRight.flip(true,false);
        weaponRight.setBounds(weaponBox.getX() + weaponBox.getWidth() - arrowSize - innerPadding, boxCenterY, arrowSize, arrowSize);
        weaponSlot.setBounds(w + w/2 - arrowSize/2, boxCenterY, arrowSize, arrowSize);
        weaponLeftButton = new UIButton(weaponLeft.getBoundingRectangle());
        weaponRightButton = new UIButton(weaponRight.getBoundingRectangle());
        weaponSelectText = new Text(font, "Weapon Select: ", w + outerPadding, weaponBox.getY() - 5);
        //weapon select



        //secondary select
        Sprite secondaryBox = DrawingBoard.createSprite("menuBox");
        Sprite secondaryLeft = DrawingBoard.createSprite("menuArrow");
        Sprite secondaryRight = DrawingBoard.createSprite("menuArrow");


        secondaryBox.setBounds(w+outerPadding,weaponSelectText.getYPos()-boxHeight-innerPadding, boxWidth, boxHeight);
        boxCenterY = secondaryBox.getY() + secondaryBox.getHeight()/2 - arrowSize/2;
        secondaryLeft.setBounds(secondaryBox.getX() + innerPadding, boxCenterY, arrowSize, arrowSize);
        secondaryRight.flip(true,false);
        secondaryRight.setBounds(secondaryBox.getX() + secondaryBox.getWidth() - arrowSize - innerPadding, boxCenterY, arrowSize, arrowSize);
        secondaryLeftButton = new UIButton(secondaryLeft.getBoundingRectangle());
        secondaryRightButton = new UIButton(secondaryLeft.getBoundingRectangle());

        Text secondaryText = new Text(font, "Secondary Select", w + outerPadding, secondaryBox.getY() - 5);
        //secondary select



        buttons.add(weaponLeftButton);
        buttons.add(weaponRightButton);
        buttons.add(secondaryLeftButton);
        buttons.add(secondaryRightButton);
        buttons.add(openButton);
        buttons.add(closeButton);

        sprites.add(playMenu);
        sprites.add(openSprite);
        sprites.add(closeSprite);
        sprites.add(weaponBox);
        sprites.add(weaponLeft);
        sprites.add(weaponRight);
        sprites.add(weaponSlot);
        sprites.add(secondaryBox);
        sprites.add(secondaryLeft);
        sprites.add(secondaryRight);

        texts.add(weaponSelectText);
        texts.add(secondaryText);

        setWeaponIcon();

    }

    public void open() {
        basicLevel.setGameState(Constants.GAME_STATES.IN_MENU);
        ControlBar.setInActive();
        setInput();
    }

    public void close() {
        basicLevel.setGameState(Constants.GAME_STATES.EXIT_MENU);
        ControlBar.setActive();
        basicLevel.setInput();
        basicLevel.addInput(this);
    }

    public void slideOpen(float delta) {
        basicLevel.hudCamera.position.lerp(basicLevel.openMenuPos, (float) (1-Math.pow(slideSpeed, delta)));
        overLayCamera = basicLevel.hudCamera;
    }

    public void slideClosed(float delta) {
        basicLevel.hudCamera.position.lerp(basicLevel.closeMenuPos, (float) (1-Math.pow(slideSpeed, delta)));
        overLayCamera = basicLevel.hudCamera;
    }

    public void quickOpen() {
        basicLevel.hudCamera.position.set(basicLevel.openMenuPos);
        overLayCamera = basicLevel.hudCamera;
        basicLevel.inMenuDelta = 0;
        open();
    }

    public void quickClose() {
        basicLevel.hudCamera.position.set(basicLevel.closeMenuPos);
        overLayCamera = basicLevel.hudCamera;
        basicLevel.inMenuDelta = 1;
        close();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        Vector3 vec = new Vector3(screenX,screenY,0);
        overLayCamera.unproject(vec);
        screenX = (int) vec.x;
        screenY = (int) vec.y;

        if(openButton.isTouched(screenX, screenY, pointer)){
            open();
        }

        if(closeButton.isTouched(screenX, screenY, pointer)){
            close();
        }

        if(weaponLeftButton.isTouched(screenX, screenY, pointer)){
            basicLevel.playerShip.prevWeapon();
            setWeaponIcon();
        }

        if(weaponRightButton.isTouched(screenX, screenY, pointer)){
            basicLevel.playerShip.nextWeapon();
            setWeaponIcon();
        }

        return false;
    }
}
