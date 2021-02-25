package com.neowise.game.gameObject.weaponProjectile;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.draw.MyAnimation;
import com.neowise.game.gameObject.defender.Defender;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.homeBase.HomeBase;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.physics.Physics;
import com.neowise.game.util.RandomUtil;

import java.util.Collection;

/**
 * Created by tabletop on 6/19/15.
 */
public class CityBombStraight extends CityBomb{

    public CityBombStraight(Vector2 pos){
        super(pos);
        speed = 50;
        vel = pos.cpy().nor().scl(-speed);
        this.size = 3;
        explosionSize = 50;
    }

    @Override
    public void update(BasicLevel basicLevel, float delta) {
        updatePos(delta);
        HomeBase homeBase = basicLevel.homeBase;

        //if bullet collides with homeBase pixmap, remove
        if (CollisionDetector.collisionPointPixmap(pos, homeBase)){

            Vector2 toPlanet = pos.cpy().nor();
            MyAnimation animation = new MyAnimation("bombtest",8, new Vector2(pos.x, pos.y).add(toPlanet.nor().scl(6)),toPlanet.angleDeg()-90,false,0.08f,1);
            basicLevel.middleAnimations.add(animation);

            homeBase.removePointsBomb(pos.x, pos.y, (int) explosionSize, 20, false);
            toRemove = true;
        }
    }

    @Override
    public void renderShapeRenderer(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0.5f, 0.1f, 0.8f, 1);
        shapeRenderer.rectLine(startPos, pos,3);
        shapeRenderer.circle(pos.x, pos.y, size);
    }

    @Override
    public void dispose() {

    }


}
