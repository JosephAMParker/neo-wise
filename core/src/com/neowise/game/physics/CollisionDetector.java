package com.neowise.game.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.neowise.game.gameObject.weaponProjectile.Laser;
import com.neowise.game.gameObject.weaponProjectile.PlayerOrbiterBomb;
import com.neowise.game.gameObject.RectangleGameObject;
import com.neowise.game.gameObject.ship.Ship;
import com.neowise.game.gameObject.weaponProjectile.Bullet;
import com.neowise.game.gameObject.weaponProjectile.WeaponProjectile;
import com.neowise.game.homeBase.HomeBase;

class gridBlock {
	
	Collection<WeaponProjectile> entities;
	
	public gridBlock(){
		
		entities = new ArrayList<>();
		
	}
	
}

public class CollisionDetector {


	private float gridWidth;
	private float gridHeight;
	private int gridDiv;
	gridBlock[] gridHostile; //grid with hostile weapons in it
	gridBlock[] gridFriendley; //grid with friendley weapons in it.


	public CollisionDetector(float w, float h) {

		gridDiv = 1;
		gridWidth = w / gridDiv;
		gridHeight = h / gridDiv;
		gridHostile = new gridBlock[(gridDiv * gridDiv)];
		gridFriendley = new gridBlock[(gridDiv * gridDiv)];

		for (int i = 0; i < gridDiv * gridDiv; i++) {
			gridHostile[i] = new gridBlock();
			gridFriendley[i] = new gridBlock();
		}

	}

	public static boolean collition(float x, float y, HomeBase homeBase) {

		return collisionCirclePixMap(x, y, homeBase.pixmap, homeBase.pos, homeBase.rotation);
	}

	public static boolean collision(Vector2 pos, Vector2 homeBasePos, double rotation, Pixmap pixmap) {

		int x = (int) (pos.x - homeBasePos.x);
		int y = (int) (pos.y - homeBasePos.y);

		double rot = 2 * Math.PI - rotation;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		x_ += pixmap.getWidth() / 2;
		y_ = pixmap.getHeight() - y_ - pixmap.getHeight() / 2;

		Color pixel = new Color(pixmap.getPixel((int) x_, (int) y_));
		if (pixel.a > 0)
			return true;
		return false;
	}

	private static boolean collisionCirclePixMap(float x, float y, Pixmap pixmap, Vector2 HBpos, double HBrotation) {

		x -= HBpos.x;
		y -= HBpos.y;

		//System.out.println("x: " + x + " y: " + y);
		double rot = 2 * Math.PI - HBrotation;

		float x_ = (float) (x * Math.cos(rot) - y * Math.sin(rot));
		float y_ = (float) (x * Math.sin(rot) + y * Math.cos(rot));

		x_ += pixmap.getWidth() / 2;
		y_ = pixmap.getHeight() - y_ - pixmap.getHeight() / 2;


		Color pixel = new Color(pixmap.getPixel((int) x_, (int) y_));

		if (pixel.a > 0)
			return true;

		return false;
	}

	public static boolean collisionCirclePoint(float x, float y, float r, float px, float py) {

		if ((x - px) * (x - px) + (y - py) * (y - py) < r * r)
			return true;
		return false;
	}

	public static boolean collisionLineRect(float x1, float y1, float x2, float y2, float recX, float recY, float recWidth, float recHeight, double rotation, ShapeRenderer shapeRenderer) {

		float minX = recX;
		float maxX = recX + recWidth;
		float minY = recY;
		float maxY = recY + recHeight;

		double rot = Math.PI * 2 - rotation;


		x1 -= recX;
		x2 -= recX;

		y1 -= recY;
		y2 -= recY;

		float x1_ = (float) (x1 * Math.cos(rot) - y1 * Math.sin(rot));
		float y1_ = (float) (x1 * Math.sin(rot) + y1 * Math.cos(rot));

		float x2_ = (float) (x2 * Math.cos(rot) - y2 * Math.sin(rot));
		float y2_ = (float) (x2 * Math.sin(rot) + y2 * Math.cos(rot));

		x1 = x1_;
		x2 = x2_;
		y1 = y1_;
		y2 = y2_;

		x1 += recX;
		x2 += recX;

		y1 += recY;
		y2 += recY;


		x1 += recWidth/2;
		x2 += recWidth/2;

		y1 +=recHeight/2;
		y2 +=recHeight/2;


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

	public static boolean collisionLineRect(float x1, float y1, float x2, float y2, float recX, float recY, float recWidth, float recHeight, double rotation) {

		//float minX = recX;
		//float maxX = recX + recWidth;
		///float minY = recY;
		//float maxY = recY + recHeight;

		recX -= recWidth / 2;
		recY -= recHeight / 2;

		float minX = 0;
		float maxX = 0 + recWidth;
		float minY = 0;
		float maxY = 0 + recHeight;

		double rot = Math.PI * 2 - rotation;


		x1 -= recX;
		x2 -= recX;

		y1 -= recY;
		y2 -= recY;

		float x1_ = (float) (x1 * Math.cos(rot) - y1 * Math.sin(rot));
		float y1_ = (float) (x1 * Math.sin(rot) + y1 * Math.cos(rot));

		float x2_ = (float) (x2 * Math.cos(rot) - y2 * Math.sin(rot));
		float y2_ = (float) (x2 * Math.sin(rot) + y2 * Math.cos(rot));

		x1 = x1_;
		x2 = x2_;
		y1 = y1_;
		y2 = y2_;

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

	public static boolean clearLineOfSight(Vector2 pos, Vector2 pos2, HomeBase homeBase, ShapeRenderer sh) {

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

				if (collition(dpos.x, dpos.y, homeBase))
					return false;

			}
		}

		return true;

	}


