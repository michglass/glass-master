package com.mglass.alphagraceapp.app;

/**
 * Created by Oliver
 * Date: 2/26/14
 */
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.google.android.glass.app.Card;

public class MainActivity extends Activity {

    // Debug
    private static final String TAG = "Main Activity";

    // Card that displays message from android phone
    Card msgCard;

    // TODO Service Variables
    // Service Variables, have to be implemented in activity tht uses BT connection
    private Messenger mBluetoothServiceMessenger;
    private boolean mBound;
    private final Messenger clientMessenger = new Messenger(new ServiceHandler());

    /**
     * Starts Service
     * @param savedInstanceState Saved State of Activity
     */
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

        //TODO Start Service
        // start service, only in Main Activity!!
        // Main Activity should not be Destroyed while other activities are connected to the service
        startService(new Intent(this, BluetoothService.class));
    }

    /**
     * On Start
     * Bind to Service
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "On Start");
        setContentView(msgCard.toView());

        //TODO Bind Service
        if(!mBound) {
            bindService(new Intent(this, BluetoothService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
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

        //TODO Unbind Service
        if(mBound) {
            sendMessageToService(BluetoothService.INT_MESSAGE, BluetoothService.UNREGISTER_CLIENT);
            unbindService(mConnection);
            mBound = false;
        }
    }
    /**
     * On Destroy
     * Close the sockets
     * Stop the Threads
     */
    @Override
    public void onDestroy() {
        Log.v(TAG, "On Destroy");

        //TODO Stop Service
        // Only in Main Activity
        stopService(new Intent(this, BluetoothService.class));

        super.onDestroy();
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
        msgCard.setText("MainActivity: " + s);
        setContentView(msgCard.toView());
    }

    //TODO Service Connection
    /**
     * ServiceConnection
     * Callback Methods that get called when Client binds to Service
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

    //TODO Send Message to Service
    /**
     * Send Message To Service
     * Sends a message regarding the connection status
     */
    public void sendMessageToService(int messageType, Object message) {
        Message msg = new Message();

        switch (messageType) {
            case BluetoothService.INT_MESSAGE:
                int intMsg = (Integer) message;
                msg.what = intMsg;
                break;
            case BluetoothService.TEXT_MESSAGE:
                msg.what = BluetoothService.TEXT_MESSAGE;
                msg.obj = message;
                break;
            case BluetoothService.PICTURE_MESSAGE:
                msg.what = BluetoothService.PICTURE_MESSAGE;
                msg.obj = message;
                break;
        }

        try {
            Log.v(TAG, "Try contacting Service");
            mBluetoothServiceMessenger.send(msg);
        } catch (RemoteException remE) {
            Log.e(TAG, "Couldn't contact Service", remE);
        }
    }
    //TODO First message upon establishing binding to service
    /**
     * Set Up Message
     * First Conact with Service
     */
    public void setUpMessage() {
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
    //TODO Handler for handling messages from Service
    /**
     * Message Handler
     * Receive Messages from BluetoothService about connection state
     * Receive Messages from Connected Thread (android input)
     */
    public class ServiceHandler extends Handler {

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

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {

        if(keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            //startActivity(new Intent(this, SubActivity.class));
            sendMessageToService(BluetoothService.TEXT_MESSAGE, "HeyHey");
            return true;
        }
        return super.onKeyDown(keycode, event);
    }
}
