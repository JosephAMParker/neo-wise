package com.neowise.game.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.neowise.game.gameObject.defender.LaserTurret;
import com.neowise.game.gameObject.ship.Ship_TestLargeShip;
import com.neowise.game.gameObject.ship.Ship_TestShip;
import com.neowise.game.gameObject.weaponProjectile.PlayerCityBomb;
import com.neowise.game.gameObject.pickup.PowerUp;
import com.neowise.game.menu.StarMap;
import com.neowise.game.gameObject.rocket.Target;
import com.neowise.game.draw.BackgroundObject;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.weaponProjectile.Bomb;
import com.neowise.game.gameObject.weaponProjectile.CityBomb;
import com.neowise.game.gameObject.weaponProjectile.Lava;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.squad.Squad;
import com.neowise.game.squad.Squad_Formation1;
import com.neowise.game.util.OrbitalAngle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BasicLevel extends GameLevelObject implements Screen, InputProcessor  {

    private class BombGroup {

        public float bombTimer,angle,bombDropCount;

        public BombGroup(float angle, int bombDropCount) {

            this.angle=angle;
            this.bombDropCount=bombDropCount;
            bombTimer = 0;
        }
    }

    private int resources = 0;
    private int score     = 0;
    private float totalTime = 0;
    private float nextSquad = 0;
    private float nextShip = 0;
    private float nextStar = 0;
    private float nextStarDelay = 0;
    //private int levelComplete = false;
    private enum GAMESTATES {LOADING, INGAME, PLAYERWIN, PLAYERLOSS}
    private GAMESTATES gameState;
    private enum LevelGameState {LOADING, INGAME, PLAYERWON, PLAYERLOSS, PAUSED};
    private enum leftClickModes {SPAWN_BASIC_UNIT, SPAWN_SQUAD, SPAWN_LARGE_UNIT, ADD_TURRET, DROP_BOMB, DROP_LAVA};
    private leftClickModes leftClickMode = leftClickModes.DROP_BOMB;
    private CollisionDetector collisionDetector;
    private Collection<WeaponProjectile> friendlyProjectiles;
    private Collection<PlayerCityBomb> friendlyCityBombs;
    private Collection<CityBomb> hostileCityBombs;
    private Collection<WeaponProjectile> hostileProjectiles;
    private Collection<Defender> friendlyTurrets;
    private Collection<Target> targets;
    private Collection<PowerUp> powerUps;
    private Collection<Squad> enemySquads;
    private Collection<Ship> ships;
    private Collection<Bomb> bullets;
    private LevelInfo levelInfo;
    private StarMap starMap;

    public BasicLevel(final ScorchedEarthGame game) {

        super(game);

        frontAnimations   = new ArrayList<>();
        ships             = new ArrayList<>();
        powerUps          = new ArrayList<>();
        middleAnimations  = new ArrayList<>();
        backAnimations    = new ArrayList<>();
        bullets           = new ArrayList<>();
        enemySquads       = new ArrayList<>();
        targets           = new ArrayList<>();
        backgroundObjects = new ArrayList<>();
        foregroundObjects = new ArrayList<>();
        friendlyTurrets   = new ArrayList<>();

        friendlyProjectiles = new ArrayList<>();
        hostileProjectiles  = new ArrayList<>();

        levelInfo = game.getCurrentLevelInfo();
        starMap   = game.getStarMap();
        gameState = GAMESTATES.LOADING;

        homeBase.goToStart(levelInfo);
        playerShip.goToStart(levelInfo);

        addStars();
    }

    private float setGameSpeed(float delta){

        if(slow)
            delta /= 10;

        return delta;
    }

    private void updateTimers(float delta){
        totalTime += delta;
        nextShip  += delta;
        nextSquad += delta;
    }

    private void updateBackGroundColours(){
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void addStars(){
        //add some initial stars
        for (int i = 0; i < 80; i ++){
            BackgroundObject star = new BackgroundObject(new Vector2((random.nextFloat()-0.5f) * 2 * h,(random.nextFloat()-0.5f) * h * 2f),0.2f,"star");
            backgroundObjects.add(star);
        }
    }

    private void checkEndLevelState(float delta){

        if(homeBase.distance <= levelInfo.levelDistance)
            gameState = GAMESTATES.PLAYERWIN;
    }

    @Override
    public void render(float delta) {

        if(delta>maxDelta)
            maxDelta=delta;

        delta = setGameSpeed(delta);
        updateTimers(delta);

        switch (gameState) {

            case LOADING    :   {renderLoading(delta);      break;  }
            case INGAME     :   {renderMainLoop(delta);     break;  }
            case PLAYERLOSS :   {renderPlayerLoss(delta);   break;  }
            case PLAYERWIN  :   {renderPlayerWin(delta);    break;  }
        }

    }
    private void renderMainLoop(float delta){

        updateBackGroundColours();
        updateBackGround(delta);
        updateBackAnimations(delta);
        updateHomeBase(delta);
        updateFriendlyTurrets(delta);
        updateHostileProjectiles(delta);
        updateEnemySquads(delta);
        updateEnemyShips(delta);
        updatePlayerShip(delta);
        updateFriendlyProjectiles(delta);
        updateMiddleAnimations(delta);
        updateFrontAnimations(delta);
        updateForeground(delta);
        rotateCameraWithPlayer(delta);

        checkEndLevelState(delta);

        drawingBoard.draw();

        //FPS draw FPS
        game.batch.begin();
        game.font.setColor(1f, 1f, 0.5f, 1);
        game.font.draw(game.batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 5, h - 30);
        game.batch.end();
        //FPS draw FPS

        //FPS draw left click mode
        game.batch.begin();
        game.font.setColor(1f, 1f, 0.5f, 1);
        game.font.draw(game.batch, "click: " + leftClickMode, 5, h - 50);
        game.batch.end();
        //FPS draw left click mode
    }


    private void updateHostileProjectiles(float delta){

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();

        for(Iterator<WeaponProjectile> it = hostileProjectiles.iterator(); it.hasNext(); ) {
            WeaponProjectile hostileProjectile = it.next();

            hostileProjectile.update(delta, homeBase, friendlyTurrets, frontAnimations);
            hostileProjectile.renderShapeRenderer(shapeRenderer);
            if(hostileProjectile.toRemove()) {
                it.remove();
                hostileProjectile.dispose();
            }
        }

        shapeRenderer.end();
    }

    private void updateEnemySquads(float delta) {

        for(Iterator<com.neowise.game.squad.Squad> it = enemySquads.iterator(); it.hasNext(); ){
            Squad squad = it.next();

            if (squad.empty())
                squad.dead = true;

            if (squad.dead)
                it.remove();

            squad.updatePos(delta);
            squad.updateTimers(delta);
            squad.performAction(delta);
        }
    }

    private boolean checkEnemyShipAlive(Iterator<Ship> it, Ship ship){

        if (ship.health <= 0)
            ship.dead = true;

        if(ship.dead) {
            //destroy
            ship.removeFromSquad();
            ship.animation.alive = false;
            resources += ship.resWorth;
            it.remove();
            return false;
        }
        return true;
    }

    private void attemptToJoinSquad(Ship ship) {
        for(Iterator<Squad> sqit = enemySquads.iterator(); sqit.hasNext();){
            Squad squad = sqit.next();
            float distanceToSquad = OrbitalAngle.distance(ship.pos.angle(), squad.pos.angle());
            if (Math.abs(distanceToSquad) < 30) {
                ship.joinSquad(squad);
                return;
            }
        }
    }

    private void updateEnemyShips(float delta) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(Iterator<Ship> it = ships.iterator(); it.hasNext();) {

            Ship ship = it.next();
            if(checkEnemyShipAlive(it, ship)) {
                ship.updateTimers(delta);
                ship.updateTargetPos(delta);
                ship.updatePos(delta);
                ship.performAction();
                ship.fire(hostileProjectiles);
                attemptToJoinSquad(ship);
            }

//          ship.animation.updatePos(ship.pos.cpy());
//          ship.animation.rotation = ship.rotation;

            renderShipsShapeRenderer(ship, shapeRenderer);

        }
        shapeRenderer.end();
    }

    private void renderShipsShapeRenderer(Ship ship, ShapeRenderer shapeRenderer){

        shapeRenderer.identity();
        shapeRenderer.translate(ship.pos.x, ship.pos.y, 0);
        shapeRenderer.setColor(1-(ship.health/50),0,ship.health/50,1);
        shapeRenderer.rotate(0, 0, 1, ship.rotation);
        shapeRenderer.rect(-ship.width/2,-ship.height/2, ship.width, ship.height);
    }

    public void renderLoading(float delta){
        gameState = GAMESTATES.INGAME;
    }
    private void renderPlayerWin(float delta) {
        endLevel(delta);
    }
    private void renderPlayerLoss(float delta) {
        endLevel(delta);
    }

    private void endLevel(float delta){

        starMap.endLevel();
        game.setScreen(starMap);
        starMap.setInput();
    }

    private void updateFriendlyProjectiles(float delta) {
        //TODO remove projectiles after leaving play area

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();


        Collection<WeaponProjectile> hitProjectiles = new ArrayList<com.neowise.game.gameObject.weaponProjectile.WeaponProjectile>();

        for(Iterator<WeaponProjectile> it = friendlyProjectiles.iterator(); it.hasNext(); ) {

            WeaponProjectile fprojectile = it.next();
            drawingBoard.drawProjectileShapeRenderer(fprojectile, shapeRenderer);

            for(Iterator<Ship> shit = ships.iterator(); shit.hasNext(); ){

                Ship ship = shit.next();
                float collisionSpot = CollisionDetector.collisionWithRectangleGameObject(ship,fprojectile,shapeRenderer);
                if(collisionSpot != 2){
                    hitProjectiles.add(fprojectile);
                    ship.health -= fprojectile.damage;
                }
            }

            fprojectile.updatePos(delta);

        }

        for(Iterator<WeaponProjectile> hit = hitProjectiles.iterator(); hit.hasNext(); ) {

            WeaponProjectile hitproj = hit.next();
            friendlyProjectiles.remove(hitproj);

        }
        shapeRenderer.end();

    }

    private void updatePlayerShip(float delta){

        playerShip.updateTimers(delta);
        playerShip.getInput(delta);
        playerShip.updatePos(delta);
        playerShip.drag(delta);

        if (playerShip.shootWeapon) {
            playerShip.bulletCoolDown = 0.5f;
            Vector2 up = homeBase.getPos().sub(playerShip.getPos()).nor().scl(-1);
            friendlyProjectiles.addAll(playerShip.fire(up, delta));
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();
        shapeRenderer.translate(playerShip.pos.x, playerShip.pos.y, 0);
        shapeRenderer.setColor(1 - (playerShip.health / 50), 1.0f, playerShip.health / 50, 1);
        shapeRenderer.rotate(0, 0, 1, (playerShip.rotation));
        shapeRenderer.rect(0, 0, playerShip.width, playerShip.height);
        shapeRenderer.end();
    }

    /**
     *  if planet has been hit check if any parts are loose
     */
    private void checkIntegrity() {
        if(homeBase.checkIntegrity) {
            homeBase.checkIntegrity = false;
            updateBoundingBox = true;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if(!homeBase.checkingIntegrity) {
                            homeBase.checkIntegrityStage1();

                            homeBase.checkIntegrityStage2();

                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    homeBase.checkIntegrityStage3();
                                    updateBoundingBox = true;
                                }
                            });
                        }
                    }catch (NullPointerException e){

                    }
                }
            }).start();
        }
    }

    /**
     * Speed up homebase to cruising speed.
     * @param delta
     */
    private void speedUpHomeBase(float delta){

        if(homeBase.vel.len2() < 80000)
            homeBase.vel.add(0,- 300 * delta);
    }

    private void updateHomeBase(float delta) {

        if(!homeBase.centreIntact)
            gameOver = true;

        checkIntegrity();
        if(!playerShip.flyToPosition)
            speedUpHomeBase(delta);

        homeBase.distance += homeBase.vel.y * delta;
        homeBase.rotate(delta);
        homeBase.updateChunks(delta);

        if(updateBoundingBox){
            updateBoundingBox = false;
            drawingBoard.updateTexture(homeBase.pixmap);
        }

        drawingBoard.drawHomeBase(homeBase.pos.x, homeBase.pos.y, homeBase.rotation);
        drawingBoard.drawChunks(homeBase.chunks);
        //TODO REMOVE CHUNKS WHEN OFF SCREEN!
    }

    private void updateFriendlyTurrets(float delta){

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(Iterator<Defender> it = friendlyTurrets.iterator(); it.hasNext(); ) {
            Defender t = it.next();
            if (t.health <= 0) {
                it.remove();
                t.dispose();
                continue;
            }

            t.locateGround(homeBase, delta);
            t.rotateByPlanet(homeBase.rotationDelta * delta, homeBase.pos);

            LaserTurret tl = (LaserTurret) t;
            tl.updateTimer(delta);

            //Vector pointing to the ship + offset
            Vector2 toTarget = tl.aimAtTarget(delta);

            //check if target is dead, lose target
            //check if target not in sight or out of range, if true lose target
            if(tl.hasTarget && (tl.targetDead() ||
                    (!CollisionDetector.clearLineOfSight(t.pos,toTarget, homeBase,shapeRenderer) ||
                            !CollisionDetector.collisionCircleSquare(t.pos.x, t.pos.y, tl.range, tl.getTargetPos().x, tl.getTargetPos().y, 1)))){
                tl.loseTarget();
            }

            if(tl.hasTarget){

                if(tl.isArmed()){
                    t.fire(friendlyProjectiles);
                    //Vector2 offset = t.getTarget().vel.cpy().scl(9);
                }
            }

            if (!tl.hasTarget() ) {

                //Acquire target
                //TODO goes through this list intelligently... sort by nearest to turret, health left, ship strength etc etc
                for (Ship ship : ships) {

                    if(CollisionDetector.collisionCircleSquare(t.pos.x, t.pos.y,150, ship.pos.x, ship.pos.y, 1)){

                        //aimAtBird: Vector pointing to the bird
                        toTarget = ship.getPos();
                        toTarget.add(ship.getVel().scl(15));

                        // if there is a clear line of sight to the bird.. set target
                        if(!ship.dead && CollisionDetector.clearLineOfSight(t.pos,toTarget, homeBase,shapeRenderer)){
                            tl.setTarget(ship);
                            break;
                        }
                    }
                }
            }

            //draw turret
            shapeRenderer.setColor(0.66f, 0.709f, 0.6f, 1);
            shapeRenderer.circle(t.pos.x, t.pos.y, t.size);
        }
        shapeRenderer.end();
    }

    private void updateBackGround(float delta){

        for(Iterator<BackgroundObject> bit = backgroundObjects.iterator(); bit.hasNext(); ){

            BackgroundObject bo = bit.next();

            if(bo.pos.y > h * 1.5f) {
                bit.remove();
            }

            bo.pos.sub(homeBase.vel.cpy().scl(delta * bo.speed));
            drawingBoard.addSpriteFromAtlas(bo.pos.x, bo.pos.y, 0,bo.scaleMul, bo.sprite);
        }

        nextStarDelay = random.nextInt(80) + 40;

        if(homeBase.distance < nextStar){

            nextStar -= nextStarDelay;
            BackgroundObject star = new BackgroundObject(
                    new Vector2((random.nextFloat()-0.5f) * 4 * h, -h),
                    0.2f,
                    1,
                    random.nextFloat()+0.5f,
                    "star");
            backgroundObjects.add(star);

        }
    }

    private void updateForeground(float delta){
        for(Iterator<BackgroundObject> bit = foregroundObjects.iterator(); bit.hasNext(); ){

            BackgroundObject bo = bit.next();

            if(bo.pos.y > h *1.5f) {
                bit.remove();
            }

            bo.pos.sub(homeBase.vel.cpy().scl(delta * bo.speed));

            //drawingBoard.addSpriteFromAtlas(bo.pos.x, bo.pos.y, bo.atlasKey);
            drawingBoard.addSpriteFromAtlasToForeGround(bo.pos.x, bo.pos.y, 0, bo.scaleMul, bo.sprite);

        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        Vector3 vec = new Vector3(screenX,screenY,0);
        camera.unproject(vec);
        screenX = (int) vec.x;
        screenY = (int) vec.y;

        if(pointer < 5){
            if(leftClickMode == leftClickModes.DROP_LAVA)
                hostileProjectiles.add(new Lava(screenX, screenY, 5));
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
        screenX = (int) vec.x;
        screenY = (int) vec.y;

        if(button == Input.Buttons.LEFT ){
            switch (leftClickMode) {
                case DROP_BOMB:{
                    hostileProjectiles.add(new Bomb(screenX, screenY, 3, 10));
                    break;
                }
                case SPAWN_BASIC_UNIT:{
                    Ship s = new Ship_TestShip(screenX, screenY, 5);
                    ships.add(s);
                    break;
                }
                case SPAWN_LARGE_UNIT:{
                    Squad_Formation1 sq = new Squad_Formation1((new Vector2(screenX, screenY)));
                    Ship s = new Ship_TestLargeShip(screenX, screenY);
                    s.joinSquad(sq);
                    ships.add(s);
                    enemySquads.add(sq);
                    break;
                }
                case SPAWN_SQUAD:{
                    Squad_Formation1 sq = new Squad_Formation1(new Vector2(screenX, screenY));
                    Ship_TestLargeShip lb = new Ship_TestLargeShip(screenX, screenY);
                    lb.joinSquad(sq);
                    ships.add(lb);

                    for(int i = 0;i<8;i++){
                        Ship_TestShip ts1 = new Ship_TestShip(screenX, screenY, 1);
                        ts1.joinSquad(sq);
                        ships.add(ts1);
                    }
                    enemySquads.add(sq);
                    break;
                }
                case ADD_TURRET:{
                    LaserTurret lt = new LaserTurret(new Vector2(screenX, screenY));
                    friendlyTurrets.add(lt);
                }
            }
        }

        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.P ){
            slow = !slow;
        }
        if ( keycode == Input.Keys.Z ){
            camera.zoom += 0.05;
            camera.update();
            drawingBoard.updateCamera(camera);
            shapeRenderer.setProjectionMatrix(camera.combined);
            batch.setProjectionMatrix(camera.combined);
        }
        if ( keycode == Input.Keys.X ){
            camera.zoom -= 0.05;
            camera.update();
            drawingBoard.updateCamera(camera);
            shapeRenderer.setProjectionMatrix(camera.combined);
            batch.setProjectionMatrix(camera.combined);
        }

        if( keycode == Input.Keys.NUM_1)
            leftClickMode = leftClickModes.DROP_BOMB;

        if (keycode == Input.Keys.NUM_2)
            leftClickMode = leftClickModes.DROP_LAVA;

        if(keycode == Input.Keys.NUM_3)
            leftClickMode = leftClickModes.SPAWN_SQUAD;

        if(keycode == Input.Keys.NUM_4)
            leftClickMode = leftClickModes.SPAWN_BASIC_UNIT;

        if(keycode == Input.Keys.NUM_5)
            leftClickMode = leftClickModes.SPAWN_LARGE_UNIT;

        if(keycode == Input.Keys.NUM_5)
            leftClickMode = leftClickModes.ADD_TURRET;

        return true;
    }

    /**
     *  set camera to game view
     *  move to subclass
     */
    public void initializeCamera() {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();
        hudRenderer   = new ShapeRenderer();
        hudRenderer.setProjectionMatrix(camera.combined);
        camera.translate(0,0);
        camera.translate(-w/2,-h*0.3f);
        camera.zoom -= 0.3f;
        camera.update();
    }

}
