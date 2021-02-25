package com.neowise.game.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.homeBase.HomeBase;

public class CollisionDetector {

	public static boolean collisionPointPixmap(Vector2 pos, HomeBase homeBase) {
		return collisionPointPixmap(pos, homeBase, 255);
	}

	public static boolean collisionPointPixmap(Vector2 pos, HomeBase homeBase, float max) {

		int x = (int) (pos.x);
		int y = (int) (pos.y);

		if(x >=  homeBase.size/2 ||
		   x < -homeBase.size/2 ||
		   y >  homeBase.size/2 ||
		   y <= -homeBase.size/2)
				return false;

		double rot = 2 * Math.PI - homeBase.rotation;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		x_ += homeBase.pixmap.getWidth() / 2;
		y_ = homeBase.pixmap.getHeight() - y_ - homeBase.pixmap.getHeight() / 2;

		return (homeBase.pixmap.getPixel((int) x_, (int) y_) & 0x000000ff) < max;
	}

	public static Vector2 collisionLinePixMap(Vector2 pos1, Vector2 pos2, HomeBase homeBase){

		Vector2 _pos1 = pos1.cpy();
		Vector2 _pos2 = pos2.cpy();

		float radians = (float) (2 * Math.PI - homeBase.rotation);

		_pos1.rotateRad(radians);
		_pos1.x += homeBase.pixmap.getWidth() / 2;
		_pos1.y =  homeBase.pixmap.getHeight() / 2 - _pos1.y;

		_pos2.rotateRad(radians);
		_pos2.x += homeBase.pixmap.getWidth() / 2;
		_pos2.y =  homeBase.pixmap.getHeight() / 2 - _pos2.y;

		_pos2.sub(_pos1);

		int steps = 3;
		float step = _pos2.len() / steps;

		_pos2.nor().scl(step);
		Color pixel;

		for (int i = 0; i<steps;i++){
			pixel = new Color(homeBase.pixmap.getPixel((int) _pos1.x, (int) _pos1.y));

			if (pixel.a > 0) {
				_pos1.x -= homeBase.pixmap.getWidth() / 2;
				_pos1.y  = homeBase.pixmap.getHeight()/2 - _pos1.y;
				_pos1.rotateRad(-radians);
				return _pos1;
			}
			_pos1.add(_pos2);
		}

		return null;
	}

	public static boolean collisionWithCore(HomeBase homeBase, Vector2 pos){
		return pos.len() < homeBase.core.radius;
	}

	public static boolean collisionCirclePoint(float x, float y, float r, float px, float py) {
		if ((x - px) * (x - px) + (y - py) * (y - py) < r * r)
			return true;
		return false;
	}

	public static boolean collisionLineLine(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){

		// calculate the distance to intersection point
		float dem = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		float uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / dem;
		float uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / dem;

		// if uA and uB are between 0-1, lines are colliding
		if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1)
			return true;