	public boolean updateGrid(com.neowise.game.gameObject.weaponProjectile.WeaponProjectile proj) {

		int x, y, gridPos;

		x = (int) Math.floor(proj.pos.x / gridWidth);
		y = (int) Math.floor(proj.pos.y / gridHeight);

		//if(x < 0 || y < 0)
		//	return false;

		gridPos = (int) (gridDiv * y + x);

		try {

			if (proj.gridPos == -1) {
				gridFriendley[gridPos].entities.add(proj);
				proj.gridPos = gridPos;
			} else if (proj.gridPos != gridPos) {

				gridFriendley[(proj.gridPos)].entities.remove(proj);
				gridFriendley[gridPos].entities.add(proj);
				proj.gridPos = gridPos;

			}
			return true;
			//grid.get(gridPos).add(laser);

		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

	}

	/**
	 * @param ship, the ship to check againt collisions
	 * @return the x position of where the collision occured as a ratio. 1 = right end, 0 = middle (or anywhere if x pos doesnt matter), -1 = left end.
	 * 2 means no collision
	 */
	public static float collisionWithRectangleGameObject(com.neowise.game.gameObject.RectangleGameObject ship, com.neowise.game.gameObject.weaponProjectile.WeaponProjectile proj) {

		if (proj instanceof Laser) {
			if (collisionLineRect(proj.pos.x, proj.pos.y, proj.getPos().add(proj.vel).x, proj.getPos().add(proj.vel).y, ship.pos.x, ship.pos.y, ship.width, ship.height, ship.rotation)) {
				return 0;
			}


		}

		if (proj instanceof com.neowise.game.gameObject.weaponProjectile.Bullet) {

			//if (collisionCircleRectangle(proj.pos.x, proj.pos.y, proj.size, ship.pos.x,ship.pos.y,ship.width,ship.height,ship.rotation)){
			//	return true;
			//}

			return collisionPointRect(proj.pos.x, proj.pos.y, ship.pos.x, ship.pos.y, ship.width, ship.height, ship.rotation);
		}

		return 2;


	}

	public static float collisionWithRectangleGameObject(RectangleGameObject ship, WeaponProjectile proj, ShapeRenderer shapeRenderer) {

		if (proj instanceof Laser) {
			Vector2 endLaser = proj.getPos().add(proj.getVel().nor().scl(((Laser) proj).length));
			if (collisionLineRect(proj.pos.x, proj.pos.y, endLaser.x, endLaser.y, ship.pos.x, ship.pos.y, ship.width, ship.height, ship.rotation, shapeRenderer)) {
				return 0;
			}
		}

		if (proj instanceof Bullet) {
			return collisionPointRect(proj.pos.x, proj.pos.y, ship.pos.x, ship.pos.y, ship.width, ship.height, ship.rotation);
		}

		return 2;


	}

	/**
	 * Weaponprojectiles are placed in gridFriendly, in their place depending on weaponprojectile(x,y). Parameter bird then checks collision detection with all weaponProjectiles in that grid position.
	 *
	 * @param bird instead of ship for some reason
	 * @return
	 */
	public Collection<WeaponProjectile> collisionWithGrid(Ship bird) {

		int x, y, x_, y_;
		Collection<com.neowise.game.gameObject.weaponProjectile.WeaponProjectile> hitProjectiles = new ArrayList<com.neowise.game.gameObject.weaponProjectile.WeaponProjectile>();

		x = (int) Math.floor(bird.pos.x / gridWidth);
		y = (int) Math.floor(bird.pos.y / gridHeight);

		//should rotate ... 
		x_ = (int) Math.floor((bird.pos.x + bird.width) / gridWidth);
		y_ = (int) Math.floor((bird.pos.y + bird.height) / gridHeight);

		try {
			for (Iterator<com.neowise.game.gameObject.weaponProjectile.WeaponProjectile> it = gridFriendley[(gridDiv * y + x)].entities.iterator(); it.hasNext(); ) {
				//for (Laser laser : grid[(gridDiv*y+x)].entities){
				com.neowise.game.gameObject.weaponProjectile.WeaponProjectile proj = it.next();

				if (proj instanceof Laser) {
					Vector2 endLaser = proj.getPos().add(proj.getVel().nor().scl(((Laser) proj).length));
					if (collisionLineRect(proj.pos.x, proj.pos.y, endLaser.x, endLaser.y, bird.pos.x, bird.pos.y, bird.width, bird.height, bird.rotation)) {
						hitProjectiles.add(proj);
						it.remove();
						//return true;
					}
				}

				if (proj instanceof PlayerOrbiterBomb || proj instanceof com.neowise.game.gameObject.weaponProjectile.Bullet) {

					if (collisionCircleRectangle(proj.pos.x, proj.pos.y, proj.size, bird.pos.x, bird.pos.y, bird.width, bird.height, bird.rotation)) {
						hitProjectiles.add(proj);
						it.remove();
					}
				}

			}

			if (x_ > x) {

				for (Iterator<com.neowise.game.gameObject.weaponProjectile.WeaponProjectile> it = gridFriendley[(gridDiv * y + x_)].entities.iterator(); it.hasNext(); ) {
					//for (Laser laser : grid[(gridDiv*y+x_)].entities){

					com.neowise.game.gameObject.weaponProjectile.WeaponProjectile proj = it.next();

					if (proj instanceof Laser) {
						if (collisionLineRect(proj.pos.x, proj.pos.y, proj.getPos().add(proj.vel).x, proj.getPos().add(proj.vel).y, bird.pos.x, bird.pos.y, bird.width, bird.height, bird.rotation)) {
							hitProjectiles.add(proj);
							it.remove();
							//return true;
						}
					}

					if (proj instanceof PlayerOrbiterBomb || proj instanceof com.neowise.game.gameObject.weaponProjectile.Bullet) {

						if (collisionCircleRectangle(proj.pos.x, proj.pos.y, proj.size, bird.pos.x, bird.pos.y, bird.width, bird.height, bird.rotation)) {
							hitProjectiles.add(proj);
							it.remove();
						}
					}

				}
			}

			if (y_ > y) {

				for (Iterator<com.neowise.game.gameObject.weaponProjectile.WeaponProjectile> it = gridFriendley[(gridDiv * y_ + x)].entities.iterator(); it.hasNext(); ) {
					//for (Laser laser : grid[(gridDiv*y_+x)].entities){

					com.neowise.game.gameObject.weaponProjectile.WeaponProjectile proj = it.next();

					if (proj instanceof Laser) {
						if (collisionLineRect(proj.pos.x, proj.pos.y, proj.getPos().add(proj.vel).x, proj.getPos().add(proj.vel).y, bird.pos.x, bird.pos.y, bird.width, bird.height, bird.rotation)) {
							hitProjectiles.add(proj);
							it.remove();
							//return true;
						}
					}

					if (proj instanceof PlayerOrbiterBomb || proj instanceof com.neowise.game.gameObject.weaponProjectile.Bullet) {

						if (collisionCircleRectangle(proj.pos.x, proj.pos.y, proj.size, bird.pos.x, bird.pos.y, bird.width, bird.height, bird.rotation)) {
							hitProjectiles.add(proj);
							it.remove();
						}
					}

				}
			}

			if (x_ > x && y_ > y) {

				for (Iterator<com.neowise.game.gameObject.weaponProjectile.WeaponProjectile> it = gridFriendley[(gridDiv * y_ + x_)].entities.iterator(); it.hasNext(); ) {
					//for (proj laser : grid[(gridDiv*y_+x_)].entities){

					WeaponProjectile proj = it.next();

					if (proj instanceof Laser) {
						if (collisionLineRect(proj.pos.x, proj.pos.y, proj.getPos().add(proj.vel).x, proj.getPos().add(proj.vel).y, bird.pos.x, bird.pos.y, bird.width, bird.height, bird.rotation)) {
							hitProjectiles.add(proj);
							it.remove();
							//return true;
						}
					}

					if (proj instanceof PlayerOrbiterBomb || proj instanceof Bullet) {

						if (collisionCircleRectangle(proj.pos.x, proj.pos.y, proj.size, bird.pos.x, bird.pos.y, bird.width, bird.height, bird.rotation)) {
							hitProjectiles.add(proj);
							it.remove();
						}
					}

				}
			}


		} catch (ArrayIndexOutOfBoundsException e) {
		}


		return hitProjectiles;
	}

	public static boolean collisionCircleCircle(float x, float y, float size,
										 float x2, float y2, float size2) {

		Vector2 circ1 = new Vector2(x, y);
		Vector2 circ2 = new Vector2(x2, y2);

		if (circ1.dst(circ2) <= size + size2)
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


	public static float collisionPointRect(float px, float py, float rx, float ry, float width, float height, float rotation) {

		px -= rx;
		py -= ry;

		double rot = (Math.PI * 2 - rotation);
		float px_ = (float) (px * Math.cos(rot) - py * Math.sin(rot));
		float py_ = (float) (px * Math.sin(rot) + py * Math.cos(rot));

		if (px_ < -width / 2 || px_ > width / 2 || py_ < -height / 2 || py_ > height / 2)
				return 2;

		return px_ / width * 2;
	}
}