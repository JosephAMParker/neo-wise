package com.neowise.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.neowise.game.LevelInfo.CityDefenderLevelInfo;
import com.neowise.game.LevelInfo.LevelInfo;
import com.neowise.game.LevelInfo.SpaceInvadersLevelInfo;
import com.neowise.game.draw.DrawingBoard;
import com.neowise.game.main.BasicLevel;
import com.neowise.game.main.GameLevelObject;
import com.neowise.game.main.NeoWiseGame;
import com.neowise.game.physics.CollisionDetector;
import com.neowise.game.util.Constants;
import com.neowise.game.util.RandomUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class StarMap extends GameLevelObject implements InputProcessor {

    public boolean reset = false, hasCamPos = false;

    private StarMapNode[][] starMap;
    private StarMapNode[] playerPath;
    private int mapWidth;
    private int numberOfLevels;
    private int currentLevel;
    private int maxReach;
    private StarMapNode currentNode;
    private float nodeHeight;
    public float planetRadius, dashWidth;
    private boolean introCameraPan;
    public float cameraPanSpeed, camYPos;
    private boolean started;
    private Sprite earth;
    public float earthPos;

    public Color bgColor = new Color(0.01f,0.01f,0.02f,1);

    public StarMap(final NeoWiseGame game) {

        super(game);

        numberOfLevels = 9;
        mapWidth = 7;
        currentLevel = 0;
        maxReach = 2;
        currentNode = null;
        playerPath = new StarMapNode[numberOfLevels];
        planetRadius = w;
        introCameraPan = true;
        cameraPanSpeed = 0;
        dashWidth= 24;

        earth = DrawingBoard.createSprite("earth");
    }

    @Override
    public void initializeCamera() {
        super.initializeCamera();
        if(hasCamPos)
            camera.position.y = camYPos;
        else
            camera.position.y = numberOfLevels * nodeHeight + h + planetRadius / 2;
    }

    @Override
    public void show() {
        super.show();
        initializeCamera();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        resetCamera();
        initializeCamera();
    }

    @Override
    public void updateCamera() {
        super.updateCamera();
        hasCamPos = true;
        camYPos = camera.position.y;
    }

    private void goToSelectedLevel(StarMapNode node){
        BasicLevel level = new BasicLevel(game);
        node.levelInfo.setBasicLevel(level);
        game.setScreen(level);
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

        //draw connecting lines
        for(int r = 0; r<starMap.length;r++) {
            StarMapNode[] row = starMap[r];
            for (int c = 0; c < row.length; c++) {
                StarMapNode sNode = row[c];
                if (sNode != null) {
                    for(int sc=0;sc<sNode.children.size();sc++){
                        StarMapNode chNode = sNode.children.get(sc);

                        if(sNode.finished && chNode.playable)
                            drawDashes(sNode, chNode, "playableDash");
                        else if(sNode.finished && chNode.finished)
                            drawLine(sNode, chNode, "finishedDash");
                        else
                            drawDashes(sNode, chNode, "lockedDash");
                    }
                }
            }
        }

        earth.setBounds(-planetRadius/2 , earthPos , planetRadius * 2, planetRadius * 2);
        DrawingBoard.addSprite(earth, DrawingBoard.getSprites());
        drawingBoard.draw();

        shapeRenderer.end();
    }

    private void drawLine(StarMapNode sNode, StarMapNode chNode, String dashName) {

        float distance = chNode.pos.dst(sNode.pos) - sNode.circleRadius - chNode.circleRadius;
        Vector2 toNode = chNode.pos.cpy().sub(sNode.pos).nor();
        Vector2 dashPos = sNode.pos.cpy();
        dashPos.add(toNode.cpy().setLength(sNode.circleRadius + (distance)/2));

        Sprite dash = DrawingBoard.createSprite(dashName);

        dash.setSize(distance, 8);
        dash.setOriginCenter();
        dash.setRotation(toNode.angleDeg());
        dash.setCenter(dashPos.x, dashPos.y);

        drawingBoard.addSprite(dash, DrawingBoard.getSprites());

    }

    private void drawDashes(StarMapNode sNode, StarMapNode chNode, String dashName){

        float distance = chNode.pos.dst(sNode.pos) - sNode.circleRadius - chNode.circleRadius;
        int numOfDashes = (int) (distance/dashWidth);
        float excess = (distance - numOfDashes * dashWidth)/numOfDashes;
        Vector2 toNode = chNode.pos.cpy().sub(sNode.pos).nor().setLength(dashWidth + excess);
        Vector2 dashPos = sNode.pos.cpy();
        dashPos.add(toNode.cpy().setLength(sNode.circleRadius + (dashWidth + excess)/2));

        for(int i = 0; i < numOfDashes; i++){

            Sprite dash = DrawingBoard.createSprite(dashName);
            dash.setSize(dashWidth + excess, 8);
            dash.setOriginCenter();
            dash.setRotation(toNode.angleDeg());
            dash.setCenter(dashPos.x, dashPos.y);
            dashPos.add(toNode);

            drawingBoard.addSprite(dash, DrawingBoard.getSprites());
        }
    }

    @Override
    public void render(float delta) {

        if(game.getScreen() == this) {
            Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        updateCamera();
        updateTimers(delta);
        drawMap();

        if(reset) {
            reset = false;
            initMap();
        }

        if(introCameraPan){
            panCamera();
        }

    }

    private void updateTimers(float delta){
        if(started)
            totalTime += delta;
    }

    private void panCamera(){
        if(started) {
            camera.translate(0, cameraPanSpeed, 0);
            cameraPanSpeed -= 0.05;
            if (camera.position.y <= 200) {
                introCameraPan = false;
                cameraPanSpeed = 0;
            }
        }
    }

    /**
     * @return -1, 0 or 1
     */
    private int randomIncrement(){
        return RandomUtil.nextInt(3) - 1;
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
                if(RandomUtil.nextInt(1) == 1) {
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

    public void initMap() {

        StarMapNode[] prevNodes = new StarMapNode[mapWidth];
        StarMapNode[] nextNodes;

        starMap = new StarMapNode[numberOfLevels][mapWidth];

        ArrayList<Integer> bottomNodeIndexs = new ArrayList<Integer>();

        int numberOfNodes = RandomUtil.nextInt(4) + 3;
        int prevNumberOfNodes = 0;
        int numberOfExtraBranches = 0;

        nodeHeight = h/6;
        earthPos = numberOfLevels * nodeHeight;

        ArrayList<Integer> places = createPlacesArray(mapWidth);

        //set up 'numberOfNodes' # of star nodes in prevNodes array;
        for(int j=0; j<numberOfNodes; j++){
            int pos = RandomUtil.nextInt(places.size());
            int index = places.remove(pos);
            StarMapNode sNode = new StarMapNode(0, index, Constants.GAME_MODE.SPACE_INVADERS, true);
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
            numberOfExtraBranches = Math.abs(numberOfNodes - prevNumberOfNodes) + RandomUtil.nextInt(3);
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
                    nextNodes[randIndex] = new StarMapNode(i, randIndex, Constants.GAME_MODE.SPACE_INVADERS);
//                  places.remove(places.get(index));
                    numberOfNodes--;
                }
            }

            for(int j=0; j<numberOfNodes; j++){
                int pos = RandomUtil.nextInt(places.size());
                int index = places.remove(pos);
                nextNodes[index] = new StarMapNode(i,index, Constants.GAME_MODE.SPACE_INVADERS);
            }

            //select a random node from prevNodes, connect to node either above, left, or right.
            bottomNodeIndexsFirstRun = (ArrayList<Integer>) bottomNodeIndexs.clone();
            for(int j=0; j<prevNumberOfNodes; j++){
                int pos = RandomUtil.nextInt(bottomNodeIndexsFirstRun.size());
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
                int pos = RandomUtil.nextInt(bottomNodeIndexs.size());
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
        if ( keycode == Input.Keys.F){
            toggleFullScreen();
        }
        if ( keycode == Input.Keys.ESCAPE){
            Gdx.graphics.setWindowedMode(500,800);
            initializeCamera();
            updateCamera();
        }
        if ( keycode == Input.Keys.Z ){
            camera.zoom += 0.05;
            updateCamera();
        }
        if ( keycode == Input.Keys.X ){
            camera.zoom -= 0.05;
            updateCamera();
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
            for(int c=0;c<row.length;c++){
                StarMapNode node = row[c];
                if(node == null) continue;
                node.selected = false;
                if(node.playable && CollisionDetector.collisionCirclePoint(node.xPos, node.yPos,node.circleRadius+5,screenX,screenY)){
                    currentNode = node;
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

        float y = Gdx.input.getDeltaY() * 1.5f;
        camera.translate(0,y);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.position.add(0,amountY * -25,0);
        return false;
    }

    public LevelInfo getCurrentLevelInfo() {
        return currentNode.levelInfo;
    }

    public void endLevel() {
        currentNode.finished = true;
        makeChildrenPlayable(currentNode);
    }

    public void start() {
        started = true;
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
        public Vector2 pos;
        public ArrayList<StarMapNode> children;
        public LevelInfo levelInfo;

        public void initStarMapNode(int level, int col, Constants.GAME_MODE gameMode){

            float colWidth = (w - 10) / mapWidth;

            this.level  = level + 1;
            this.col    = col;
            children = new ArrayList<>();
            circleRadius = colWidth / 2 - RandomUtil.nextFloat() * colWidth / 8;

            xPos = col*colWidth + circleRadius + 5 + RandomUtil.nextInt(10);
            yPos = level * nodeHeight + RandomUtil.nextInt(20) - 10;

            pos = new Vector2(xPos, yPos);

            selected = false;
            playable = false;
            finished = false;

            switch (gameMode) {
                case CITY_DEFENDER : levelInfo = new CityDefenderLevelInfo(this.level); break;
                case SPACE_INVADERS: levelInfo = new SpaceInvadersLevelInfo(this.level); break;
            }

        }

        public StarMapNode(int level, int col, Constants.GAME_MODE gameMode) {
            this.isConnected = false;
            initStarMapNode(level, col, gameMode);
        }

        public StarMapNode(int level, int col, Constants.GAME_MODE gameMode, boolean isConnected) {
            this.isConnected = isConnected;
            initStarMapNode(level, col, gameMode);
        }

    }
}