		return false;
	}

	public static boolean collisionLineRect(float x1, float y1, float x2, float y2, float recX, float recY, float recWidth, float recHeight, double rotation) {

		float minX = recX - recWidth/2;
		float maxX = recX + recWidth/2;
		float minY = recY - recHeight/2;
		float maxY = recY + recHeight/2;

		double rot = Math.PI * 2 - rotation * MathUtils.degreesToRadians;

		x1 -= recX;
		x2 -= recX;

		y1 -= recY;
		y2 -= recY;

		float cos = (float) Math.cos(rot);
		float sin = (float) Math.sin(rot);

		float x1_ = (float) (x1 * cos - y1 * sin);
		float y1_ = (float) (x1 * sin + y1 * cos);

		float x2_ = (float) (x2 * cos - y2 * sin);
		float y2_ = (float) (x2 * sin + y2 * cos);

		x1 = x1_;
		x2 = x2_;
		y1 = y1_;
		y2 = y2_;

		x1 += recX;
		x2 += recX;

		y1 += recY;
		y2 += recY;

		// Completely outside.
		if ((x1 <= minX && x2 <= minX) || (y1 <= minY && y2 <= minY) || (x1 >= maxX && x2 >= maxX) || (y1 >= maxY && y2 >= maxY))
			return false;

		float m = (y2 - y1) / (x2 - x1);

		float y = m * (minX - x1) + y1;
		if (y > minY && y < maxY) return true;

		y = m * (maxX - x1) + y1;
		if (y > minY && y < maxY) return true;

		float x = (minY - y1) / m + x1;
		if (x > minX && x < maxX) return true;

		x = (maxY - y1) / m + x1;
		if (x > minX && x < maxX) return true;

		return false;

	}

	public static boolean collisionCircleSquare(float x, float y, float r, float qx, float qy, float qsize) {

		float circleDistance_x = Math.abs(x - (qx));
		float circleDistance_y = Math.abs(y - (qy));

		if (circleDistance_x > (qsize / 2 + r)) {
			return false;
		}
		if (circleDistance_y > (qsize / 2 + r)) {
			return false;
		}

		if (circleDistance_x <= (qsize / 2)) {
			return true;
		}
		if (circleDistance_y <= (qsize / 2)) {
			return true;
		}

		double cornerDistance_sq = Math.pow((circleDistance_x - qsize / 2), 2) + Math.pow((circleDistance_y - qsize / 2), 2);

		return (cornerDistance_sq <= (Math.pow(r, 2)));

	}

	public static boolean clearLineOfSight(Vector2 pos, Vector2 pos2, HomeBase homeBase) {

		// change to one call of collisoinLineQuad(...)
		//return !collisionLineQuad(pos.x,pos.y,pos2.x,pos2.y,homeBase.quad,homeBase.pos,homeBase.rotation);

		Vector2 dpos = pos.cpy(); //defence pos
		Vector2 toTarget = pos.cpy().sub(pos2);
		toTarget.nor();
		dpos.sub(toTarget.scl(5));
		toTarget.scl(0.2f);
		for (int i = 0; i < pos.dst(pos2); i++) {
			dpos.sub(toTarget);

			if (i % 10 == 0) {

				if (collisionPointPixmap(dpos, homeBase))
					return false;

			}
		}

		return true;

	}

	public static boolean collisionCircleCircle(float x, float y, float size,
										 float x2, float y2, float size2) {

		Vector2 circ1 = new Vector2(x, y);
		Vector2 circ2 = new Vector2(x2, y2);

		if (circ1.dst(circ2) <= size + size2)
			return true;
		return false;
	}

	public static boolean collisionCircleCircle(Vector2 circle1, float size,
												Vector2 circle2, float size2) {

		if (circle1.dst(circle2) <= size + size2)
			return true;
		return false;
	}

	public static boolean collisionCircleRectangle(float cx, float cy,
												   float r, float rx, float ry, float width, float height, float rotation) {

		cx -= rx;
		cy -= ry;

		double rot = (Math.PI * 2 - rotation);
		float cx_ = (float) (cx * Math.cos(rot) - cy * Math.sin(rot));
		float cy_ = (float) (cx * Math.sin(rot) + cy * Math.cos(rot));

		float circleDistance_x = Math.abs(cx_ - width / 2);
		float circleDistance_y = Math.abs(cy_ - height / 2);

		if (circleDistance_x > (width / 2 + r)) {
			return false;
		}
		if (circleDistance_y > (height / 2 + r)) {
			return false;
		}

		if (circleDistance_x <= (width / 2)) {
			return true;
		}
		if (circleDistance_y <= (height / 2)) {
			return true;
		}

		double cornerDistance_sq = Math.pow((circleDistance_x - width / 2), 2) +
				Math.pow((circleDistance_y - height / 2), 2);

		return (cornerDistance_sq <= Math.pow(r, 2));
	}


	public static boolean collisionPointRect(float px, float py, float rx, float ry, float width, float height, float rotation) {

		px -= rx;
		py -= ry;

		double rot = (Math.PI * 2 - rotation);
		double cos = Math.cos(rot);
		double sin = Math.sin(rot);
		float px_ = (float) (px * cos - py * sin);
		float py_ = (float) (px * sin + py * cos);

		if (px_ < -width / 2 || px_ > width / 2 || py_ < -height / 2 || py_ > height / 2)
			return false;

		return true;
	}
}