package com.abq.servicetest.servicetest;

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
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

    // Start -> Bind -> Stop ... doesnt stop the service
    // Start -> Bind -> Stop -> Unbind results in On destroy service
    // Start -> Bind -> Stop -> Start -> Unbind doesnt result in On Destroy Service
    // Start -> Bind -> Stop -> Start -> Unbind -> Stop results in On Destroy
    // --> conclusion: stopService doesn't stop a service as long as there is still a component bind to it
    // --> stop service and unbind last remaining component results in destruction (without calling stop again)
    // if sth happens (start, bind) between stopService and unbind, calling unbind doesn't destroy the service
    // Debug
    private static final String TAG = "Main Activity";

    /**
     * Service Stuff
     */
    // Messenger for sending messages to service
    private Messenger mBluetoothServiceMessenger;

    /**
     * Service Stuff
     */
    // boolean indicating if client is connected to service
    private boolean mBound;

    /**
     * Service Stuff
     */
    // Messenger that gets published to Service
    private final Messenger clientMessenger = new Messenger(new ServiceHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "On Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Service Stuff
         */
        // start service
        startService(new Intent(this, BluetoothService.class));
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "On Start");
        super.onStart();

        /**
         * Service Stuff
         */
        if(!mBound) {
            bindService(new Intent(this, BluetoothService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }
    @Override
    protected void onStop() {
        Log.v(TAG, "On Stop");

        super.onStop();

        /**
         * Service Stuff
         */
        if(mBound) {
            sendMessageToService(BluetoothService.UNREGISTER_CLIENT);
            unbindService(mConnection);
            mBound = false;
        }
    }
    @Override
    protected void onDestroy() {
        Log.v(TAG, "On Destroy");

        /**
         * Service Stuff
         */
        stopService(new Intent(this, BluetoothService.class));
        BluetoothService.BOUND_COUNT = 0;

        super.onDestroy();
    }

    public void OnButtonClick(View v) {

        switch(v.getId()) {

            case R.id.sendOkMain:
                Log.v(TAG, "Send OK to Service");
                sendMessageToService(BluetoothService.GLASS_OK);
                break;
            case R.id.sendBackMain:
                Log.v(TAG, "Send Back to Service");
                sendMessageToService(BluetoothService.GLASS_BACK);
                break;
            case R.id.startSubActivity:
                Log.v(TAG, "Start Sub Button");
                startActivity(new Intent(this, SubActivity.class));
                break;
        }
    }

    /**
     * Service Stuff
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
    /**
     * Service Stuff
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

    /**
     * Service Stuff
     */
    private class ServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch(msg.what) {

                // connection state changed
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    Toast.makeText(getApplicationContext(),
                            "Connection state changed", Toast.LENGTH_SHORT).show();
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(),
                                    "Connected", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(),
                                    "Connecting", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_LISTENING:
                            Toast.makeText(getApplicationContext(),
                                    "Listening", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_NONE:
                            Toast.makeText(getApplicationContext(),
                                    "Doing Nothing", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_WRITE:
                    Toast.makeText(getApplicationContext(),
                            "Write to OutStream", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.MESSAGE_RESTART:
                    Toast.makeText(getApplicationContext(),
                            "Restart Listening", Toast.LENGTH_SHORT).show();

                    // send message to service that it has to restart the connection
                    sendMessageToService(BluetoothService.MESSAGE_RESTART);
                    break;
                case BluetoothService.WAIT_FOR_CONNECTION:
                    Toast.makeText(getApplicationContext(),
                            "Wait for Connection", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.GLASS_STOPPED:
                    Toast.makeText(getApplicationContext(),
                            "Restart Listening", Toast.LENGTH_SHORT).show();
                    // send message to service that it has to restart the connection
                    sendMessageToService(BluetoothService.MESSAGE_RESTART);
                    break;
            }
        }
    }
}
