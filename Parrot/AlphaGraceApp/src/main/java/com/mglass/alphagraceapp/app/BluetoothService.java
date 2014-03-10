package com.mglass.alphagraceapp.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/*
    Created by Oliver
    Date: 02/19/2014
 */
public class BluetoothService {

    // Debug
    private static final String TAG = "Bluetooth Service";

    // Bluetooth Vars
    private final BluetoothAdapter mbtAdapter;
    private BluetoothDevice mbtDevice;
    private static final String DEVICE_NAME = "Cone"; // Name of Danny's phone
    private int mCurrState;

    // Thread that initiates the connection and Thread that manages connection
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    // unique ID for the app (same as on the android phone)
    private static final UUID btUUID = UUID.fromString("bfdd94e0-9a5e-11e3-a5e2-0800200c9a66");


    // Constants that indicate the devices state
    public static final int STATE_NONE = 0; // doing nothing
    public static final int STATE_CONNECTING = 1; // connecting
    public static final int STATE_CONNECTED = 2; // connected

    // Extra is the key for the string message that gets put into the bundle
    // the bundle is send to the activity which can get the string message with this key
    public static final String EXTRA_MESSAGE = "com.mglass.alphagraceapp.app.EXTRA_MESSAGE";

    // handler that can send messages back to UI
    private Handler mMainHandler; // Handler from Main Activity

    /**
     * Constructor
     * Set up Handler (to be able to send messages back to Main Activity)
     * Set up Bluetooth Adapter
     * @param btHandler Handler from Main Activity
     */
    public BluetoothService(Handler btHandler) {
        Log.v(TAG, "BTService Constructor");
        this.mbtAdapter = BluetoothAdapter.getDefaultAdapter();

        // set the handler
        mMainHandler = btHandler;

        // check if there is BT capacity, should be on Glass,Android
        if(mbtAdapter == null) {
            Log.e(TAG, "Device doesn't support Bluetooth");
        }

        // set the current state
        setState(STATE_NONE);
    }

    /**
     * Methods to handle the Threads
     * Start Threads
     * Manage Connection
     * Shut Down Threads
     */

    /**
     * Connect
     * Start Up Connection by starting ConnectThread
     * Called by Activity onResume
     */
    public void connect() {
        Log.v(TAG, "Connect");

        // Cancel all Threads currently trying to set up a connection
        if(mCurrState == STATE_CONNECTING) {
            if(mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel Thread that currently runs a connection
        if(mConnectedThread != null) {
            mConnectedThread.cancel(); // close socket
            mConnectedThread = null;
        }

        // Start thread to connect to device
        // Device is passed to obtain socket and Handler for sending messages to Activity
        mConnectThread = new ConnectThread(mbtDevice);
        mConnectThread.start();

        // set state to connecting and send message to activity
        setState(STATE_CONNECTING);
    }
    /**
     * Manage Connection
     * Start ConnectedThread
     * @param btSocket Socket that helps to get I/O streams
     */
    public void manageConnection(BluetoothSocket btSocket) {
        Log.v(TAG, "Manage Connection");
/*
        // Cancel thread currently setting up a connection
        if(mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
*/
        // Cancel thread currently running a connection
        if(mConnectedThread != null) {
            mConnectedThread.cancel(); // closes Bluetooth socket
            mConnectedThread = null;
        }

        // Start thread to manage the connection
        mConnectedThread = new ConnectedThread(btSocket, mMainHandler);
        mConnectedThread.start();

        // connection successful, set state to connected
        setState(STATE_CONNECTED);
    }
    /**
     * Stop Threads
     * Stop all threads
     * Called by Activity onDestroy
     */
    public void stopThreads() {
        Log.v(TAG, "Stop all Threads");

        // cancel connecting thread if it exists
        if(mConnectThread != null) {
            mConnectThread.cancel(); // cancels the socket
            mConnectThread = null;
        }

        // cancel thread running the connection
        if(mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // all threads stopped, set state to none and send message to UI
        setState(STATE_NONE);
    }


    /**
     * Util Methods
     */

    /**
     * Set state
     * Set the new connection state
     * Send message to Main Activity
     * @param toState new State of connection
     */
    public void setState(int toState) {
        Log.v(TAG, "State changed from " + mCurrState + "-->"+ toState);
        mCurrState = toState;

        // send message to Main Activity
        Message msg = new Message();
        msg.what = MainActivity.MESSAGE_STATE_CHANGE;
        msg.arg1 = toState;
    }
    /**
     * Query Devices
     * Query all available devices
     * Assign Danny's (later Grace's) phone to the member device
     */
    public void queryDevices() {
        Log.v(TAG, "Query devices");
        // get all paired devices
        Set<BluetoothDevice> pairedDevices = mbtAdapter.getBondedDevices();
        Log.v(TAG, mbtAdapter.getName());
        try {
            // start looking only if there's at least one device
            if(pairedDevices.size() > 0) {
                // find specific Device (Grace's phone)
                for(BluetoothDevice btDevice : pairedDevices) {
                    // if device is found save it in member var
                    if(btDevice.getName().equals(DEVICE_NAME)) {
                        mbtDevice = btDevice;
                        Log.v(TAG, "Device Name: "+ mbtDevice.getName());
                    }
                }
            } else {
                Log.v(TAG, "No devices found");
            }
        } catch (Exception e) {
            Log.v(TAG, "No devices found");
        }
    }

    /**
     * Connect Thread
     * Send out connection request
     * Start ConnectedThread
     */
    private class ConnectThread extends Thread {
        // Debug
        private static final String TAG = "Connect Thread";

        // Bluetooth Vars
        private final BluetoothSocket mmBtSocket;

        /**
         * Constructor
         * Set up the device
         * Set up the Handler (sending messages back to Main Activity)
         * Set up Socket
         * @param device Bluetooth device variable (e.g. Grace's phone)
         */
        public ConnectThread(BluetoothDevice device) {
            Log.v(TAG, "Constructor");

            BluetoothSocket tempSocket = null;

            // set up bluetooth socket with UUID
            try {
                Log.v(TAG, "Try setting up Socket");
                // returns a BT Socket ready for outgoing connection
                tempSocket = device.createRfcommSocketToServiceRecord(btUUID);
            } catch (Exception e) {
                Log.e(TAG, "Socket Setup Failed");
            }
            mmBtSocket = tempSocket;
        }
        /**
         * Run
         * Send out request for connection
         * When connection established, start Manage Connection
         */
        @Override
        public void run() {
            Log.v(TAG, "Run");
            // Connect device through Socket
            // Blocking call!
            try {
                Log.v(TAG, "Try connecting through socket");
                mmBtSocket.connect();
            } catch (IOException connectException) {
                // unable to connect, try closing socket
                try {
                    Log.v(TAG, "Unable to connect");
                    mmBtSocket.close();
                } catch(IOException closeException) {
                    Log.e(TAG, "Closing Socket Failed", closeException);
                }
                Log.v(TAG, "Run Return after Fail");
                return;
            }

            // connection established, manage connection
            manageConnection(mmBtSocket);
            Log.v(TAG, "Run Return after Success");
        }
        /**
         * Cancel
         * Close Socket
         */
        public void cancel() {
            try {
                Log.v(TAG, "Try Closing Socket");
                mmBtSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Closing Socket Failed", e);
            }
        }
    }
}
