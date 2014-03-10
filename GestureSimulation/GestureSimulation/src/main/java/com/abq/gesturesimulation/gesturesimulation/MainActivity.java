package com.abq.gesturesimulation.gesturesimulation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.android.glass.app.Card;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // Debug
    private static final String TAG = "Main Activity";

    Card mainCard1;
    Card mainCard2;
    private List<Card> mCards = new ArrayList<Card>();
    private CardScrollView mCardScrollView;
    // private GestureDetector gestureDetector;
    private final Context mContext = this;
    ExampleCardScrollAdapter adapter;
    Runnable mSwipeLoopRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen from dimming
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // gestureDetector = createGestureDetector(this);

        mCards.add(new Card(this).setText("Card 1"));
        mCards.add(new Card(this).setText("Card 2"));
        mCards.add(new Card(this).setText("Card 3"));
        mCardScrollView = new CardScrollView(this);
        adapter = new ExampleCardScrollAdapter();
        mCardScrollView.setAdapter(adapter);
        mCardScrollView.activate();
        setContentView(mCardScrollView);

        Gestures gestures = new Gestures();
        mSwipeLoopRunnable = gestures.swipeLoop(adapter.getCount());

        createCardClickListener();
    }
    @Override
    protected void onResume() {

        mSwipeLoopRunnable.run();
        super.onResume();
    }
    @Override
    protected void onStop() {
        Log.v(TAG, "On Stop");
        Gestures.mKeepRunning = false;
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        Log.v(TAG, "On Destroy");
        super.onDestroy();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        String coord = "X: " + x + ", Y: " + y;

        switch(event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                Log.v(TAG, "Action Down: " + coord);
                break;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "Action Up: " + coord);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.v(TAG, "Action Move: " + coord);
                break;
            case MotionEvent.ACTION_SCROLL:

                break;
        }

        return super.onGenericMotionEvent(event);//gestureDetector.onMotionEvent(event);
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if(keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            Log.v(TAG, "Tap");
        }
        if(keycode == KeyEvent.KEYCODE_BACK) {
            Log.v(TAG, "Swipe Down");
        }
        return super.onKeyDown(keycode, event);
    }
    @Override
    public boolean onKeyUp(int keycode, KeyEvent event) {
        if(keycode == KeyEvent.KEYCODE_BACK)
            Log.v(TAG, "Up Back");
        return super.onKeyUp(keycode, event);
    }
    /**
     * create listener for cards clicked in the view
     */
    private void createCardClickListener() {
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Card clickedCard = (Card) adapter.getItem(position);
                String cardText = clickedCard.getText();
                Log.v(TAG, "Card Name: " + cardText + " Pos: " + position);
               /* Gestures gestures = new Gestures();
                gestures.createGesture(Gestures.TYPE_SWIPE_RIGHT);*/
            }
        });
    }


    public GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        Log.v(TAG, "Gesture Detector");
        final Context thisContext = context;
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                Log.v(TAG, "On Gesture");
                if(gesture == Gesture.TAP) {
                    Card tapCard = new Card(thisContext);
                    tapCard.setText("Tap");
                    setContentView(tapCard.toView());
                    Log.v(TAG, "Tap");
                } else if(gesture == Gesture.LONG_PRESS) {
                    Log.v(TAG, "Long Press");
                } else if(gesture == Gesture.SWIPE_LEFT) {

                    Log.v(TAG, "Swipe Left");
                } else if(gesture == Gesture.SWIPE_RIGHT) {
                    Log.v(TAG, "Swipe Right");
                } else if(gesture == Gesture.SWIPE_DOWN) {
                    Log.v(TAG, "Swipe Down");
                }
                return false;
            }
        });
        return gestureDetector;
    }

    private class ExampleCardScrollAdapter extends CardScrollAdapter {

        @Override
        public int findIdPosition(Object id) {
            return -1;
        }

        @Override
        public int findItemPosition(Object item) {
            return mCards.indexOf(item);
        }

        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position).toView();
        }
    }

}
