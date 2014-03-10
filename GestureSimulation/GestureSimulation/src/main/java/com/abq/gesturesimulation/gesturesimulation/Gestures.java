package com.abq.gesturesimulation.gesturesimulation;

import android.app.Instrumentation;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by Oliver
 * Date: 02/03/2014
 *
 * Gestures
 * Simulate Glass gestures (Tap, Swipe) with this class
 */
public class Gestures {

    // Debug
    private static final String TAG = "Gestures";

    // Types of Gestures
    public static final int TYPE_TAP = 1;
    public static final int TYPE_SWIPE_RIGHT = 2;
    public static final int TYPE_SWIPE_LEFT = 3;
    public static final int TYPE_SWIPE_DOWN = 4;

    // selected type
    private int GESTURE_TYPE;

    // Thread that injects a motion event
    InstThread instrThread;

    // Variable for managing the swipe loop
    public static int mCurrPosition;
    public static int mFinalPosition;
    public static boolean mKeepRunning = true; // indicates if the swipe loop should stop
    private Handler mHandler = new Handler();


    /**
     * Create Gesture
     * Simulate a Gesture (Tap, Swipe left right down)
     * @param gestureType Type of gesture we want to simulate
     */
    public void createGesture(int gestureType) {
        GESTURE_TYPE = gestureType;
        instrThread = new InstThread(new Instrumentation());
        instrThread.start();
    }
    /**
     * Swipe Loop
     * Runnable for swiping through a card scroll view
     * @param size Size of the CardScrollView
     */
    public Runnable swipeLoop(int size) {
        mCurrPosition = 0;
        mFinalPosition = size - 1;

        return new Runnable() {
            boolean swipeRight = true;
            boolean swipeLeft = false;
            @Override
            public void run() {
                Log.v(TAG, "Runnable Run");
                Log.v(TAG, "keep runn: " + mKeepRunning);
                if(mKeepRunning) {

                    Log.v(TAG, "Swipe Loop");
                    if(swipeRight) {
                        createGesture(Gestures.TYPE_SWIPE_RIGHT);
                        mCurrPosition++;
                    } else if(swipeLeft) {
                        createGesture(Gestures.TYPE_SWIPE_LEFT);
                        mCurrPosition--;
                    }

                    if(mCurrPosition == 0) {
                        swipeRight = true;
                        swipeLeft = false;
                    }
                    if(mCurrPosition == mFinalPosition) {
                        swipeLeft = true;
                        swipeRight = false;
                    }

                    mHandler.postDelayed(this, 3000);
                }
                mKeepRunning = true;
            }
        };
    }

    /**
     * Methods that simulate the different gestures
     */

    /**
     * Send Tap
     * Simulates a Tap Event on Glass
     */
    private void sendTap(float x, float y) {

        // inject the down event
        MotionEvent down = getEvent(x, y, MotionEvent.ACTION_DOWN);
        down.setSource(InputDevice.SOURCE_TOUCHPAD);
        instrThread.sendEvent(down);

        // inject the up event
        MotionEvent up = getEvent(x, y, MotionEvent.ACTION_UP);
        up.setSource(InputDevice.SOURCE_TOUCHPAD);
        instrThread.sendEvent(up);
    }
    /**
     * Send Swipe Left
     * Simulates a left swipe on Glass
     */
    private void sendSwipeLeft(float x1, float x2) {
        sendSwipe(x1, x2);
    }
    /**
     * Send Swipe Right
     * Simulates a right swipe on Glass
     */
    private void sendSwipeRight(float x1, float x2) {
        sendSwipe(x1, x2);
    }
    /**
     * Send Swipe Down
     * @param y1 Start Position of Swipe
     * @param y2 End Position of Swipe
     */
    private void sendSwipeDown(float y1, float y2) {

        // get and inject down event
        MotionEvent downEvent = getEvent(600f, y1, MotionEvent.ACTION_DOWN);
        downEvent.setSource(InputDevice.SOURCE_TOUCHPAD);
        instrThread.sendEvent(downEvent);

        while (y1 <= y2) {

            // get and inject move event
            MotionEvent moveEvent = getEvent(600f, y1, MotionEvent.ACTION_MOVE);
            moveEvent.setSource(InputDevice.SOURCE_TOUCHPAD);
            instrThread.sendEvent(moveEvent);
            y1+=10;
        }

        // get key events
        KeyEvent downBack = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        instrThread.sendEvent(downBack);
        KeyEvent upBack = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK);
        instrThread.sendEvent(upBack);
    }
    /**
     * Send Swipe
     * Inject a Swipe Gesture into the System
     * @param x1 Start X coord
     * @param x2 End x coord
     */
    private void sendSwipe(float x1, float x2) {

        // get and inject down event
        MotionEvent down = getEvent(x1, 90f, MotionEvent.ACTION_DOWN);
        down.setSource(InputDevice.SOURCE_TOUCHPAD);
        instrThread.sendEvent(down);

        float currPos = x1;

        // injecting 2 move events is sufficient for moving cards
        if(x2 > x1) currPos+=50; else currPos-=50;
        MotionEvent move = getEvent(currPos, 90f, MotionEvent.ACTION_MOVE);
        move.setSource(InputDevice.SOURCE_TOUCHPAD);
        instrThread.sendEvent(move);

        if(x2 > x1) currPos+=50; else currPos-=50;
        MotionEvent move2 = getEvent(currPos, 90f, MotionEvent.ACTION_MOVE);
        move2.setSource(InputDevice.SOURCE_TOUCHPAD);
        instrThread.sendEvent(move2);

        Log.v(TAG, "X1: " + currPos);

        // get and inject up event
        MotionEvent up = getEvent(x2, 90f, MotionEvent.ACTION_UP);
        up.setSource(InputDevice.SOURCE_TOUCHPAD);
        instrThread.sendEvent(up);
    }

    /**
     * Util methods
     */

    /**
     * Get Event
     * Return an event depending on the type and the position
     * @param eventType Type of event we want to create
     */
    private MotionEvent getEvent(float x, float y, int eventType) {
        return MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                eventType,
                x,
                y,
                0
        );
    }

    /**
     * Instrumentation Thread
     * Inject Gestures to Glass
     * Seperate Thread is needed since sendPointerSync() blocks Main Thread
     */
    private class InstThread extends Thread {
        // Debug
        private static final String TAG = "Inst Thread";

        private Instrumentation mInstr;

        public InstThread(Instrumentation i) {
            mInstr = i;
        }
        @Override
        public void run() {
            Log.v(TAG, "Run");
            switch (GESTURE_TYPE) {

                case TYPE_TAP:
                    Log.v(TAG, "Simulate Tap");
                    sendTap(600f, 90f);
                    break;
                case TYPE_SWIPE_RIGHT:
                    Log.v(TAG, "Simulate Swipe right");
                    sendSwipeRight(600f, 800f);
                    break;
                case TYPE_SWIPE_LEFT:
                    Log.v(TAG, "Simulate Swipe Left");
                    sendSwipeLeft(600f, 400f);
                    break;
                case TYPE_SWIPE_DOWN:
                    Log.v(TAG, "Simulate Swipe Down");
                    sendSwipeDown(0f, 60f);
                    break;
            }
            Log.v(TAG, "Run Return");
        }
        public void sendEvent(MotionEvent event) {
            Log.v(TAG, "send event");
            mInstr.sendPointerSync(event);
        }
        public void sendEvent(KeyEvent event) {
            Log.v(TAG, "Send Key Event");
            mInstr.sendKeySync(event);
        }
    }
}
