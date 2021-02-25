package com.neowise.game.LevelInfo;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.ship.LaserCannonShip;
import com.neowise.game.gameObject.ship.Orbiter;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.ship.Ship_TestLargeShip;
import com.neowise.game.gameObject.ship.Ship_TestShip;
import com.neowise.game.gameObject.ship.WeaponUpgradeShip;
import com.neowise.game.gameObject.ship.enemy.Dart;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.squad.Squad;
import com.neowise.game.squad.Squad_Formation1;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class SpaceInvadersLevelInfo extends LevelInfo {

    public class Wave {

        public int squads, flocks, ships, lasers;
        public float nextSquad, nextFlock, nextShip;
        public float nextSquadReset, nextFlockReset, nextShipReset;
        boolean bossSpawned = false;
        boolean rewardSpawned = false;

        public Wave(int level, int wave) {

            squads = wave;
            flocks = wave + 1;
            ships = 10 + wave * 2 + level * 2;
            lasers = wave;

            nextSquadReset = 16;
            nextFlockReset = 10;
            nextShipReset  = 4;

            if(level > 4){
                lasers += 1;
                squads += 2;
                flocks += 3;
            }

            if(level == 1){
                nextShip += 1;
                nextFlock += 5;
                nextSquad += 10;
            }

            nextShip  = nextShipReset  + RandomUtil.nextInt(2);
            nextFlock = nextFlockReset + RandomUtil.nextInt(  5);
            nextSquad = nextSquadReset;

            if(level == 1 && wave == 1) {
                nextSquad += 20;
                nextFlock += 5;
            }

        }

        public void updateTimers(float delta) {

            if(nextSquad > 0)
                nextSquad -= delta;

            if(nextFlock > 0)
                nextFlock -= delta;

            if(nextShip > 0)
                nextShip -= delta;

        }

        public boolean waveFinished() {
            return rewardSpawned && bossSpawned && basicLevel.ships.isEmpty();
        }

        public boolean spawnReward() {
            return !rewardSpawned && bossSpawned && basicLevel.ships.isEmpty();
        }

        public boolean spawnBoss() {
            return !bossSpawned && squads == 0 && flocks == 0;
        }
    }

    public float endLevelCountDown;
    public Wave currentWave;
    private boolean endLevel = false;

    public SpaceInvadersLevelInfo(int level) {
        super(level, Constants.GAME_MODE.SPACE_INVADERS);

        wave = 1;
        maxWaves = 3;
        if(level > 5)
            maxWaves = 4;

        levelTimer = 0;
        endLevelCountDown = 5;

        currentWave = new Wave(level,wave);
    }

    @Override
    public void updateTimers(float delta) {
        levelTimer += delta;
        currentWave.updateTimers(delta);
        if(endLevel)
            endLevelCountDown -= delta;
    }

    @Override
    public void checkLevelState(BasicLevel basicLevel) {

        if(wave == maxWaves && currentWave.waveFinished() && basicLevel.powerUps.isEmpty())
            endLevel = true;

        if(endLevelCountDown < 0)
            basicLevel.setGameState(Constants.GAME_STATES.PLAYER_WIN);

    }

    @Override
    public void update(float delta) {
        updateTimers(delta);
        spawnSquads();
        spawnFlock();
        spawnShips();
        spawnBoss();
        spawnReward();
        endWave();
    }

    private void endWave() {
        if(currentWave.waveFinished()) {
            if(wave < maxWaves)
                currentWave = new Wave(level, wave++);
        }
    }

    private void spawnReward() {
        if(currentWave.spawnReward()){
            currentWave.rewardSpawned = true;
            for (int i = 0; i < wave; i++) {
                basicLevel.ships.add(new WeaponUpgradeShip(randomSpawnPoint(), basicLevel));
            }
        }
    }

    public void spawnBoss(){
        if(currentWave.spawnBoss()) {
            currentWave.bossSpawned = true;
            for (int i = 0; i < currentWave.lasers; i++) {
                makeLaserGroup(basicLevel.ships, basicLevel.homeBase.enemyOrbitRange);
            }
        }
    }

    public void spawnFlock() {
        if(currentWave.flocks > 0 && currentWave.nextFlock < 0){
            makeFlock(basicLevel.ships, basicLevel.homeBase.enemyOrbitRange, 10 + wave * 2);
            currentWave.nextFlock = currentWave.nextFlockReset + RandomUtil.nextInt(5);
            currentWave.flocks--;
        }
    }

    public void spawnSquads() {
        if(currentWave.squads > 0 && currentWave.nextSquad < 0){
            makeFullSquadFormation1(basicLevel.enemySquads, basicLevel.ships, basicLevel.homeBase.enemyOrbitRange);
            currentWave.nextSquad = currentWave.nextSquadReset + RandomUtil.nextInt(5);
            currentWave.squads--;
        }
    }

    public void spawnShips() {
        if(currentWave.ships > 0 && currentWave.nextShip < 0){
            makeShips(wave);
            currentWave.nextShip = currentWave.nextShipReset + RandomUtil.nextInt(1);
            currentWave.ships--;
        }
    }

    private Vector2 randomSpawnPoint(){
        Vector2 sqPos = new Vector2(1,1);
        sqPos.setLength(750);
        sqPos.setAngleDeg(RandomUtil.nextInt(360));
        return sqPos;
    }

    private void makeShips(int n) {
        for(int i = 0; i<n;i++){
            basicLevel.ships.add(new Ship_TestShip(randomSpawnPoint(), basicLevel.homeBase.enemyOrbitRange));
        }
    }

    private void makeFlock(Collection<Ship> ships, float enemyOrbitRange, int count){
        int dir = RandomUtil.nextOne();
        Vector2 dPos = randomSpawnPoint();
        for(int i = 0;i<count;i++){
            Vector2 dPosCpy = dPos.cpy();
            dPosCpy.rotateDeg(RandomUtil.nextInt(30));
            Dart dart = new Dart(dPosCpy, enemyOrbitRange, dir, i * 10);
            ships.add(dart);
        }
    }

    private void makeLaserGroup(Collection<Ship> ships, float enemyOrbitRange){

        Vector2 pos = randomSpawnPoint();

        Ship s = new LaserCannonShip(pos.cpy(), 20, enemyOrbitRange);
        ships.add(s);

        int orbs = 12;
        int ang  = 0;
        for(int i = 0;i<orbs;i++){
            Ship so = new Orbiter(pos.cpy(), s, 8, ang,50);
            ang += 360/orbs;
            ships.add(so);
        }
    }

    private void makeFullSquadFormation1(Collection<Squad> squads, Collection<Ship> ships, float enemyOrbitRange){

        Vector2 sqPos = randomSpawnPoint();

        Squad_Formation1 sq = new Squad_Formation1(sqPos.cpy(), enemyOrbitRange);
        Ship_TestLargeShip lb = new Ship_TestLargeShip(sqPos.cpy());
        lb.joinSquad(sq);
        ships.add(lb);

        for(int i = 0;i<8;i++){
            Ship_TestShip ts1 = new Ship_TestShip(sqPos.cpy(), enemyOrbitRange);
            ts1.joinSquad(sq);
            ships.add(ts1);
        }

        squads.add(sq);
    }
}
