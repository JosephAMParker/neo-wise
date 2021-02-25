package com.neowise.game.LevelInfo;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.util.Constants;

public abstract class LevelInfo {

    public int level;
    public float levelTimer;
    public Constants.GAME_MODE gameMode;
    public int wave, maxWaves;
    protected BasicLevel basicLevel;

    public LevelInfo(int level, Constants.GAME_MODE gameMode){

        this.level = level;
        this.gameMode = gameMode;
        levelTimer = 0;
    }

    public void setBasicLevel(BasicLevel basicLevel){
        this.basicLevel = basicLevel;
    }

    public abstract void updateTimers(float delta);

    public abstract void checkLevelState(BasicLevel basicLevel);

    public abstract void update(float delta);
}
