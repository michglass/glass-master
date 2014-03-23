package com.abq.graphictestglass.graphictestglass;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oliver
 * Date 3/21/14.
 */
public class DrawingLogic {

    // Debug
    private static final String TAG = "Drawing Logic";

    // Game Variables
    private Canvas mCanvas; // Background on which Game field and objects are drawn
    private SurfaceHolder mSurfaceHolder; // manages Canvas
    private Paint mGridPaint; // Styles Playing Grid
    private Paint mRectPaint; // style rectangle paint
    private Rect mRectangle; // Rectangle indicates what cell is currently in focus

    // Thread Var
    private DrawGameThread mGameThread;
    private boolean mKeepUpdatingGame; // indicates if game keeps updating
    private AIThread mAIThread;
    private FillCellThread mFillCellThread;

    // Storing Coord
    private List<Pair<Integer, Integer>> aiValues = new ArrayList<Pair<Integer, Integer>>();
    private List<Pair<Integer, Integer>> playerValues = new ArrayList<Pair<Integer, Integer>>();
    private boolean GAME_OVER;
    // Ints
    private final int XGap = 213;
    private final int YGap = 120;

    // Game Matrix
    private char[][] mGameMatrix = new char[3][3];
    private final char PLAYER = 'P';
    private final char AI = 'A';
    private List<Pair<Integer, Integer>> mOpenCells;

    // class that holds coordinates of the symbols set in the game
    private class Pair<XVal,YVal> {
        private XVal l;
        private YVal r;
        public Pair(XVal l, YVal r){
            this.l = l;
            this.r = r;
        }
        public XVal getXVal(){ return l; }
        public YVal getYVal(){ return r; }
        public void setXVal(XVal l){ this.l = l; }
        public void setYVal(YVal r){ this.r = r; }
    }

    // Rectangle Variables
    private int xStart;
    private int xStop;
    private int yStart;
    private int yStop;
    private int currX;
    private int currY;
    // Field Dimensions (Dimensions of a Card)
    private final int FIELD_WIDTH = 640;
    private final int FIELD_HEIGHT = 360;
    // increment X
    private  void incrementX() {
        xStart += XGap;
        xStop += XGap;
        currX = xStop;
    }
    // reset X
    private  void resetX() {
        xStart = 0;
        xStop = XGap;
        currX = xStop;
    }
    // increment X
    private  void incrementY() {
        yStart += YGap;
        yStop += YGap;
        currY = yStop;
    }
    // reset Y
    private  void resetY() {
        yStart = 0;
        yStop = YGap;
        currY = yStop;
    }
    // get X
    public  int getCurrX() {
        Log.v(TAG, "Get X");
        return currX;
    }
    // get Y
    public  int getCurrY() {
        Log.v(TAG, "Get Y");
        return currY;
    }

