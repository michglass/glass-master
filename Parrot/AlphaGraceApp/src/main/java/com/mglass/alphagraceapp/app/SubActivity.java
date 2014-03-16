package com.mglass.alphagraceapp.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.glass.app.Card;

/**
 * Created by Oliver
 * Date: 3/16/14.
 */
public class SubActivity extends Activity {

    // Debug
    private static final String TAG = "Sub Activity";

    // Card
    Card mSubCard;

    //TODO Service Variables
    // Service Variables
    private Messenger mBluetoothServiceMessenger;
    private boolean mBound;
    private final Messenger clientMessenger = new Messenger(new ServiceHandler());

    /**
     * On Create
     * Don't start Service here!!
     * @param savedInstanceState Saved Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "On Create");
        super.onCreate(savedInstanceState);

        //TODO keep screen from dimming
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSubCard = new Card(this);
        mSubCard.setText("Sub Activity");
        setContentView(mSubCard.toView());
    }
    /**
     * On Start
     * Bind to Service
     */
    @Override
    protected void onStart() {
        Log.v(TAG, "On Start");
        super.onStart();

        //TODO Bind to Service
        if(!mBound) {
            bindService(new Intent(this, BluetoothService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }
    /**
     * On Stop
     * Unbind from Service
     */
    @Override
    protected void onStop() {
        Log.v(TAG, "On Stop");
        super.onStop();

        //TODO Unbind from Service
        if(mBound) {
            sendMessageToService(BluetoothService.UNREGISTER_CLIENT);
            unbindService(mConnection);
            mBound = false;
        }
    }
    @Override
    protected void onDestroy() {
        Log.v(TAG, "On Destroy");
        super.onDestroy();
    }

    /**
     * Update Cards
     * Sets new Text on Card
     * @param msg Message from Android Phone
     */
    public void updateCard(int msg) {
        String s = String.valueOf(msg);
        mSubCard.setText("SubActivity: " + s);
        setContentView(mSubCard.toView());
    }
    //TODO Service Connection
    /**
     * Service Connection
     * For getting Interface (messenger) to Service)
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.v(TAG, "On Service Connect");

            // set up messenger
            mBluetoothServiceMessenger = new Messenger(iBinder);
            mBound = true;

            setUpMessage();
        }

        /**
         * Only called when Service unexpectedly disconnected!!
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.v(TAG, "On Service Disconnect");
            mBound = false;
        }
    };
    //TODO Send message to Service
    /**
     * Send Message To Service
     * Send a message concerning the connection to the service
     */
    private void sendMessageToService(int message) {
        Message msg = new Message();
        msg.what = message;

        try {
            Log.v(TAG, "Try contacting Service");
            mBluetoothServiceMessenger.send(msg);
        } catch (RemoteException remE) {
            Log.e(TAG, "Couldn't contact Service", remE);
        }
    }
    //TODO Set up Message
    /**
     * Set Up Message
     * Message for a first contact to service
     */
    private void setUpMessage() {
        Message startMsg = new Message();
        startMsg.what = BluetoothService.REGISTER_CLIENT;
        startMsg.replyTo = clientMessenger;

        try {
            Log.v(TAG, "First time contact to service");
            mBluetoothServiceMessenger.send(startMsg);
        } catch (RemoteException remE) {
            Log.e(TAG, "Couldn't contact Service", remE);
        }
    }
    //TODO Handler for Service Messages
    /**
     * Message Handler
     * Receive Messages from BluetoothService about connection state
     * Receive Messages from Connected Thread (android input)
     */
    private class ServiceHandler extends Handler {

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
                    // mbtService.setState(BluetoothService.STATE_NONE);
                    finish(); // close this application if Android application is down
                    break;
                case BluetoothService.MESSAGE_CONNECTION_FAILED:
                    Log.v(TAG, "Failed Conn App Closing");
                    // mbtService.setState(BluetoothService.STATE_NONE);
                    finish();
                    break;
            }
        }
    }
}
