package com.neowise.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.main.GameLevelObject;
import com.neowise.game.main.LevelInfo;
import com.neowise.game.main.ScorchedEarthGame;
import com.neowise.game.physics.CollisionDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class StarMap extends GameLevelObject implements Screen, InputProcessor {

    public boolean reset = false;

    private StarMapNode[][] starMap;
    private StarMapNode[] playerPath;
    private int mapWidth;
    private int numberOfLevels;
    private int currentLevel;
    private int maxReach;
    private StarMapNode currentNode;
    private Random random;

    public Color bgColor = new Color(0.01f,0.01f,0.02f,1);


    public StarMap(final ScorchedEarthGame game) {

        super(game);

        numberOfLevels = 12;
        mapWidth = 12;
        currentLevel = 0;
        maxReach = 2;
        currentNode = null;
        random = new Random();
        playerPath = new StarMapNode[numberOfLevels];

        initMap();

    }

    private void goToSelectedLevel(StarMapNode node){
        BasicLevel level = new BasicLevel(game);
        game.setScreen(level);
        level.setInput();
    }

    private void makeChildrenPlayable(StarMapNode node){
        for(StarMapNode[] row : starMap){
            for(StarMapNode ch : row){
                if(ch != null)
                    ch.playable = false;
            }
        }
        for(StarMapNode ch : node.children){
            ch.playable = true;
        }
    }

    private void drawMap(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.identity();
        //draw circles
        for(int r = 0; r<starMap.length;r++) {
            StarMapNode[] row = starMap[r];
            for (int c = 0; c < row.length; c++) {
                StarMapNode sNode = row[c];
                if (sNode != null) {
                    shapeRenderer.setColor(Color.GRAY);
                    if(sNode.playable)
                        shapeRenderer.setColor(Color.CYAN);
                    if(sNode.selected)
                        shapeRenderer.setColor(Color.GOLDENROD);
                    if(sNode.finished)
                        shapeRenderer.setColor(Color.FOREST);

                    shapeRenderer.circle(sNode.xPos, sNode.yPos, sNode.circleRadius);
                }
            }
        }

        for(int r = 0; r<starMap.length;r++) {
            StarMapNode[] row = starMap[r];
            for (int c = 0; c < row.length; c++) {
                StarMapNode sNode = row[c];
                if (sNode != null) {
                    for(int sc=0;sc<sNode.children.size();sc++){
                        StarMapNode chNode = sNode.children.get(sc);
                        shapeRenderer.setColor(Color.GRAY);
                        if(sNode.finished && chNode.playable)
                            shapeRenderer.setColor(Color.CYAN);
                        if(sNode.finished && chNode.finished)
                            shapeRenderer.setColor(Color.FOREST);
                        shapeRenderer.line(sNode.xPos, sNode.yPos, chNode.xPos, chNode.yPos);
                    }
                }
            }
        }
        shapeRenderer.end();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //checkForLevelSelect();
        drawMap();

        if(reset) {
            reset = false;
            initMap();
        }

    }

    /**
     * @return -1, 0 or 1
     */
    private int randomIncrement(){
        return random.nextInt(3) - 1;
    }

    /**
     * @param node1
     * @param node2
     * @return true if node1 and node2 exist and node1 has node2 as a child
     */
    private boolean hasChild(StarMapNode node1, StarMapNode node2){

       if(node1 == null || node2 == null)
           return false;

       return node1.children.contains(node2);
    }

    /**
     * @return true if node b from bottomNodes can create a branch to node t from topNodes without crossing
     */
    private boolean isReachable(StarMapNode[] bottomNodes, StarMapNode[] topNodes, int b, int t){

        if(topNodes[t] == null || bottomNodes[b] == null)
            return false;

        if(hasChild(bottomNodes[b], topNodes[t])) return false;

        if(Math.abs(t-b)>maxReach) return false;

        for(int j=0; j<b; j++){
            for(int k=t+1; k<topNodes.length; k++){
                if(hasChild(bottomNodes[j], topNodes[k]))
                    return false;
            }
        }

        for(int j=b+1; j<bottomNodes.length; j++){
            for(int k=0; k<t; k++){
                if(hasChild(bottomNodes[j], topNodes[k]))
                    return false;
            }
        }

        return true;
    }

    private ArrayList<Integer> createPlacesArray(int size){
        ArrayList<Integer> places = new ArrayList<Integer>();
        for(int j=0; j<size; j++){
            places.add(j);
        }
        return places;
    }

    private Integer searchLeft(StarMapNode[] bottomNodes, StarMapNode[] topNodes, int startIndex) {

        for(int i=startIndex-1; i>=0; i--) {
            if (isReachable(bottomNodes, topNodes, startIndex, i))
                return i;
        }

        return -1;
    }

    private Integer searchRight(StarMapNode[] bottomNodes, StarMapNode[] topNodes, int startIndex) {

        for(int i=startIndex+1; i<topNodes.length; i++) {
            if (isReachable(bottomNodes, topNodes, startIndex, i))
                return i;
        }

        return -1;
    }

    /**
     *
     * @param dir
     * @param topNodes
     * @return
     */
    private Integer getRandomTopNodeIndex(int dir, int bottomIndex, StarMapNode[] bottomNodes, StarMapNode[] topNodes){

        switch (dir) {
            case 0 : {
                if(isReachable(bottomNodes,topNodes,bottomIndex, bottomIndex)) return bottomIndex;
                if(random.nextInt(1) == 1) {
                    int l = searchLeft(bottomNodes, topNodes, bottomIndex);
                    if (l >= 0) return l;
                    int r = searchRight(bottomNodes, topNodes, bottomIndex);
                    if (r >= 0) return r;
                } else {
                    int r = searchRight(bottomNodes, topNodes, bottomIndex);
                    if (r >= 0) return r;
                    int l = searchLeft(bottomNodes, topNodes, bottomIndex);
                    if (l >= 0) return l;
                }
            }
            case 1 : {
                int r = searchRight(bottomNodes, topNodes, bottomIndex);
                if (r >= 0) return r;
                if(isReachable(bottomNodes,topNodes,bottomIndex, bottomIndex)) return bottomIndex;
                int l = searchLeft(bottomNodes, topNodes, bottomIndex);
                if (l >= 0) return l;
            }
            case -1 : {
                int l = searchLeft(bottomNodes, topNodes, bottomIndex);
                if (l >= 0) return l;
                if(isReachable(bottomNodes,topNodes,bottomIndex, bottomIndex)) return bottomIndex;
                int r = searchRight(bottomNodes, topNodes, bottomIndex);
                if (r >= 0) return r;
            }
        }
        return -1;
    }

    private boolean hasCloseNode(StarMapNode[] topNodes, int index){
        for(int i = Math.max(0,index-maxReach); i<Math.min(topNodes.length, index+maxReach);i++){
            if(topNodes[i] != null) return true;
        }
        return false;
    }

    private void initMap() {

        StarMapNode[] prevNodes = new StarMapNode[mapWidth];
        StarMapNode[] nextNodes = new StarMapNode[mapWidth];

        starMap = new StarMapNode[numberOfLevels][mapWidth];

        ArrayList<Integer> bottomNodeIndexs = new ArrayList<Integer>();

        int numberOfNodes = random.nextInt(4) + 3;
        int prevNumberOfNodes = 0;
        int numberOfExtraBranches = 0;

        ArrayList<Integer> places = createPlacesArray(mapWidth);

        //set up 'numberOfNodes' # of star nodes in prevNodes array;
        for(int j=0; j<numberOfNodes; j++){
            int pos = random.nextInt(places.size());
            int index = places.remove(pos);
            StarMapNode sNode = new StarMapNode(0, index,true);
            sNode.playable = true;
            prevNodes[index] = sNode;
            bottomNodeIndexs.add(index);
        }

        starMap[0] = prevNodes;

        for(int i=1; i<numberOfLevels-1; i++){
            nextNodes = new StarMapNode[mapWidth];
            prevNumberOfNodes = 0;
            bottomNodeIndexs = new ArrayList<Integer>();

            prevNodes = starMap[i-1];
            for(int j=0;j<prevNodes.length;j++){
                if(prevNodes[j] != null){
                    prevNumberOfNodes += 1;
                    bottomNodeIndexs.add(j);
                }
            }

            numberOfNodes = Math.min(Math.max(3, prevNumberOfNodes + randomIncrement()), mapWidth/2);
            numberOfExtraBranches = Math.abs(numberOfNodes - prevNumberOfNodes) + random.nextInt(3);
            ArrayList<Integer> topNodeIndexs = new ArrayList<Integer>();
            ArrayList<Integer> bottomNodeIndexsFirstRun = new ArrayList<Integer>();



            //set up 'numberOfNodes' # of star nodes in nextNodes array;
            places = createPlacesArray(mapWidth);
            Collections.shuffle(bottomNodeIndexs);
            for(int j=0; j<bottomNodeIndexs.size(); j++){
                int index = bottomNodeIndexs.get(j);
                if(!hasCloseNode(nextNodes, index)){
                    int randIndex = index + randomIncrement() + randomIncrement();
                    randIndex = Math.min(mapWidth-1, randIndex);
                    randIndex = Math.max(0, randIndex);
                    nextNodes[randIndex] = new StarMapNode(i, randIndex);
//                  places.remove(places.get(index));
                    numberOfNodes--;
                }
            }

            for(int j=0; j<numberOfNodes; j++){
                int pos = random.nextInt(places.size());
                int index = places.remove(pos);
                nextNodes[index] = new StarMapNode(i,index);
            }

            //select a random node from prevNodes, connect to node either above, left, or right.
            bottomNodeIndexsFirstRun = (ArrayList<Integer>) bottomNodeIndexs.clone();
            for(int j=0; j<prevNumberOfNodes; j++){
                int pos = random.nextInt(bottomNodeIndexsFirstRun.size());
                int bottomIndex = bottomNodeIndexsFirstRun.remove(pos);

                int dir = randomIncrement(); // -1 left, 0 up, 1 right
                int topIndex = getRandomTopNodeIndex(dir, bottomIndex, prevNodes, nextNodes);

                if(topIndex < 0 || bottomIndex < 0) continue;


                StarMapNode bottomNode = prevNodes[bottomIndex];
                StarMapNode topNode    = nextNodes[topIndex];

                bottomNode.isConnected = true;
                topNode.isConnected    = true;

                bottomNode.children.add(topNode);
            }

            for(int j=0; j<numberOfExtraBranches; j++){
                int pos = random.nextInt(bottomNodeIndexs.size());
                int bottomIndex = bottomNodeIndexs.get(pos);

                int dir = randomIncrement(); // -1 left, 0 up, 1 right
                int topIndex = getRandomTopNodeIndex(dir, bottomIndex, prevNodes, nextNodes);

                if(topIndex < 0 || bottomIndex < 0) continue;

                StarMapNode bottomNode = prevNodes[bottomIndex];
                StarMapNode topNode    = nextNodes[topIndex];



                bottomNode.isConnected = true;
                topNode.isConnected    = true;

                bottomNode.children.add(topNode);
            }

            for(int j=0; j<nextNodes.length; j++){
                if(nextNodes[j] != null && !nextNodes[j].isConnected) {
                    for(int k=0; k<bottomNodeIndexs.size();k++){
                        if(isReachable(prevNodes, nextNodes, bottomNodeIndexs.get(k),j)){
                            prevNodes[bottomNodeIndexs.get(k)].isConnected = true;
                            nextNodes[j].isConnected = true;
                            prevNodes[bottomNodeIndexs.get(k)].children.add(nextNodes[j]);

                        }
                    }
                }
            }

            for(int j=0; j<nextNodes.length; j++){
                if(nextNodes[j] != null && !nextNodes[j].isConnected) {
                    numberOfNodes -= 1;
                    nextNodes[j] = null;
                    //topNodeIndexs.remove(topNodeIndexs.indexOf(j));
                }
            }

            starMap[i] = nextNodes;

        }
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {

        if( keycode == Input.Keys.SPACE){
            reset = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        Vector3 vec = new Vector3(screenX,screenY,0);
        camera.unproject(vec);
        screenX = (int) vec.x;
        screenY = (int) vec.y;

        for(int r=0;r<starMap.length;r++){
            StarMapNode[] row = starMap[r];
            for(int c=0;c<starMap.length;c++){
                StarMapNode node = row[c];
                if(node == null) continue;
                node.selected = false;
                if(node.playable && CollisionDetector.collisionCirclePoint(node.xPos, node.yPos,node.circleRadius+5,screenX,screenY)){
//                    playerPath[currentLevel] = node;
                    currentNode = node;
                    //node.selected = true;
                    goToSelectedLevel(node);
                }
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public LevelInfo getCurrentLevelInfo() {
        return currentNode.levelInfo;
    }

    public void endLevel() {
        currentNode.finished = true;
        makeChildrenPlayable(currentNode);
    }

    private class StarMapNode {

        public int level;
        public int col;
        public boolean isConnected;
        public boolean selected;
        public boolean finished;
        public boolean playable;
        public float circleRadius;
        public float xPos, yPos;
        public ArrayList<StarMapNode> children;
        public LevelInfo levelInfo;

        public void initStarMapNode(int level, int col){

            this.level  = level;
            this.col    = col;
            children = new ArrayList<>();
            circleRadius = random.nextFloat()*3+6;
            xPos = col*40 + 34 + random.nextFloat()*12;
            yPos = h/2 - 150 + level * 54 + random.nextFloat()*10;
            selected = false;
            playable = false;
            finished = false;

            levelInfo = new LevelInfo(level);

        }

        public StarMapNode(int level, int col) {
            this.isConnected = false;
            initStarMapNode(level, col);
        }

        public StarMapNode(int level, int col, boolean isConnected) {
            this.isConnected = isConnected;
            initStarMapNode(level, col);
        }

    }
}
