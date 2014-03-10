package com.abq.glassbluetooth.androidbluetooth.androidbluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Oliver
 * Date: 02/24/2014
 *
 * Connected Thread
 * Manages connection
 * Writes to OutStream
 */
public class ConnectedThread extends Thread {

    // Debug
    private static final String TAG = "Connected Thread";

    // Handler to send messages to Activity
    private Handler mHandler;

    // BT member vars
    private final BluetoothSocket mmBTSocket;
    private final OutputStream mmOutStream;
    private final InputStream mmInStream;

    /**
     * Constructor
     * Set up Socket
     * Get OutputStream
     * @param btSocket Socket to get I/O Streams
     * @param handler that sends messages to Activity
     */
    public ConnectedThread(BluetoothSocket btSocket, Handler handler) {
        Log.v(TAG, "Constructor");

        // set up handler
        mHandler = handler;

        mmBTSocket = btSocket;
        OutputStream tempOut = null;
        InputStream tempIn = null;

        // try getting the output stream
        try {
            Log.v(TAG, "Try getting Out Stream");
            tempOut = btSocket.getOutputStream();
            tempIn = btSocket.getInputStream();
        } catch (IOException ioE) {
            Log.e(TAG, "failed getting outStream");
        }
        // if successful assign to mmOutStream
        mmOutStream = tempOut;
        mmInStream = tempIn;
    }
    /**
     * Run
     * Listen to Glass Input constantly
     * Glass sends message when it shuts down
     */
    @Override
    public void run() {
        Log.v(TAG, "Run");

        byte[] buffer = new byte[1024]; // input buffer that stores the message from glass
        int bytes;

        while(true) {
            Log.v(TAG, "Loop Connected Thread");
            try {
                bytes = mmInStream.read(buffer);

                // convert bytes to int
                ByteBuffer wrapper = ByteBuffer.wrap(buffer);
                int inMessage = wrapper.getInt();
                Log.v(TAG, "In Message: " + inMessage);

                // Send Message to Activity
                sendMessage(inMessage);

            } catch (IOException e) {
                Log.e(TAG, "Failed reading from Glass", e);
                break;
            }
        }
    }
    /**
     * Write
     * Write bytes to OutputStream
     * Send Message to Main Activity
     * @param bytes Bytes to write out
     * @param handler Handler to send Message to Main Activity
     */
    public void write(byte[] bytes, Handler handler) {
        Log.v(TAG, "Write out");
        try {
            mmOutStream.write(bytes);

            // send message to Main Activity
            Message msg = new Message();
            msg.what = BluetoothMethods.MESSAGE_WRITE;
            handler.sendMessage(msg);
        } catch (IOException ioE) {
            Log.e(TAG, "Write Failed", ioE);

            // send message to main activity to restart listening
            Message restart = new Message();
            restart.what = MainActivity.MESSAGE_RESTART;
            handler.sendMessage(restart);
        }
    }
    /**
     * Call from activity to shut down connection
     */
    public void cancel() {
        Log.v(TAG, "cancel bt socket");
        try {
            MainActivity.CONNECTED = false;
            mmBTSocket.close();
        } catch (IOException ioE) {
            Log.e(TAG, "closing socket failed", ioE);
        }
    }
    /**
     * Send Message
     * Send Message from Glass to an Activity
     * @param inMessage The message that's being send to the Activity
     */
    public void sendMessage(int inMessage) {

        Message msg = new Message();
        msg.what = inMessage;
        mHandler.sendMessage(msg);
    }
}
