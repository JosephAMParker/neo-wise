package com.neowise.game.homeBase;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.neowise.game.util.RandomUtil;
import com.neowise.game.util.Settings;


public class PixMapController {

    private HomeBase homeBase;
    private Pixmap pixmap;
    public  Pixmap p1,p2,l,r,c; //bomb pixmaps: regular hit, lava hit, ring cutout, center hole.
    private Queue<PixmapHit> hitQueue = new Queue<>();
    private Queue<PixmapHit> healQueue = new Queue<>();

    private void updateQueue(Queue<PixmapHit> q, int c) {
        int count = 0;
        while (q.notEmpty() && count < c) {
            count += 1;
            q.removeFirst().doHit();
            homeBase.setCheckIntegrity();
        }
    }

    public void update() {
        updateQueue(hitQueue, 25);
        updateQueue(healQueue, 100);
    }

    public void doAllHits() {
        while (hitQueue.notEmpty()){
            PixmapHit ph = hitQueue.removeFirst();
            ph.doHit();
        }
        homeBase.setCheckIntegrity();
    }

    abstract class PixmapHit {
        abstract void doHit();
    }

    class HealthHitGWT extends PixmapHit {
        int x,y,bombRadius;
        int healAmount;
        HealthHitGWT(int x, int y, int bombRadius, int healAmount){
            this.x = (int)x;
            this.y = (int)y;
            this.bombRadius = bombRadius;
            this.healAmount = healAmount;
        }

        void doHit() {
            pixmap.setColor(healAmount);
            pixmap.fillCircle(x ,y, bombRadius);
        }
    }

    class HealthHit extends PixmapHit {

        int x,y,bombRadius;
        int healAmount;
        HealthHit(int x, int y, int bombRadius, int healAmount){
            this.x = (int)x;
            this.y = (int)y;
            this.bombRadius = bombRadius;
            this.healAmount = healAmount;
        }

        void doHit() {
            int a;
            for (int i = -bombRadius; i < bombRadius; i++) {
                for (int j = -bombRadius; j < bombRadius; j++) {
                    float distance = ((float) (i*i + j*j)) / (bombRadius*bombRadius);
                    if (distance < 1) {
                        a = pixmap.getPixel(x + i, y + j) & 0x000000ff;
                        a -= healAmount * (1-distance);
                        a = Math.max(a, 0);
                        pixmap.drawPixel(x + i, y + j, a);
                    }
                }
            }
        }
    }

    class BombHit extends PixmapHit {

        Pixmap p;
        int x,y,bombSize,holeSize;
        boolean clearCenter;

        BombHit(Pixmap p, float x, float y, int bombSize, int holeSize, boolean clearCenter){
            this.p = p;
            this.x = (int) x;
            this.y = (int) y;
            this.bombSize = bombSize;
            this.holeSize = holeSize;
            this.clearCenter = clearCenter;
        }

        void doHit(){
            pixmap.setBlending(Pixmap.Blending.SourceOver);
            pixmap.drawPixmap(p,0,0,p.getWidth(),p.getHeight(),(int) x - bombSize/2,(int) y - bombSize/2, bombSize,bombSize);
            pixmap.drawPixmap(p,0,0,p.getWidth(),p.getHeight(),(int) x - bombSize/2,(int) y - bombSize/2, bombSize,bombSize);
            if(clearCenter)
                pixmap.drawPixmap(c, 0, 0, c.getWidth(), c.getHeight(),(int) x - holeSize/2,(int) y- holeSize/2, holeSize,holeSize);
            else
                pixmap.drawPixmap(r, 0, 0, r.getWidth(), r.getHeight(),(int) x- holeSize/2,(int) y- holeSize/2, holeSize,holeSize);

            resetCore();
        }
    }

    class LavaHit extends PixmapHit {

        int x,y,lavaSize;

        LavaHit(float x, float y, int lavaSize){
            this.x = (int) x;
            this.y = (int) y;
            this.lavaSize = lavaSize;
        }

        void doHit(){
            pixmap.setBlending(Pixmap.Blending.SourceOver);
            pixmap.drawPixmap(l,0,0,l.getWidth(),l.getHeight(),x,y, lavaSize,lavaSize);
            resetCore();
        }
    }