    // Constructor
    public DrawingLogic(SurfaceHolder surfaceHolder,Paint gridPaint, Paint rectPaint, Rect rect) {
        Log.v(TAG, "Constructor");

        mSurfaceHolder = surfaceHolder;
        mRectangle = rect;
        mGridPaint = gridPaint;
        mRectPaint = rectPaint;
        mOpenCells = new ArrayList<Pair<Integer, Integer>>();
        for(int j=0; j<=240; j+=120) {
            for(int i=0; i<=426; i+=213) {
                Pair<Integer,Integer> pair = new Pair<Integer, Integer>(i,j);
                mOpenCells.add(pair);
            }
        }
        printOpenCells();
    }
    // start game thread
    public void startGameThread() {
        Log.v(TAG, "Start Game Thread");

        mKeepUpdatingGame = true;
        mGameThread = new DrawGameThread();
        mGameThread.start();
    }
    // pause Game Thread
    public void pauseGameThread() {
        Log.v(TAG, "Pause Game Thread");

        mKeepUpdatingGame = false;
        mGameThread = null;
    }
    // resume game thread
    public void resumeGameThread() {
        Log.v(TAG, "Resume Game Thread");
        mKeepUpdatingGame = true;
        mGameThread = new DrawGameThread();
        mGameThread.start();
    }
    // fill cell
    public void fillCell(int id, int x, int y) {
        Log.v(TAG, "Fill Cell");
        if(id == GameSurface.PLAYER_ID) {
            pauseGameThread();
        }
        if(id == GameSurface.PLAYER_ID) {
            mFillCellThread = new FillCellThread(id, getCurrX(), getCurrY());
            mFillCellThread.start();
        }
        else {
            mFillCellThread = new FillCellThread(id, x, y);
            mFillCellThread.start();
        }/*
        FillCellThread fillCell = new FillCellThread(id, getCurrX(), getCurrY());
        fillCell.start(); */
    }
    // draw game field
    public void drawField() {

        if(mCanvas != null) {
            Log.v(TAG, "Draw Game Field");
            mCanvas.drawColor(Color.DKGRAY);

            mCanvas.drawLine(mCanvas.getWidth() / 3, 0, mCanvas.getWidth() / 3,
                    mCanvas.getHeight(), mGridPaint);
            mCanvas.drawLine(mCanvas.getWidth() * 2 / 3, 0, mCanvas.getWidth() * 2 / 3,
                    mCanvas.getHeight(), mGridPaint);
            mCanvas.drawLine(0, mCanvas.getHeight() / 3, mCanvas.getWidth(),
                    mCanvas.getHeight() / 3, mGridPaint);
            mCanvas.drawLine(0, mCanvas.getHeight() * 2 / 3, mCanvas.getWidth(),
                    mCanvas.getHeight() * 2 / 3, mGridPaint);
        }
    }
    // move Rectangle
    private void drawRectangle(int xS, int yS, int xF, int yF) {
        mRectangle.set(xS, yS, xF, yF);
        if(mCanvas != null) {
            Log.v(TAG, "Draw Rectangle");
            Log.v(TAG, "xS:" + xS + ",xF:"+xF+"/yS:"+yS+",yF:"+yF);
            mCanvas.drawRect(mRectangle, mRectPaint);
        }
    }
    private void drawCircles(List<Pair<Integer, Integer>> coord) {
        Log.v(TAG, "Draw Circles");
        Paint cPaint = new Paint();
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setColor(Color.GREEN);
        cPaint.setStrokeWidth(8);
        for(Pair<Integer,Integer> p : coord) {
            mCanvas.drawCircle(p.getXVal(),p.getYVal(),20,cPaint);
        }
    }
    private void drawCrosses(List<Pair<Integer, Integer>> coord){
        Log.v(TAG, "Draw Crosses");
        for(Pair<Integer,Integer> p : coord) {
            mCanvas.drawCircle(p.getXVal(),p.getYVal(),20,new Paint());
        }
    }
    private void drawSymbols(List<Pair<Integer, Integer>> aiCoord,
                             List<Pair<Integer, Integer>> playerCoord) {

        drawCrosses(playerCoord);
        drawCircles(aiCoord);
    }
    public void aiMove() {
        mAIThread = new AIThread();
        mAIThread.start();
    }
    private void stopAIThread() {
        if(mAIThread != null) {
            mAIThread.setRunning(false);
            mAIThread.setThisRunning(false);
            mAIThread = null;
        }
    }
    private void stopFillCellThread() {
        if(mFillCellThread != null) {
            Log.v(TAG, "Stop Fill Cell");
            mFillCellThread.setFillThreadBool(false);
            mFillCellThread = null;
        }
    }
    private boolean cellFilled(int x, int y) {
        Log.v(TAG, "Compare Players");
        for(Pair playerPair : playerValues) {
            if(playerPair.getXVal().equals(x) && playerPair.getYVal().equals(y)) {
                Log.v(TAG, "cell filled!");
                return true;
            }
        }
        Log.v(TAG, "Compare AI");
        for(Pair aiPair : aiValues){
            if(aiPair.getXVal().equals(x) && aiPair.getYVal().equals(y)) {
                Log.v(TAG, "cell filled!");
                return true;
            }
        }
        return false;
    }
    private void printGameMatrix() {
        for(int i=0; i<3; i++) {
            Log.v(TAG, mGameMatrix[i][0] + ","+mGameMatrix[i][1]+","+mGameMatrix[i][2]);
        }
    }
    private boolean checkWin(int id) {

        for(int i=0; i<3; i++)
            if(checkRowWin(id, i))
                return true;
        for(int j=0; j<3; j++)
            if(checkColumnWin(id, j))
                return true;
        for(int c = 0; c < 2; c++)
            if(checkDiagWin(id, c))
                return true;
        return false;
    }
    private boolean checkRowWin(int id, int row) {
        char c;
        if (id == GameSurface.PLAYER_ID) c = PLAYER;
        else c = AI;

        for (int j = 0; j < 3; j++) {
            if (c != mGameMatrix[row][j])
                return false;
        }
        return true;
    }
    private boolean checkColumnWin(int id, int col) {
        char c;
        if (id == GameSurface.PLAYER_ID) c = PLAYER;
        else c = AI;

        for (int i = 0; i < 3; i++) {
            if (c != mGameMatrix[i][col])
                return false;
        }
        return true;
    }
    private boolean checkDiagWin(int id, int diag) {
        char c;
        if (id == GameSurface.PLAYER_ID) c = PLAYER;
        else c = AI;
        int row;
        int column;
        if(diag == 1) {
            row = 0;
            column = 0;
        } else {
            row = 0;
            column = 2;
        }

        for(int count=0; count<3; count++) {
            if(mGameMatrix[row][column] != c)
                return false;
            else {
                if(diag == 1) {
                    row++;
                    column++;
                } else {
                    row++;
                    column--;
                }
            }
        }
        return true;
    }
    private void addToMatrix(int xStart, int yStart, int id) {
        int row = 0;
        int column = 0;

        switch (xStart) {
            case 0:
                column = 0;
                break;
            case 213:
                column = 1;
                break;
            case 426:
                column = 2;
                break;
        }
        switch (yStart) {
            case 0:
                row = 0;
                break;
            case 120:
                row = 1;
                break;
            case 240:
                row = 2;
                break;
        }
        if(id == GameSurface.PLAYER_ID)
            mGameMatrix[row][column] = PLAYER;
        if(id == GameSurface.AI_ID)
            mGameMatrix[row][column] = AI;
    }
    public void deleteFromOpenCells(int x, int y) {
        Log.v(TAG, "Delete From Open Cells");
        for(int i=0; i<mOpenCells.size(); i++) {
            int xCurr = mOpenCells.get(i).getXVal();
            int yCurr = mOpenCells.get(i).getYVal();
            if(x == xCurr && y == yCurr)
                mOpenCells.remove(i);
        }
    }
    public void printOpenCells() {
        for(int i=0; i<mOpenCells.size(); i++) {
            Log.v(TAG, ""+mOpenCells.get(i).getXVal()+","+mOpenCells.get(i).getYVal()+"/");
        }
    }

