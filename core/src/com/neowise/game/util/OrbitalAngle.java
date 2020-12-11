package com.neowise.game.util;

/**
 * Created by tabletop on 5/28/15.
 */
public class OrbitalAngle {

    public float angle;

    public OrbitalAngle(float angle){

        if (angle > 360)
            this.angle = angle - 360;
        else if (angle < 0)
            this.angle = 360 - angle;
        else
            this.angle = angle;
    }

    public boolean clockwiseOf(OrbitalAngle target){

        if (angle < 180)
            return (target.angle > angle && target.angle < angle + 180);
        else
            return !(target.angle > angle - 180 && target.angle < angle);

    }

    public static float distance(float angle1, float angle2){

        float result = (angle1 - angle2);

        if (result > 180){

            if (angle1 > angle2)

                return (angle1 - 360 - angle2);

            return (angle2 - 360 - angle1);

        }

        return result;
    }

    public float distance(OrbitalAngle target){

        float result = (angle - target.angle);

        if (result > 180){

            if (angle > target.angle)

                return (angle - 360 - target.angle);

            return (target.angle - 360 - angle);

        }

        return result;
    }

    public float distance2(OrbitalAngle target){

        float result = (angle - target.angle)*(angle - target.angle);

        if (result > 32400){

            if (angle > target.angle)

                return (angle - 360 - target.angle)*(angle - 360 - target.angle);

            return (target.angle - 360 - angle) * (target.angle - 360 - angle);

        }

        return result;

    }

    public OrbitalAngle add(float ad) {
        return new OrbitalAngle(angle + ad);
    }
}
