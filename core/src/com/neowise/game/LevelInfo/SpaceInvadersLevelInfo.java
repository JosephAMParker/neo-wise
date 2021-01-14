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

    public float nextSquad, nextSquadReset, nextBoss, nextBossReset;
    private boolean spawnUpgrade;

    public SpaceInvadersLevelInfo(int level) {
        super(level, Constants.GAME_MODE.SPACE_INVADERS);

        levelTimer = 0;
        nextSquadReset = 10;
        nextSquad = 4;

        nextBossReset = 30;
        nextBoss = nextBossReset;

        spawnUpgrade = false;
    }

    @Override
    public void updateTimers(float delta) {
        levelTimer += delta;
        nextSquad -= delta;
        nextBoss  -= delta;
    }

    @Override
    public Constants.GAME_STATES checkLevelState(BasicLevel basicLevel) {
        if(basicLevel.deadShips.size() > 1550)
            return Constants.GAME_STATES.PLAYER_WIN;
        return Constants.GAME_STATES.IN_GAME;
    }

    @Override
    public void update(float delta) {
        updateTimers(delta);
        spawnSquads();
        spawnUpgrade();
    }

    private Vector2 randomSpawnPoint(){
        Vector2 sqPos = new Vector2(1,1);
        sqPos.setLength(300);
        sqPos.setAngleDeg(RandomUtil.nextInt(360));
        return sqPos;
    }

    private void spawnUpgrade() {
        if(basicLevel.deadShips.size() % 10 == 9){
            if(spawnUpgrade) {
                Ship s = new WeaponUpgradeShip(randomSpawnPoint(), basicLevel);
                basicLevel.ships.add(s);
                spawnUpgrade = false;
            }
        } else
            spawnUpgrade = true;
    }

    private void makeFlock(Collection<Ship> ships, float enemyOrbitRange, int count){
        int dir = RandomUtil.nextOne();
        for(int i = 0;i<count;i++){
            Vector2 dPos = randomSpawnPoint();
            dPos.x += RandomUtil.nextFloat2() * 50;
            dPos.y += RandomUtil.nextFloat2() * 50;
            Dart dart = new Dart(dPos, enemyOrbitRange, dir, i * 10);
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

    public void spawnSquads() {
        if(nextSquad < 0){
            if(RandomUtil.nextBoolean())
                makeFullSquadFormation1(basicLevel.enemySquads, basicLevel.ships, basicLevel.homeBase.enemyOrbitRange);
            else
                makeFlock(basicLevel.ships, basicLevel.homeBase.enemyOrbitRange, 10);
            nextSquad = nextSquadReset;
        }

        if(nextBoss < 0){
            nextBoss = nextBossReset;
            nextSquad = 25;
            nextSquadReset -= 1;
            if(nextSquadReset < 5)
                nextSquadReset = 5;

            makeLaserGroup(basicLevel.ships, basicLevel.homeBase.enemyOrbitRange);
        }
    }

    public void spawnEnemyShips(Collection<Ship> ships) {

    }
}