    private class AIThread extends Thread {

        // Debug
        private final String TAG = "AI Thread";

        // coord
        private int xCoord;
        private int yCoord;

        // bool
        private boolean mKeepRunning;
        private boolean keepThisRunning;

        public AIThread() {
            xCoord = 107;
            yCoord = 60;
            mKeepRunning = true;
            keepThisRunning = true;
        }

        @Override
        public void run() {
            Log.v(TAG, "Run");

            if(keepThisRunning) {
                try {
                    sleep(1000);
                } catch (InterruptedException intE) {
                    Log.e(TAG, "Thread Interrupted");
                }
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (!cellFilled(xCoord, yCoord)) {
                            fillCell(GameSurface.AI_ID, xCoord + 106, yCoord + 60);
                        /*FillCellThread fillCellThread =
                                new FillCellThread(GameSurface.AI_ID, xCoord+106, yCoord+60);
                        fillCellThread.start();*/
                            try {
                                sleep(2000);
                            } catch (InterruptedException intE) {
                                Log.v(TAG, "Thread interrupted");
                            }
                            if (mKeepRunning) {
                                printGameMatrix();
                                resumeGameThread();
                            }
                            return;
                        }
                        xCoord += XGap;
                    }
                    xCoord = 107;
                    yCoord += YGap;
                }
                if (mKeepRunning)
                    resumeGameThread();
                Log.v(TAG, "Run Return");
            }
        }
        public void setRunning(boolean b) {
            this.mKeepRunning = b;
        }
        public void setThisRunning(boolean b) {
            this.keepThisRunning = b;
        }
    }
    /**
     * Fill Cell Thread
     * When player/AI makes a move this thread
     * fills a cell with the appropriate symbol
     */
    private class FillCellThread extends Thread {

        // Debug
        private static final String TAG = "Fill Cell Thread";

        // ID that determines who made the move
        private int mID;
        private boolean mKeepRunning;

        // coord for symbol
        private int xCent;
        private int yCent;

        /**
         * Constructor
         */
        public FillCellThread(int id, int x, int y) {
            xCent = x - 106;
            yCent = y - 60;
            mID = id;
            mKeepRunning = true;
        }

        @Override
        public void run() {
            Log.v(TAG, "Run");

            if(!GAME_OVER) {
                while (mKeepRunning) {

                    if (!mSurfaceHolder.getSurface().isValid())
                        continue;

                    mCanvas = mSurfaceHolder.lockCanvas();
                    drawField();
                    Log.v(TAG, "XCent: " + xCent + ", YCent: " + yCent);
                    if (mKeepRunning) {
                        if (mID == GameSurface.PLAYER_ID) {
                            playerValues.add(new Pair<Integer, Integer>(xCent, yCent));
                            drawSymbols(aiValues, playerValues);
                            addToMatrix(xCent - 107, yCent - 60, GameSurface.PLAYER_ID);
                            deleteFromOpenCells(xCent - 107, yCent - 60);
                            printOpenCells();

                            if (checkWin(GameSurface.PLAYER_ID)) {
                                GAME_OVER = true;
                                Log.v(TAG, "Player wins");
                                Paint p = new Paint();
                                p.setTextSize(100f);
                                p.setStyle(Paint.Style.STROKE);
                                p.setStrokeWidth(5);
                                p.setColor(Color.RED);
                                mCanvas.drawText("Grace Wins!", 100, FIELD_HEIGHT / 2, p);
                                pauseGameThread();
                                stopAIThread();
                                stopFillCellThread();
                            }
                        }
                    }
                    if (mKeepRunning) {
                        if (mID == GameSurface.AI_ID) {
                            aiValues.add(new Pair<Integer, Integer>(xCent, yCent));
                            //drawRectangle(xCent - 107, yCent - 60, xCent + 106, yCent + 60);
                            drawSymbols(aiValues, playerValues);
                            addToMatrix(xCent - 107, yCent - 60, GameSurface.AI_ID);
                            deleteFromOpenCells(xCent - 107, yCent - 60);
                            printOpenCells();

                            if (checkWin(GameSurface.AI_ID)) {
                                GAME_OVER = true;
                                Log.v(TAG, "AI Wins");
                                Paint p = new Paint();
                                p.setTextSize(100f);
                                p.setStyle(Paint.Style.STROKE);
                                p.setStrokeWidth(5);
                                p.setColor(Color.RED);
                                mCanvas.drawText("Glass Wins!", 100, FIELD_HEIGHT / 2, p);
                                pauseGameThread();
                                stopAIThread();
                                stopFillCellThread();
                            }
                        }
                    }
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);

                    mKeepRunning = false;
                }
            }
            Log.v(TAG, "Run Return");
        }
        public void setFillThreadBool(boolean b) {
            this.mKeepRunning = b;
        }
    }
    /**
     * Draw Game Thread
     * Updates the game field every n - seconds
     * Moves the Rectangle one Cell further
     */
    private class DrawGameThread extends Thread {

        // Debug
        private static final String TAG = "Draw Game Thread";

        /**
         * Constructor
         */
        public DrawGameThread() {
            GAME_OVER = false;
            Log.v(TAG, "Constructor");
        }

        @Override
        public void run() {
            Log.v(TAG, "Run");
            int x;
            int y;
            int count = 0;
            while (mKeepUpdatingGame) {

                if(!mSurfaceHolder.getSurface().isValid())
                    continue;

                mCanvas = mSurfaceHolder.lockCanvas();

                // better current pos, not reset
                //resetX();
                //resetY();

                if(mKeepUpdatingGame) {
                    if(mOpenCells.size() == 0) {
                        stopAIThread();
                        stopFillCellThread();
                        pauseGameThread();
                    } else {
                        x = mOpenCells.get(count).getXVal();
                        y = mOpenCells.get(count).getYVal();
                        currX = x + 213;
                        currY = y + 120;
                        drawField();
                        drawSymbols(aiValues, playerValues);
                        drawRectangle(x, y, x + 213, y + 120);
                    }
                } else {break;}
                if(mCanvas != null && mKeepUpdatingGame) {
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    try {
                        Log.v(TAG, "Delay Game");
                        sleep(3000);
                    } catch (InterruptedException intE) {
                        Log.e(TAG, "Game Thread interrupted");
                    }
                } else {break;}
/*
                for(int i=0; i<3; i++) {
                    for(int j=0; j<3; j++) {
                        if(mKeepUpdatingGame) {
                            mCanvas = mSurfaceHolder.lockCanvas();
                            drawField();
                            drawSymbols(aiValues, playerValues);
                            drawRectangle(xStart, yStart, xStop, yStop);
                        }
                        if(mCanvas != null && mKeepUpdatingGame) {
                            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                            try {
                                Log.v(TAG, "Delay Game");
                                sleep(3000);
                            } catch (InterruptedException intE) {
                                Log.e(TAG, "Game Thread interrupted");
                            }
                            incrementX();
                        } else {break;}
                    }
                    if(mCanvas != null && mKeepUpdatingGame) {
                        resetX();
                        incrementY();
                    } else {break;}
                }
                */
                if(count == mOpenCells.size()-1)
                    count = 0;
                else
                    count++;
            }

            Log.v(TAG, "Run Return");
        }
    }
}
