package com.mglass.alphagraceapp.app;

/**
 * Created by vbganesh on 2/26/14.
 */
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.glass.app.Card;



import com.google.android.glass.app.Card;

import java.io.File;

public class MainActivity extends Activity {

    private Card mCard = new Card(this);
    private boolean baseMenu = true;
    private boolean colorMenu = false;
    private boolean animalMenu = false;
    private boolean foodMenu = false;
    private Handler handler = new Handler();


    // Debug
    private static final String TAG = "Main Activity";

    // Bluetooth Vars
    private BluetoothAdapter mbtAdapter;
    private BluetoothService mbtService;

    // Card that displays message from android phone
    Card msgCard;

    // Request Variables
    private static final int REQUEST_ENABLE_BT = 1;

    // messages from BT service
    public static final int MESSAGE_STATE_CHANGE = 3; // indicates connection state change (debug)
    public static final int MESSAGE_INCOMING = 4; // message with string content (only for debug)

    // messages that indicate commands (Tap = select, Right, Left, Down = back)
    public static final int COMMAND_OK = 1;
    public static final int COMMAND_BACK = 2;




    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "inside void run()");
      /* do what you need to do */
            if(baseMenu){
                if(mCard.getText() == "Favorite Color"){
                    mCard.setText("Favorite Animal");
                }
                else if(mCard.getText() == "Favorite Animal"){
                    mCard.setText("Favorite Food");
                }
                else{
                    mCard.setText("Favorite Color");
                }
            }
            else if(colorMenu){
                if(mCard.getText() == "Pink"){
                    mCard.setText("Blue");
                    mCard.clearImages();
                    mCard.addImage(R.drawable.blue);
                }
                else if(mCard.getText() == "Blue"){
                    mCard.setText("Purple");
                    mCard.clearImages();
                    mCard.addImage(R.drawable.purple);
                }
                else{
                    mCard.setText("Pink");
                    mCard.clearImages();
                    mCard.addImage(R.drawable.pink);
                }
            }
            else if(animalMenu){
                if(mCard.getText() == "Puppies"){
                    mCard.setText("Kitties");
                    mCard.clearImages();
                    //mCard.setImageLayout(Card.ImageLayout.FULL);
                    mCard.addImage(R.drawable.kitten);
                }
                else{
                    mCard.setText("Puppies");
                    mCard.clearImages();
                    //mCard.setImageLayout(Card.ImageLayout.FULL);
                    mCard.addImage(R.drawable.puppy);
                }
            }
            else{
                if(mCard.getText() == "Ice Cream"){
                    mCard.setText("Candy");
                    mCard.clearImages();
                    mCard.addImage(R.drawable.candy);
                }
                else if(mCard.getText() == "Candy"){
                    mCard.setText("Fruit");
                    mCard.clearImages();
                    mCard.addImage(R.drawable.fruits);
                }
                else{
                    mCard.setText("Ice Cream");
                    mCard.clearImages();
                    mCard.addImage(R.drawable.icecream);
                }
            }
            setContentView(mCard.toView());
      /* and here comes the "trick" */
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "On Create");

        super.onCreate(savedInstanceState);

        // keep screen from dimming
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        msgCard = new Card(this);
        msgCard.setText("Messages get displayed here");
        setContentView(msgCard.toView());

        this.mbtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mbtAdapter == null) {
            Log.v(TAG, "BT not supported");
            finish();
        }
        runnable.run();
        mCard.setText("Favorite Color");
        setContentView(mCard.toView());
        mCard.setImageLayout(Card.ImageLayout.FULL);
        Log.v(TAG, "On Create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "On Destroy");

        if(mbtService != null) {
            mbtService.stopThreads();
        }
    }


//From GlassBluetooth


    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "On Start");

        msgCard.setText("Welcome to Grace's Glasses");
        setContentView(msgCard.toView());

        // Request enabling Bluetooth, if it's not on
        if(!mbtAdapter.isEnabled()) {
            // Should always be enabled on Glass!
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.v(TAG, "Bluetooth already enabled"); // usually on Glass
            // find device Glass should be paired to
            mbtService = new BluetoothService(mHandler); // set up bluetooth service
            mbtService.queryDevices();
        }
    }
    /**
     * On Resume (Activity visible, not in foreground)
     * Start connection
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "On Resume");
        // Starting connection with mbtService
        // if successful ConnectedThread will start
        // (called from within ConnectThread.run) that manages the connection
        mbtService.connect();
    }
    /**
     * On Pause (Activity is not in foreground)
     */
    @Override
    protected void onPause() {
        Log.v(TAG, "On Pause");
        super.onPause();
    }
    /**
     * On Stop (Activity not longer visible)
     */
    @Override
    protected void onStop() {
        Log.v(TAG, "On Stop");
        super.onStop();

        // activity not longer visible
        // stop all threads which also close the sockets
        if(mbtService != null) {
            mbtService.stopThreads();
        }
    }

    /**
     * Util Functions
     */

    /**
     * Update Cards
     * Sets new Text on Card
     * @param msg Message from Android Phone
     */
    public void updateCard(int msg) {

        //String msgString = String.valueOf(msg);
        if(mCard.getText() == "Favorite Color"){
            baseMenu = false;
            colorMenu = true;
            animalMenu = false;
            foodMenu = false;
        }
        else if(mCard.getText() == "Favorite Animal"){
            baseMenu = false;
            colorMenu = false;
            animalMenu = true;
            foodMenu = false;
        }
        else if(mCard.getText() == "Favorite Food"){
            baseMenu = false;
            colorMenu = false;
            animalMenu = false;
            foodMenu = true;
        }
        //msgCard.setText(msgString);
        //setContentView(msgCard.toView());
    }

    /**
     * Message Handler
     * Receive Messages from BluetoothService about connection state
     * Receive Messages from Connected Thread (android input)
     */
    private final Handler mHandler = new Handler() {

        // when message gets send this method
        // gives info to activity
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.v(TAG, "connection state changed");
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Log.v(TAG, "state connected");
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Log.v(TAG, "state connecting");
                            break;
                        case BluetoothService.STATE_NONE:
                            Log.v(TAG, "state none");
                            break;
                    }
                    break;
                // in case this activity received a string message from phone
                case MESSAGE_INCOMING:
                    Log.v(TAG, "message income");
                    // display message on the card, null check is performed
                    // before message gets send --> shouldn't be null
                    // String msgFromPhone = msg.getData().getString(BluetoothService.EXTRA_MESSAGE);
                    // display message on card
                    // updateCard(msgFromPhone);
                    break;
                // user commands that manipulate glass timeline
                //TODO on those commands invoke some kind of simulated Inputs
                case COMMAND_OK:
                    Log.v(TAG, "Command ok");
                    updateCard(1);
                    break;
                case COMMAND_BACK:
                    Log.v(TAG, "Command back");
                    updateCard(2);
                    break;
            }
        }
    };

    /**
     * On Activity Result
     * System callback method for startActivityForResult()
     * @param requestCode Code that we put in startActivityForResult
     * @param resultCode Code that indicates if Bluetooth connection was established
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // shouldn't be called, because Bluetooth should be enabled on Glass
        switch (resultCode) {

            case RESULT_OK:
                Log.v(TAG, "Bluetooth Success");
                return;
            case RESULT_CANCELED:
                Log.v(TAG, "Bluetooth Failed");
        }
    }
}
