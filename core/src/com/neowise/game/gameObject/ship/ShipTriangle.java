package com.neowise.game.gameObject.ship;

import com.badlogic.gdx.math.Vector2;
import com.neowise.game.physics.CollisionDetector;

public abstract class ShipTriangle extends Ship{

    public ShipTriangle(Vector2 pos) {
        super(pos);
    }

    @Override
    public boolean collisionLine(Vector2 projectilePos, Vector2 endLaser) {
        return CollisionDetector.collisionLineRect(projectilePos.x, projectilePos.y, endLaser.x, endLaser.y, pos.x, pos.y, width, height, rotation);
    }

    @Override
    public boolean collisionPoint(Vector2 projectilePos) {
        return CollisionDetector.collisionPointRect(projectilePos.x, projectilePos.y, pos.x, pos.y, width, height, rotation);
    }

    @Override
    public boolean collisionCircle(Vector2 projectilePos, float radius) {
        return CollisionDetector.collisionCircleRectangle(projectilePos.x, projectilePos.y, radius, pos.x, pos.y, width, height, rotation);
    }
}
