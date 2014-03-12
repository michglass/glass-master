package com.abq.glassbluetooth.androidbluetooth.androidbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

/*
    Created By Oliver
    Date: 02/21/2014
 */
public class BluetoothService {

    // Debug
    private final static String TAG = "Bluetooth Service Android";

    // Unique UUID of this app (same as UUID on Glass)
    private static final UUID btUUID = UUID.fromString("bfdd94e0-9a5e-11e3-a5e2-0800200c9a66");

    // Bluetooth Vars
    private final BluetoothAdapter mbtAdapter;
    private BluetoothDevice mbtDevice;
    private static final String DEVICE_NAME = "Tim Wood's Glass"; // Name of Glass
    private int mCurrState; // current state of the connection

    // Handler to send messages back to mai activity
    private Handler mHandler;

    // Threads to initiate and handle connection
    private AcceptThread mAcceptThread;
    private ConnectedThread mConnectedThread;

    // Variables that indicate the Connection State
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    // Message Variables
    public static final int MESSAGE_STATE_CHANGE = 4;
    public static final int MESSAGE_WRITE = 5;
    public static final int MESSAGE_RESTART = 6;
    public static final int WAIT_FOR_CONNECTION = 7;
    public static final int THIS_STOPPED = 8; // (== ANDROID_STOPPED on Glass)

    // Messages from/to Glass
    public static final int GLASS_STOPPED = 9; // (== THIS_STOPPED on Glass)
    public static final int GLASS_OK = 10;
    public static final int GLASS_BACK = 11;

    // Specific Message for Glass


    /**
     * Constructor
     * Set up BTAdapter
     * Set up Handler
     * @param handler Handler to send messages back to Main Activity
     */
    public BluetoothService(Handler handler) {
        Log.v(TAG, "BT Service Constructor Android");
        this.mbtAdapter = BluetoothAdapter.getDefaultAdapter();

        // check if there is BT capacity, should be on Glass,Android
        if(mbtAdapter == null) {
            Log.e(TAG, "Device doesn't support Bluetooth");
        }
        // set the handler to send messages back to the UI
        mHandler = handler;

        // set message to nothings
        setState(STATE_NONE);
    }


    /**
     * Methods to handle the Threads
     * Start Threads
     * Manage Connection
     * Stop Connection, Threads
     */

