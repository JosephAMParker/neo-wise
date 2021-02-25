package com.neowise.game.LevelInfo;

import com.neowise.game.gameObject.CityBombGroup.CityBombGroup;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

public class CityDefenderLevelInfo extends LevelInfo{

    private float nextBombGroup, nextBombGroupReset;

    public CityDefenderLevelInfo(int level) {
        super(level, Constants.GAME_MODE.CITY_DEFENDER);

        nextBombGroupReset = 9;
        levelTimer = 0;
        nextBombGroup = 0;
    }

    @Override
    public void updateTimers(float delta) {
        levelTimer += delta;
        nextBombGroup -= delta;
    }

    @Override
    public void checkLevelState(BasicLevel basicLevel) {

    }

    @Override
    public void update(float delta) {
        spawnBombGroups(basicLevel.bombGroups);
        updateTimers(delta);
    }

    public void spawnBombGroups(Collection<CityBombGroup> bombGroups) {
        if (nextBombGroup <= 0){
            nextBombGroup = nextBombGroupReset;

            int numberOfBombs = 8 + RandomUtil.nextInt(4);
            bombGroups.add(new CityBombGroup(RandomUtil.nextInt(360), numberOfBombs));
        }
    }
}
