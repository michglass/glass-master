package com.abq.glassbluetooth.androidbluetooth.androidbluetooth;

import android.os.Handler;
import android.util.Log;

/**
 * Created by Oliver
 * Date: 02/24/2014
 *
 * Common methods that usually can be implemented by
 * activities that use Bluetoothconnections
 */
public interface BluetoothMethods {

    // Constants that indicate the devices state
    public static final int STATE_NONE = 0; // doing nothing
    public static final int STATE_LISTENING = 1; // listening for connections
    public static final int STATE_CONNECTING = 2; // connecting
    public static final int STATE_CONNECTED = 3; // connected

    // Constants that indicate the messages send by the Bluetoothservice handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_WRITE = 2;

    // Modify the current state of the connection
    // Set state of connection
    public void setState(int toState);

    // Create a handler that the BTservice and Threads can use to send
    // messages to activities
    public Handler setHandler();
}