    public void init(){
        generateBombPixmaps();
        initializeHomeBasePixmap(homeBase.size, (int) homeBase.core.radius);
    }

    public void resetHomeBasePixmap(){
        initializeHomeBasePixmap(homeBase.size, (int) homeBase.core.radius);
    }

    private void generateBombPixmaps(){
        int bombRadius = 100;

        l  = new Pixmap(bombRadius*2,bombRadius*2, Pixmap.Format.Alpha);
        p1 = new Pixmap(bombRadius*2,bombRadius*2, Pixmap.Format.Alpha);
        p2 = new Pixmap(bombRadius*2,bombRadius*2, Pixmap.Format.Alpha);
        r  = new Pixmap(bombRadius*2,bombRadius*2, Pixmap.Format.Alpha);
        c  = new Pixmap(bombRadius*2,bombRadius*2, Pixmap.Format.Alpha);

        for (int i = -bombRadius; i < bombRadius; i++) {
            for (int j = -bombRadius; j < bombRadius; j++) {
                float distance = ((float) (i * i + j * j)) / (bombRadius * bombRadius);
                if(distance < 1){

                    l.drawPixel(i + bombRadius, j + bombRadius, Color.alpha((1f-distance))/6);
                    c.drawPixel(i + bombRadius, j + bombRadius, Color.alpha((1f)));
                    p1.drawPixel(i + bombRadius, j + bombRadius, Color.alpha(Math.min(0.99f,Math.max(0,1f-distance))));

                    if(distance > 0.5f)
                        r.drawPixel(i + bombRadius, j + bombRadius, Color.alpha((1f)));
                    if(distance > 0.05f) {
                        distance = (1 - distance);
                        p2.drawPixel(i + bombRadius, j + bombRadius, Color.alpha(distance));
                    }

                }
            }
        }

    }

    private void initializeHomeBasePixmap(int size, int coreRadius) {
        pixmap = new Pixmap(size, size, Pixmap.Format.Intensity);
        pixmap.setBlending(Pixmap.Blending.SourceOver);
        setInitialPixmapCircle(size);
        randomizePixMapEdges();
        doAllHits();
    }

    private void randomizePixMapEdges(){

        Vector2 removePoint;
        Vector2 toCenter = new Vector2(homeBase.size/2, homeBase.size/2);
        for (int i = 0; i<360; i++){
            removePoint = new Vector2(0, homeBase.size/2 + 5);
            removePoint.rotateDeg(i);
            removePoint.add(toCenter);
            if(RandomUtil.nextInt(10) > 5) {
                removePointsBomb(p1, removePoint.x, removePoint.y, 50, 10, false);
            } else removePointsBomb(p1, removePoint.x, removePoint.y, 30, 30, true);
        }
    }

    public PixMapController(HomeBase homeBase){
        this.homeBase = homeBase;
    }

    public void removePointsLava(float x, float y, int lavaSize){
        hitQueue.addLast(new LavaHit(x,y,lavaSize));
    }

    public void removePointsBomb(Pixmap p, float x, float y, int bombSize, int holeSize, boolean clearCenter){
        hitQueue.addLast(new BombHit(p,x,y,bombSize,holeSize,clearCenter));
    }

    private void resetCore() {
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(Color.alpha(0));
        pixmap.fillCircle(homeBase.size/2,homeBase.size/2, homeBase.core.radius);
    }

    private void setInitialPixmapCircle(int size){

        pixmap.setColor(Color.alpha(1));
        pixmap.fill();

        pixmap.setBlending(Pixmap.Blending.None);

        pixmap.setColor(0);
        pixmap.fillCircle(size / 2, size / 2, size / 2);
        preChunk.size = size;
    }

    public Pixmap getPixmap() {
        return pixmap;
    }


    public void addHealthBombPoints(int x, int y, int bombRadius, int healAmount){
        if(Settings.isGWT)
            healQueue.addLast(new HealthHitGWT(x,y,bombRadius, healAmount));
        else
            healQueue.addLast(new HealthHit(x,y,bombRadius, healAmount));
    }
}
