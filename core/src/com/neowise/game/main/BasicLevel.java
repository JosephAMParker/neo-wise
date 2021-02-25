package com.neowise.game.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.neowise.game.LevelInfo.LevelInfo;
import com.neowise.game.LevelInfo.SpaceInvadersLevelInfo;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.draw.ScreenShake;
import com.neowise.game.gameObject.CityBombGroup.CityBombGroup;
import com.neowise.game.gameObject.defender.LaserTurret;
import com.neowise.game.gameObject.player.Weapon.ChainGun;
import com.neowise.game.gameObject.player.Weapon.CityDefender;
import com.neowise.game.gameObject.player.Weapon.LaserBeamGun;
import com.neowise.game.gameObject.player.Weapon.ShotGun;
import com.neowise.game.gameObject.ship.LaserCannonShip;
import com.neowise.game.gameObject.ship.Orbiter;
import com.neowise.game.gameObject.ship.Ship_TestLargeShip;
import com.neowise.game.gameObject.ship.Ship_TestShip;
import com.neowise.game.gameObject.pickup.PowerUp;
import com.neowise.game.gameObject.ship.WeaponUpgradeShip;
import com.neowise.game.gameObject.ship.enemy.Dart;
import com.neowise.game.gameObject.weaponProjectile.Bullet;
import com.neowise.game.gameObject.weaponProjectile.BulletHostile;
import com.neowise.game.gameObject.weaponProjectile.HealthBomb;
import com.neowise.game.gameObject.weaponProjectile.LavaBomb;
import com.neowise.game.menu.ControlBar;
import com.neowise.game.menu.MainMenuScreen;
import com.neowise.game.menu.StarMap;
import com.neowise.game.gameObject.rocket.Target;
import com.neowise.game.draw.BackgroundObject;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.weaponProjectile.Bomb;
import com.neowise.game.gameObject.weaponProjectile.Lava;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.menu.UI.UIPlayerLoss;
import com.neowise.game.menu.UI.UIPlayerMenu;
import com.neowise.game.squad.Squad;
import com.neowise.game.squad.Squad_Formation1;
import com.neowise.game.util.Constants;
import com.neowise.game.gameObject.explosion.Explosion;
import com.neowise.game.util.RandomUtil;
import com.neowise.game.util.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BasicLevel extends GameLevelObject implements InputProcessor  {

    private boolean debugMode   = false;
    private boolean sandboxMode = false;
    private boolean showText = true;

    private int integrityStage = 1;
    private float nextStar = 0;
    public float inMenuDelta = 1;

    private Constants.GAME_STATES gameState;
    private enum leftClickModes {SPAWN_BASIC_UNIT, SPAWN_SQUAD, SPAWN_LARGE_UNIT, ADD_TURRET, DROP_BOMB, REMOVE_CIRCLE, SPAWN_DART_UNIT, SPAWN_FLOCK, SHOOT_BULLET, DROP_LAVA_BALL, SPAWN_LASER_UNIT, DROP_HEALTH_BALL, SPAWN_UPGRADER, DROP_CITY_BOMB, DROP_LAVA};
    private leftClickModes leftClickMode = leftClickModes.SPAWN_LASER_UNIT;
    public Collection<WeaponProjectile> friendlyProjectiles;
    public Collection<Explosion> friendlyExplosions;
    public Collection<WeaponProjectile> tempHostileProjectiles;
    public Collection<WeaponProjectile> hostileProjectiles;
    public Collection<Defender> friendlyTurrets;
    public Collection<CityBombGroup> bombGroups;
    private Collection<Target> targets;
    public Collection<PowerUp> powerUps;
    public Collection<Squad> enemySquads;
    public Collection<Ship> ships;
    public Collection<Ship> deadShips;
    private final LevelInfo levelInfo;
    private final StarMap starMap;
    private UIPlayerLoss uiPlayerLoss;
    private UIPlayerMenu uiPlayerMenu;
    private float speedMul;
    private float playerSpeedMul;

    public Vector3 homeBaseProject, openMenuPos, closeMenuPos;
    Sprite spaceBack, spaceBack2;

    public BasicLevel(final NeoWiseGame game) {

        super(game);

        setInput();

        frontAnimations   = new ArrayList<>();
        ships             = new ArrayList<>();
        deadShips         = new ArrayList<>();
        powerUps          = new ArrayList<>();
        middleAnimations  = new ArrayList<>();
        backAnimations    = new ArrayList<>();
        enemySquads       = new ArrayList<>();
        bombGroups        = new ArrayList<>();
        targets           = new ArrayList<>();
        backgroundObjects = new ArrayList<>();
        foregroundObjects = new ArrayList<>();
        friendlyTurrets   = homeBase.getDefences();
        homeBase.resetDefences();

        friendlyProjectiles = new ArrayList<>();
        friendlyExplosions  = new ArrayList<>();
        tempHostileProjectiles = new ArrayList<>();
        hostileProjectiles  = new ArrayList<>();

        levelInfo = game.getCurrentLevelInfo();
        starMap   = game.getStarMap();
        gameState = Constants.GAME_STATES.LOADING;

        ScreenShake.clear();
        homeBase.goToStart(levelInfo);
        playerShip.goToStart(levelInfo);
        playerShip.orbitalRange = homeBase.playerOrbitRange;
        speedMul = 1;
        playerSpeedMul = 1;
        openMenuPos = new Vector3(w/2 + w,h/2,0);
        closeMenuPos = new Vector3(hudCamera.position.x, hudCamera.position.y, 0);

        uiPlayerLoss = new UIPlayerLoss(w,h);
        uiPlayerMenu = new UIPlayerMenu(this, w,h);
        addInput(uiPlayerMenu);
        ControlBar.init(w, h);
        initHomeBase(false);
        initBackGround();

    }

    private void initBackGround() {

        spaceBack = DrawingBoard.createSprite("spaceBack2");
        spaceBack2 = DrawingBoard.createSprite("spaceBack2");

        float spaceRatio = h/spaceBack.getWidth();
        spaceBack.setBounds(-h/2,-h,h,spaceBack.getHeight() * spaceRatio);
        spaceBack2.setBounds(-h/2,spaceBack.getY()-spaceBack.getHeight(),h,spaceBack2.getHeight() * spaceRatio);

        spaceBack2.flip(true,false);
        spaceBack.setAlpha(0.25f);
        spaceBack2.setAlpha(0.25f);

        addInitialStars();
    }

    private void updateTimers(float delta){
        totalTime += delta;
    }

    private void updateBackGroundColours(){
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void setGameState(Constants.GAME_STATES gameState){
        this.gameState = gameState;
    }

    @Override
    public void render(float delta) {

        float gameDelta = delta;

        gameDelta *= speedMul;
        delta *= playerSpeedMul;

        updateTimers(delta);
        updateBackGroundColours();

        drawingBoard.drawBackGround();

        switch (gameState) {
            case PAUSED      :   {renderPaused(delta);              break;  }
            case LOADING     :   {renderLoading(delta);             break;  }
            case IN_GAME     :   {renderMainLoop(gameDelta, delta); break;  }
            case PLAYER_LOSS :   {renderPlayerLoss(delta);          break;  }
            case PLAYER_WIN  :   {renderPlayerWin(delta);           break;  }
            case IN_MENU     :   {renderInMenu(delta);              break;  }
            case EXIT_MENU   :   {renderExitMenu(delta);            break;  }
        }

        updateHud(delta);
        drawingBoard.draw();

    }

    private void renderExitMenu(float delta){

        if(inMenuDelta < 1)
            inMenuDelta += delta;

        inMenuDelta = Math.min(1, inMenuDelta);

        uiPlayerMenu.slideClosed(delta);
        updateCamera();

        if(hudCamera.position.x - 0.5 <= closeMenuPos.x) {
            setGameState(Constants.GAME_STATES.IN_GAME);
            inMenuDelta = 1;
            hudCamera.position.x = closeMenuPos.x;
        }

        renderMainLoop(delta * inMenuDelta, delta * inMenuDelta);
    }

    private void renderInMenu(float delta) {

        if(inMenuDelta > 0)
            inMenuDelta -= delta;

        inMenuDelta = Math.max(0, inMenuDelta);

        uiPlayerMenu.slideOpen(delta);
        updateCamera();

        ScreenShake.clear();

        if(hudCamera.position.x + 1 < openMenuPos.x) {
            renderMainLoop(delta * inMenuDelta, delta * inMenuDelta);
        }
    }

    private void renderPaused(float delta){

    }

    private void renderMainLoop(float delta, float playerDelta){

        updateLevelInfo(delta);
        updateBackGround(delta);
        updateBackAnimations(delta);
        updateFriendlyTurrets(playerDelta); // must be before updateHomeBase to ensure turrets reposition when their ground is lost.
        updateHomeBase(delta);              // must be after updateFriendlyTurrets
        updateEnemyShips(delta);
        updateEnemySquads(delta);
        updateCityBombs(delta);
        updateHostileProjectiles(delta);
        updatePlayerShip(playerDelta);
        updateFriendlyProjectiles(playerDelta);
        updateFriendlyExplosions(playerDelta);
        updatePowerUps(delta);
        updateMiddleAnimations(delta);
        updateFrontAnimations(delta);
        updateForeground(delta);
        rotateCameraWithPlayer(playerDelta);
        ScreenShake.shake(delta, camera);
        resetCenterCamera();
        checkLevelState();

        updateCamera();

        printScreenText();

    }

    /**
     * update onscreen controls and HUD
     * @param delta
     */
    private void updateHud(float delta){

        //left right and shoot buttons
        ControlBar.update(delta);
        uiPlayerMenu.draw();
    }

    private void printScreenText(){
        SpriteBatch batch = drawingBoard.getTextBatch();
        if(debugMode && showText) {

            SpaceInvadersLevelInfo li = (SpaceInvadersLevelInfo) levelInfo;

            batch.begin();
            game.font.setColor(1f, 1f, 0.5f, 1);
            //draw FPS
            game.font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 5, h - 30);
            //draw left click mode
            game.font.draw(batch, "click: " + leftClickMode, 5, h - 50);
            //draw health
            game.font.draw(batch, "health: " + homeBase.core.health, 5, h - 70);
            //draw weapon
            game.font.draw(batch, "weapon: " + playerShip.currentWeapon.weaponType, 5, h - 90);
            //draw number dead
            game.font.draw(batch, "wave: " + levelInfo.wave, 5, h - 110);
            //draw sandbox mode
            //game.font.draw(game.batch, "gameplay on: " + !sandboxMode, 5, h - 130);

            game.font.draw(batch, "time: " + levelInfo.levelTimer, 5, h - 130);
            game.font.draw(batch, "next squad: " + li.currentWave.nextSquad, 5, h - 150);
            game.font.draw(batch, "next flock: " + li.currentWave.nextFlock, 5, h - 170);
            game.font.draw(batch, "next ship: "  + li.currentWave.nextShip, 5, h - 190);

            batch.end();
        } else {
            batch.begin();
            game.font.setColor(1f, 1f, 0.5f, 1);
            game.font.draw(batch, "wave: " + levelInfo.wave + " / " + levelInfo.maxWaves, 5, h - 30);
            batch.end();
        }
    }

    /**
     * after screen shake the camera may not be centered.
     * translate camera in direction of original center of homebase.
     */
    public void resetCenterCamera(){
        Vector3 n = homeBaseProject.cpy();
        camera.unproject(n);
        camera.translate(n.scl(-1 * 0.5f));
    }

    private void checkLevelState(){
        levelInfo.checkLevelState(this);
        if(!homeBase.isAlive()) {
            uiPlayerMenu.quickClose();
            gameState = Constants.GAME_STATES.PLAYER_LOSS;
        }
    }

    private void updateLevelInfo(float delta){
        if(!sandboxMode)
            levelInfo.update(delta);
    }

    private void updateEnemySquads(float delta) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();
        shapeRenderer.setColor(Color.GREEN);
        for(Iterator<com.neowise.game.squad.Squad> it = enemySquads.iterator(); it.hasNext(); ){
            Squad squad = it.next();

            if(squad.toRemove()){
                it.remove();
                continue;
            }

            squad.update(delta);
        }
        shapeRenderer.end();
    }

    private void updateEnemyShips(float delta){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for(Iterator<Ship> it = ships.iterator(); it.hasNext();) {
            Ship ship = it.next();

            if(ship.toRemove()){
                deadShips.add(ship);
                ship.kill(this);
                it.remove();
                continue;
            }

            ship.update(this, delta);
            ship.renderShapeRenderer(shapeRenderer);
        }
        shapeRenderer.end();
    }

    private void updateCityBombs(float delta){

        for(Iterator<CityBombGroup> it = bombGroups.iterator(); it.hasNext();) {

            CityBombGroup cbg = it.next();

            if(cbg.empty()) {
                it.remove();
                continue;
            }

            cbg.updateTimers(delta);
            cbg.spawnBombs(hostileProjectiles);
        }
    }

    public void renderLoading(float delta){
        gameState = Constants.GAME_STATES.IN_GAME;
    }
    private void renderPlayerWin(float delta) {
        back2StarMap();
    }
    private void renderPlayerLoss(float delta) {

        ControlBar.setInActive();
        uiPlayerLoss.update(delta);
        if(inMenuDelta > 0.3f)
            inMenuDelta *= 1f;

        renderMainLoop(delta*inMenuDelta, delta);

        uiPlayerLoss.draw();
        uiPlayerLoss.setInput();

        if(uiPlayerLoss.back()){
            back2MainMenu();
        }
    }

    private void back2MainMenu(){
        game.setScreen(new MainMenuScreen(game));
    }

    private void back2StarMap(){
        starMap.endLevel();
        game.setScreen(starMap);
        starMap.setInput();
    }

    private void updateProjectiles(Collection<WeaponProjectile> projectiles, float delta){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();

        for(Iterator<WeaponProjectile> it = projectiles.iterator(); it.hasNext(); ) {
            WeaponProjectile projectile = it.next();

            if(projectile.toRemove()) {
                it.remove();
                projectile.dispose();
                continue;
            }

            projectile.update(this, delta);
            projectile.renderShapeRenderer(shapeRenderer);
        }

        if(!tempHostileProjectiles.isEmpty()){
            hostileProjectiles.addAll(tempHostileProjectiles);
            tempHostileProjectiles = new ArrayList<>();
        }

        shapeRenderer.end();
    }

    private void updateHostileProjectiles(float delta){
        updateProjectiles(hostileProjectiles, delta);
    }

    private void updateFriendlyProjectiles(float delta) {
        updateProjectiles(friendlyProjectiles, delta);
    }

    private void updateFriendlyExplosions(float delta) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.FIREBRICK);

        for (Iterator<Explosion> it = friendlyExplosions.iterator(); it.hasNext(); ) {

            Explosion exp = it.next();

            if(exp.duration <= 0){
                it.remove();
                continue;
            }

            exp.update(this, delta);

            shapeRenderer.circle(exp.pos.x, exp.pos.y, exp.size);
        }

        shapeRenderer.end();
    }

    private void updatePowerUps(float delta){

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GOLDENROD);
        shapeRenderer.identity();

        for (Iterator<PowerUp> pit = powerUps.iterator(); pit.hasNext(); ) {
            PowerUp pup = pit.next();

            if(pup.toRemove()){
                pit.remove();
                continue;
            }

            pup.update(delta);
        }

        shapeRenderer.end();
    }

    private void updatePlayerShip(float delta){

        playerShip.update(this, delta);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();
        shapeRenderer.translate(playerShip.pos.x, playerShip.pos.y, 0);
        shapeRenderer.setColor(1 - (playerShip.health / 50), 1.0f, playerShip.health / 50, 1);
        shapeRenderer.rotate(0, 0, 1, (playerShip.rotation));
        shapeRenderer.rect(-playerShip.width/2, 0, playerShip.width, playerShip.height);
        shapeRenderer.end();
    }

    /**
     *  if planet has been hit check if any parts are loose
     */
    private void checkIntegrity() {

        if(homeBase.checkIntegrity && !homeBase.checkingIntegrity) {

            homeBase.checkIntegrity = false;
            homeBase.checkingIntegrity = true;
             if(integrityStage == 1) {
             homeBase.checkIntegrityStage1();
             integrityStage = 2;
             }
             if(integrityStage == 2) {
             homeBase.checkIntegrityStage2();
             integrityStage = 3;
             }
             if(integrityStage == 3) {
             homeBase.checkIntegrityStage3();
             integrityStage = 1;
             homeBase.checkingIntegrity = false;
             homeBase.checkingFinished  = true;
             }
        }

        /**
        if(homeBase.checkIntegrity && !homeBase.checkingIntegrity) {
            homeBase.checkIntegrity = false;
            homeBase.checkingIntegrity = true;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        homeBase.checkIntegrityStage1();
                        homeBase.checkIntegrityStage2();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                homeBase.checkIntegrityStage3();
                                homeBase.checkingIntegrity = false;
                                homeBase.checkingFinished = true;
                            }
                        });

                    } catch (NullPointerException e) {
                        homeBase.checkingIntegrity = false;
                        homeBase.checkingFinished = true;
                    }
                }
            }).start();
        }**/

    }

    private void updateHomeBase(float delta) {

        homeBase.update(this, delta);
        drawingBoard.drawHomeBase(homeBase.pos.x, homeBase.pos.y, homeBase.rotation);
        drawingBoard.drawChunks(homeBase.chunks);
        //TODO REMOVE CHUNKS WHEN OFF SCREEN!

        checkIntegrity();
    }

    private void updateFriendlyTurrets(float delta){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();
        for(Iterator<Defender> it = friendlyTurrets.iterator(); it.hasNext(); ) {

            Defender defender = it.next();
            if(defender.toRemove()){
                it.remove();
                defender.dispose();
                continue;
            }

            if(homeBase.checkingFinished)
                defender.onGround = false;

            defender.update(this, delta);
            defender.renderShapeRenderer(shapeRenderer);
        }

        shapeRenderer.end();
    }

    private String pickStar(){
        int s = RandomUtil.nextInt(10);
        if(s < 5) return "star1";
        if(s < 8) return "star2";
        return "star3";
    }

    private void addInitialStars(){
        //add some initial stars
        float homeBaseDistance = -h;
        while (homeBaseDistance < h*2) {
            homeBaseDistance += 1; 

            if(homeBaseDistance > nextStar){

                nextStar += (10 + RandomUtil.nextInt(12));

                float size = 1 + RandomUtil.nextFloat() * 6;
                BackgroundObject star = new BackgroundObject(
                        new Vector2((RandomUtil.nextFloat()-0.5f) * 2 * h, -h + homeBaseDistance),
                        size,
                        0,
                        0.3f,
                        0.01f * size,
                        pickStar());

                backgroundObjects.add(star);
            }
        }

        nextStar = 0;
    }

    private void addStars(){
        if(homeBase.distance < nextStar){

            nextStar -= (RandomUtil.nextInt(160) + 80);

            float size = 1 + RandomUtil.nextInt(6);
            BackgroundObject star = new BackgroundObject(
                    new Vector2((RandomUtil.nextFloat()-0.5f) * 2 * h, -h),
                    size,
                    0,
                    0.3f,
                    0.01f * size,
                    pickStar());
            backgroundObjects.add(star);

            if(RandomUtil.nextBoolean(0.02f)){
                size = 30 + RandomUtil.nextInt(60);
                star = new BackgroundObject(
                        new Vector2((RandomUtil.nextFloat()-0.5f) * 2 * h, -h),
                        size,
                        RandomUtil.nextInt(360),
                        0.3f,
                        0.001f * size,
                        "bluestar");
                backgroundObjects.add(star);
            }

        }
    }

    /**
     * move background up constantly. If one is far enough off screen swap it to below the other one
     * to make for a constant 'conveyor belt' of background movement.
     * @param delta
     */
    private void updateSpaceBackground(float delta){

        spaceBack.setPosition(spaceBack.getX(), spaceBack.getY() + delta * 10);
        spaceBack2.setPosition(spaceBack2.getX(), spaceBack2.getY() + delta * 10);

        if(spaceBack.getY() > 500){
            spaceBack.setPosition(spaceBack.getX(), spaceBack2.getY()-spaceBack2.getHeight());
        }

        if(spaceBack2.getY() > 500){
            spaceBack2.setPosition(spaceBack2.getX(), spaceBack.getY()-spaceBack.getHeight());
        }

        DrawingBoard.addSprite(spaceBack, DrawingBoard.getBackgroundSprites());
        DrawingBoard.addSprite(spaceBack2, DrawingBoard.getBackgroundSprites());
    }

    private void updateBackGround(float delta){

        updateSpaceBackground(delta);

        for(Iterator<BackgroundObject> bit = backgroundObjects.iterator(); bit.hasNext(); ){

            BackgroundObject bo = bit.next();

            if(bo.pos.y > h * 1.5f) {
                bit.remove();
            }

            bo.pos.sub(homeBase.vel.cpy().scl(delta * bo.speed));
            DrawingBoard.addSpriteFromAtlas(bo.pos.x, bo.pos.y, bo.size, bo.size, bo.alpha, bo.rotation, bo.sprite, DrawingBoard.getBackgroundSprites());
        }

        addStars();

    }

    private void updateForeground(float delta){
        for(Iterator<BackgroundObject> bit = foregroundObjects.iterator(); bit.hasNext(); ){

            BackgroundObject bo = bit.next();

            if(bo.pos.y > h *1.5f) {
                bit.remove();
            }

            bo.pos.sub(homeBase.vel.cpy().scl(delta * bo.speed));
            DrawingBoard.addSpriteFromAtlas(bo.pos.x, bo.pos.y, bo.size, bo.size, bo.alpha, bo.rotation, bo.sprite, DrawingBoard.getBackgroundSprites());

        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        ControlBar.init(w, h);
        resetCamera();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        Vector3 vec = new Vector3(screenX,screenY,0);
        camera.unproject(vec);
        screenX = (int) vec.x;
        screenY = (int) vec.y;

        if(pointer < 5){
            if(leftClickMode == leftClickModes.DROP_LAVA)
                hostileProjectiles.add(new Lava(new Vector2(screenX, screenY), 4, 2));
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        Vector3 vec = new Vector3(screenX,screenY,0);
        camera.unproject(vec);
        float x = vec.x;
        float y = vec.y;

        if(button == Input.Buttons.LEFT ){
            if(debugMode) {
                switch (leftClickMode) {
                    case DROP_BOMB: {
                        hostileProjectiles.add(new Bomb(new Vector2(x, y), 50,3, 300, 1));
                        break;
                    }
                    case SHOOT_BULLET: {
                        Vector2 pos = new Vector2(x, y);
                        Vector2 aim = pos.cpy().nor().scl(-1);
                        Bullet bullet = new BulletHostile(pos.cpy(), aim.rotateDeg(RandomUtil.nextInt(10) - 10), 200, 4, 2);
                        hostileProjectiles.add(bullet);
                        break;
                    }
                    case DROP_CITY_BOMB: {

                    }
                    case DROP_LAVA_BALL: {
                        LavaBomb lb = new LavaBomb(new Vector2(x, y));
                        hostileProjectiles.add(lb);
                        break;
                    }
                    case DROP_HEALTH_BALL: {
                        HealthBomb hb = new HealthBomb(new Vector2(x, y));
                        friendlyProjectiles.add(hb);
                        break;
                    }
                    case SPAWN_BASIC_UNIT: {
                        Ship s = new Ship_TestShip(new Vector2(x, y), homeBase.enemyOrbitRange);
                        ships.add(s);
                        break;
                    }
                    case SPAWN_LARGE_UNIT: {
                        Squad_Formation1 sq = new Squad_Formation1(new Vector2(x, y), homeBase.enemyOrbitRange);
                        Ship s = new Ship_TestLargeShip(new Vector2(x, y));
                        s.joinSquad(sq);
                        ships.add(s);
                        enemySquads.add(sq);

                        int orbs = 7;
                        int ang = 0;
                        for (int i = 0; i < orbs; i++) {
                            Ship so = new Orbiter(new Vector2(x, y), s, 8, ang, 50);
                            ang += 360 / orbs;
                            ships.add(so);
                        }
                        break;
                    }
                    case SPAWN_DART_UNIT: {
                        Ship s = new Dart(new Vector2(x, y), homeBase.enemyOrbitRange, RandomUtil.nextOne());
                        ships.add(s);
                        break;
                    }
                    case SPAWN_LASER_UNIT: {
                        Ship s = new LaserCannonShip(new Vector2(x, y), 20, homeBase.enemyOrbitRange);
                        ships.add(s);

                        int orbs = 12;
                        int ang = 0;
                        for (int i = 0; i < orbs; i++) {
                            Ship so = new Orbiter(new Vector2(x, y), s, 8, ang, 50);
                            ang += 360 / orbs;
                            ships.add(so);
                        }

                        break;
                    }
                    case SPAWN_UPGRADER: {
                        Ship s = new WeaponUpgradeShip(new Vector2(x, y), this);
                        ships.add(s);
                        break;
                    }
                    case SPAWN_SQUAD: {
                        Squad_Formation1 sq = new Squad_Formation1(new Vector2(x, y), homeBase.enemyOrbitRange);
                        Ship_TestLargeShip lb = new Ship_TestLargeShip(new Vector2(x, y));
                        lb.joinSquad(sq);
                        ships.add(lb);

                        for (int i = 0; i < 8; i++) {
                            Ship_TestShip ts1 = new Ship_TestShip(new Vector2(x, y), homeBase.enemyOrbitRange);
                            ts1.joinSquad(sq);
                            ships.add(ts1);
                        }
                        enemySquads.add(sq);
                        break;
                    }
                    case SPAWN_FLOCK: {
                        int dir = RandomUtil.nextOne();
                        for (int i = 0; i < 10; i++) {
                            Vector2 dPos = new Vector2(x + RandomUtil.nextFloat2() * 50, y + RandomUtil.nextFloat2() * 50);
                            Dart dart = new Dart(dPos, homeBase.enemyOrbitRange, dir, i * 10);
                            ships.add(dart);
                        }
                        break;
                    }
                    case ADD_TURRET: {
                        LaserTurret lt = new LaserTurret(new Vector2(x, y));
                        friendlyTurrets.add(lt);
                        break;
                    }
                    case REMOVE_CIRCLE: {
                        homeBase.removePointsBomb(x, y, 8, 3, true);
                        homeBase.setCheckIntegrity();
                        break;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean keyDown(int keycode) {

        /**
        if (keycode == Input.Keys.Q ){
            homeBase.rotationDelta -= 0.1f;
        }
        if (keycode == Input.Keys.E ){
            homeBase.rotationDelta += 0.1f;
        }
         **/
        if (keycode == Input.Keys.P ){
            slow = !slow;
        }
        if ( keycode == Input.Keys.Z ){
            camera.zoom += 0.05;
            updateCamera();
        }
        if ( keycode == Input.Keys.X ){
            camera.zoom -= 0.05;
            updateCamera();
        }
        if ( keycode == Input.Keys.T ){
            showText = !showText;
        }
        if ( keycode == Input.Keys.F){
            toggleFullScreen();
        }
        if ( keycode == Input.Keys.G){
            sandboxMode = !sandboxMode;
        }
        if( keycode == Input.Keys.O){
            debugMode   = !debugMode;
        }
        if ( keycode == Input.Keys.C){
            Settings.showControls = !Settings.showControls;
        }
        if ( keycode == Input.Keys.ESCAPE){
            Gdx.graphics.setWindowedMode(Constants.w,Constants.h);
            initializeCamera();
            updateCamera();
        }

        if(keycode == Input.Keys.NUM_1) {
            switch (leftClickMode) {
                case DROP_BOMB:leftClickMode=leftClickModes.DROP_CITY_BOMB;break;
                case DROP_CITY_BOMB:leftClickMode=leftClickModes.DROP_LAVA;break;
                case DROP_LAVA:leftClickMode=leftClickModes.DROP_LAVA_BALL;break;
                case DROP_LAVA_BALL:leftClickMode=leftClickModes.DROP_HEALTH_BALL;break;
                default:leftClickMode=leftClickModes.DROP_BOMB;break;
            }
        }

        if(keycode == Input.Keys.NUM_2) {
            switch (leftClickMode) {
                case SPAWN_SQUAD:leftClickMode=leftClickModes.SPAWN_FLOCK;break;
                default:leftClickMode=leftClickModes.SPAWN_SQUAD;
            }
        }

        if(keycode == Input.Keys.NUM_3) {
            switch (leftClickMode) {
                case SPAWN_BASIC_UNIT:leftClickMode=leftClickModes.SPAWN_LARGE_UNIT;break;
                case SPAWN_LARGE_UNIT:leftClickMode=leftClickModes.SPAWN_DART_UNIT;break;
                case SPAWN_DART_UNIT:leftClickMode=leftClickModes.SPAWN_LASER_UNIT;break;
                default:leftClickMode=leftClickModes.SPAWN_BASIC_UNIT;
            }
        }

        if(keycode == Input.Keys.NUM_4)
            leftClickMode = leftClickModes.ADD_TURRET;

        if(keycode == Input.Keys.NUM_5){
            switch (playerShip.currentWeapon.weaponType) {
                case FLAK:playerShip.currentWeapon = new ChainGun();break;
                case CHAIN_GUN:playerShip.currentWeapon = new CityDefender();break;
                case BOMB:playerShip.currentWeapon = new LaserBeamGun(playerShip.pos, 7); break;
                default:playerShip.currentWeapon = new ShotGun(); break;
            }
        }

        if(keycode == Input.Keys.NUM_8)
            leftClickMode = leftClickModes.SPAWN_UPGRADER;

        if(keycode == Input.Keys.NUM_9)
            leftClickMode = leftClickModes.REMOVE_CIRCLE;

        if(keycode == Input.Keys.R) {
            homeBase.resetHomeBasePixMap();
            homeBase.setCheckIntegrity();
        }

        return true;
    }

    private void setCameraToHomeBase(){

        homeBaseProject   = new Vector3(0,0,0);
        camera.zoom = homeBase.size / w / 0.5f;
        homeBaseCameraPos = new Vector3(-w/2,-h/2 + homeBase.size / 2, 0);
        camera.translate(homeBaseCameraPos);
        camera.update();
        camera.project(homeBaseProject);
        homeBaseProject.y = h - homeBaseProject.y;
    }

    /**
     *  set camera to game view
     */
    @Override
    public void initializeCamera() {
        super.initializeCamera();
        setCameraToHomeBase();
    }

    @Override
    public void resetCamera() {
        super.resetCamera();
        setCameraToHomeBase();
    }

    @Override
    public void pause() {
        super.pause();
        uiPlayerMenu.quickOpen();
    }
}