    /**
     * Start
     * Set up AcceptThread
     */
    public void start(){

        // close thread trying to run connection
        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // initiate connection with new thread
        // if successful connection will be managed
        mAcceptThread = new AcceptThread(mbtAdapter);
        mAcceptThread.start();

        // if connection was successful update state
        setState(STATE_LISTENING);
    }
    /**
     * Manage Connection
     * Open ConnectedThread to handle the connection
     * @param btSocket Socket that is used in the ConnectedThread
     *                 to get I/O streams
     */
    public void manageConnection(BluetoothSocket btSocket) {
        Log.v(TAG, "start managing connection");

        // close thread that currently runs a connection
        if(mConnectedThread != null) {
            mConnectedThread.cancel(); // closes server socket
            mConnectedThread = null;
        }

        // start thread to manage connection
        mConnectedThread = new ConnectedThread(btSocket, mHandler);
        mConnectedThread.start();

        // connection was successful, set state to connected
        setState(STATE_CONNECTED);
    }
    /**
     * Disconnect
     * Cancel Sockets
     */
    public void disconnect(){

        // close thread trying to set up connection
        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // close thread running a connection
        if(mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // connection state none
        setState(STATE_NONE);
    }
    /**
     * Restart
     * If writing to Glass fails (Glass app shut down) restart in listening mode
     */
    public void restart() {
        Log.v(TAG, "Restart Connection");
        // cancel all running threads
        this.disconnect();

        // try listening again
        this.start();
    }
    /**
     * Send To Glass
     * Gets a Command (int) and sends it to Glass
     * @param command Command to send to Glass
     */
    public void sendToGlass(int command) {
        byte[] msgBytes;

        // convert int to byte array (just 3 bits needed 1 = 001 , 2 = 010)
        msgBytes = ByteBuffer.allocate(4).putInt(command).array();
        if(msgBytes == null) Log.v(TAG, "msgBytes NULL");
        write(msgBytes);
        Log.v(TAG, "Message: " + command);
    }
    /**
     * Write
     * Write to ConnectedThread (unsynchronized)
     * Write to the OutputStream to Glass
     */
    public void write(byte[] out) {
       /* // temp ConnectedThread
        ConnectedThread tempConn = null;

        // synchronize tempConn
        synchronized (this) {
            if(mCurrState != BluetoothMethods.STATE_CONNECTED) { return; }
            tempConn = mConnectedThread;
        }
        tempConn.write(out, mHandler); */
        if(mConnectedThread != null) // if trying to write out when still listening
            mConnectedThread.write(out, mHandler);
        else {
            Message msg = new Message();
            msg.what = BluetoothService.WAIT_FOR_CONNECTION;
            mHandler.sendMessage(msg);
        }
    }


    /**
     * Util Methods
     * Query Devices
     * Set State
     */

    /**
     * Adapter Enabled
     * Checks if the BT Adapter is enabled
     * @return True if Adapter is enabled, false otherwise
     */
    public boolean AdapterEnabled() {
        return this.mbtAdapter.isEnabled();
    }
    /**
     * Query Devices
     * Find Glass
     */
    public void queryDevices() {
        Log.v(TAG, "Query devices");
        // get all paired devices
        Set<BluetoothDevice> pairedDevices = mbtAdapter.getBondedDevices();
        Log.v(TAG, mbtAdapter.getName());
        try {
            // start looking only if there's at least one device
            if(pairedDevices.size() > 0) {
                // find specific Device (Glass)
                for(BluetoothDevice btDevice : pairedDevices) {
                    // if device is found save it in member var
                    if(btDevice.getName().equals(DEVICE_NAME)) {
                        mbtDevice = btDevice;
                        Log.v(TAG, "Device Name: "+ mbtDevice.getName());
                    } else {
                        Log.v(TAG, "glass not found");
                        Log.v(TAG, btDevice.getName());
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
     * Set State
     * Change State of Connection
     * Send message to Main Activity
     * @param toState New State of Connection
     */
    public void setState(int toState) {
        Log.v(TAG, "State changed from " + mCurrState + "-->"+ toState);
        mCurrState = toState;

        // send Message to UI to indicate State Change
        Message msg = new Message();
        msg.what = MESSAGE_STATE_CHANGE;
        msg.arg1 = toState;
        mHandler.sendMessage(msg);
    }
    /**
     * Get State
     * Get the current connection state
     * @return int The current state of the connection
     */
    public int getState() {
        return this.mCurrState;
    }

    /**
     * Accept Thread
     * Listens to incoming connection requests
     * Initiates ConnectedThread
     */
    private class AcceptThread extends Thread {

        // Debug
        private static final String TAG = "Accept Thread";

        // Bluetooth variables
        private final BluetoothServerSocket mBTServerSocket; // only used to listen for incoming requests
        private final BluetoothAdapter mBTAdapter;

        /**
         * Constructor
         * Set up BTAdapter
         * @param btAdapter Bluetooth Adapter
         */
        public AcceptThread(BluetoothAdapter btAdapter) {
            Log.v(TAG, "Constructor");

            mBTAdapter = btAdapter;

            // mmBTServerSocket is final -> use temp socket
            BluetoothServerSocket tempServSocket = null;
            try {
                tempServSocket = mBTAdapter.
                        listenUsingRfcommWithServiceRecord("Android Bluetooth", btUUID);
            } catch (IOException ioE) {
                Log.e(TAG, "Can't set up Server Socket", ioE);
            }
            // if successful assign mmBTServerSocket
            mBTServerSocket = tempServSocket;
        }
        /**
         * Run
         * Listen to an incoming connection request
         * Start ConnectedThread if connection successful
         */
        @Override
        public void run() {
            Log.v(TAG, "Run");

            // set up socket that will manage the connection
            BluetoothSocket btSocket;

            // keep listening until socket returned by accept or exception occurs
            while (true) {

                try {
                    Log.v(TAG, "Listen to incoming request");
                    btSocket = mBTServerSocket.accept();
                } catch (IOException ioE) {
                    Log.e(TAG, "Listening failed", ioE);
                    Log.v(TAG, "Run Return Fail");
                    break;
                }
                // if a connection was accepted
                // btSocket is already connected (no need to call connect())
                if(btSocket != null) {
                    Log.v(TAG, "Connection Accepted");
                    MainActivity.CONNECTED = true;
                    // start managing connection
                    manageConnection(btSocket);
                    // Server Socket no longer needed
                    try {
                        mBTServerSocket.close();
                    } catch (IOException ioE) {
                        Log.e(TAG, "Closing Server Socket failed", ioE);
                        break;
                    }
                    // break loop if connection successful
                    break;
                }
            }
            Log.v(TAG, "Run Return");
        }
        /**
         * Cancel
         * Closes the Server Socket (listening socket)
         */
        public void cancel() {
            try {
                Log.v(TAG, "Try closing Server Socket");
                mBTServerSocket.close();
            } catch (IOException ioE) {
                Log.e(TAG, "Closing Server Socket failed", ioE);
            }
        }
    }
}
