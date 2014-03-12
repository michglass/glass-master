package com.mglass.alphagraceapp.app;

/**
 * Created by Oliver Breit
 * Date: 2/26/14
 */
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.glass.app.Card;

public class MainActivity extends Activity {

    // Debug
    private static final String TAG = "Main Activity";

    // Bluetooth Vars
    private BluetoothService mbtService;

    // Card that displays message from android phone
    Card msgCard;

    // Request Variables
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "On Create");

        super.onCreate(savedInstanceState);

        //TODO keep screen from dimming
        // keep screen from dimming
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        msgCard = new Card(this);
        msgCard.setText("Badadum");
        setContentView(msgCard.toView());

        //TODO Set up Bluetooth Service
        mbtService = new BluetoothService(mHandler); // set up bluetooth service

        Log.v(TAG, "On Create");
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "On Start");

        setContentView(msgCard.toView());

        //TODO See if Adapter is enabled and query devices
        // Request enabling Bluetooth, if it's not on
        if(!mbtService.AdapterEnabled()) {
            // Should always be enabled on Glass!
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.v(TAG, "Bluetooth already enabled"); // usually on Glass
            // find device Glass should be paired to
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

        //TODO Connect to Android
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

        //TODO Disconnect from Android
        // activity not longer visible
        // stop all threads which also close the sockets
        if(mbtService != null) {
            mbtService.write(BluetoothService.THIS_STOPPED);
            mbtService.stopThreads();
        }
    }
    /**
     * On Destroy
     * Close the sockets
     * Stop the Threads
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "On Destroy");

        //TODO Disconnect Bluetooth from Android
        mbtService.stopThreads();
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
        String s = String.valueOf(msg);
        msgCard.setText(s);
        setContentView(msgCard.toView());
    }

    //TODO Handler for Messages from Android/BT Service
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
                case BluetoothService.MESSAGE_STATE_CHANGE:
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
                case BluetoothService.MESSAGE_INCOMING:
                    Log.v(TAG, "message income");
                    // display message on the card, null check is performed
                    // before message gets send --> shouldn't be null
                    // String msgFromPhone = msg.getData().getString(BluetoothService.EXTRA_MESSAGE);
                    // display message on card
                    // updateCard(msgFromPhone);
                    break;
                // user commands that manipulate glass timeline
                //TODO on those commands invoke some kind of simulated Inputs
                case BluetoothService.COMMAND_OK:
                    Log.v(TAG, "Command ok");
                    updateCard(BluetoothService.COMMAND_OK);
                    break;
                case BluetoothService.COMMAND_BACK:
                    Log.v(TAG, "Command back");
                    updateCard(BluetoothService.COMMAND_BACK);
                    break;
                case BluetoothService.ANDROID_STOPPED:
                    Log.v(TAG, "Android App closed");
                    mbtService.setState(BluetoothService.STATE_NONE);
                    finish(); // close this application if Android application is down
                    break;
                case BluetoothService.MESSAGE_CONNECTION_FAILED:
                    Log.v(TAG, "Failed Conn App Closing");
                    mbtService.setState(BluetoothService.STATE_NONE);
                    finish();
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
