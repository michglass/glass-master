package com.abq.graphictestglass.graphictestglass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Oliver
 * Date 3/21/14.
 */
public class GameSurface extends SurfaceView {

    // Debug
    private static final String TAG = "Game Surface";

    // Game Surface Variables
    private SurfaceHolder mSurfaceHolder; // Holds and manages/modifies game surface
    private Canvas mCanvas; // Background for Game Grid
    private Paint mGridPaint; // Paint for setting styles for Grid
    private Paint mRectPaint; // Paint for setting styles for Rectangle
    private Rect mRectangle; // Rectangle that indicates which cell is currently in focus

    // Thread Variables
    private DrawingLogic mDrawingLogic;

    // Player Variables
    public static final int PLAYER_ID = 1;
    public static final int AI_ID = 2;

    /**
     * Constructor
     */
    public GameSurface(Context context) {
        super(context);
        Log.v(TAG, "Constructor");

        // Initiate Game Field Variables
        mSurfaceHolder = getHolder();
        mGridPaint = initPaint(Color.BLACK, Paint.Style.STROKE);
        mRectPaint = initPaint(Color.BLUE, Paint.Style.STROKE);
        mRectangle = new Rect();

        mDrawingLogic = new DrawingLogic(mSurfaceHolder, mGridPaint, mRectPaint, mRectangle);
    }

    // Start updating the Game field
    public void updateGame() {
        Log.v(TAG, "Update Game");
        mDrawingLogic.startGameThread();
    }
    // Pause the Game
    public void pauseGame() {
        Log.v(TAG, "Pause Game");
        mDrawingLogic.pauseGameThread();
    }
    // make a move
    public void makeMove(int id) {
        Log.v(TAG, "Make Move");
        mDrawingLogic.fillCell(id,0,0);

        mDrawingLogic.aiMove();
    }
    // init paint
    private Paint initPaint(int c, Paint.Style s) {
        Paint p = new Paint();
        p.setColor(c);
        p.setStyle(s);
        p.setStrokeWidth(8);
        return p;
    }
}
