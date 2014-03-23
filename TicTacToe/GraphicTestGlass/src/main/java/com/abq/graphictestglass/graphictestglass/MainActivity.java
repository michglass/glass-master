package com.abq.graphictestglass.graphictestglass;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.google.android.glass.app.Card;

/**
 * Canvas: Holds "draw" calls
 * To draw sth 4 basic components are needed:
 * 1. Bitmap holding Pixels
 * 2. Canvas holding Draw Calls (writing into the bitmap)
 * 3. A drawing primitive (Rect, Path, text, Bitmap)
 * 4. Paint (Describes colors, styles for drawing)
 * Canvas.
 *  drawColor(int color)
 *  drawLine(float startX, float startY, float stopX, float stopY, Paint paint)
 *  drawPaint(Paint paint)
 *  drawPath(Path path, Paint paint)
 *  drawPicture(Picture picture, RectF dst)
 *
 */


public class MainActivity extends Activity {

    // Debug
    private static final String TAG = "Main Activity";

    private Card mCard;
    private GameSurface mGameSurface;
    private Handler mGameHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "On Create");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mGameSurface = new GameSurface(this);
        mGameSurface.updateGame();
        setContentView(mGameSurface);
    }
    @Override
    public void onResume() {
        Log.v(TAG, "On Resume");
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        Log.v(TAG, "On Destroy");
        super.onDestroy();

        mGameSurface.pauseGame();
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        Log.v(TAG, "On Key Down");

        if(keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            mGameSurface.makeMove(GameSurface.PLAYER_ID);
            return true;
        }
        return super.onKeyDown(keycode, event);
    }
}
