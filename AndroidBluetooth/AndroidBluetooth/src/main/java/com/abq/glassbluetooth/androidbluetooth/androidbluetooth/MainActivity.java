package com.abq.glassbluetooth.androidbluetooth.androidbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.nio.ByteBuffer;

public class MainActivity extends Activity {

    // Debug
    private static final String TAG = "Main Activity Android";

    // Bluetooth Vars
    private BluetoothAdapter mbtAdapter;
    private BluetoothService mbtService;
    private Handler mHandler;

    // Buttons for menu to choose between Interface
    Button mOneButtonMenu;
    Button mTwoButtonMenu;

    // Button for one button menu (short click = select, long click = go back)
    Button mSelectButton;

    // message for glass
    private int msgToGlass;
    private byte[] msgBytes;

    // indicates connection
    public static boolean CONNECTED = false;
    public static final int WAIT_FOR_CONNECTION = 11;

    // Message from Glass to indicate that it has stopped running the BT connection
    public static final int GLASS_STOPPED = 9;

    // Commands that get send to Glass
    private static final int GLASS_OK = 1;
    private static final int GLASS_BACK = 2;
    private static final int THIS_STOPPED = 5; // tell glass that this application has stopped

    // message from connected thread to restart listening
    public static final int MESSAGE_RESTART = 10;

    // Request to enable BT
    private static final int REQUEST_ENABLE_BT = 1;

    /**
     * On Create
     * Set up Bluetooth Adapter
     * Set up Buttons
     * Set up Handler
     * Set up Menu to select between one button or two button interface
     * @param savedInstanceState Saved State of Application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // choice between two button or one button menu
        setContentView(R.layout.interface_option);

        Log.v(TAG, "On Create");

        // set message handler
        mHandler = setHandler();

        // set up the option buttons
        mOneButtonMenu = (Button) findViewById(R.id.buttonOneButton);
        mTwoButtonMenu = (Button) findViewById(R.id.buttonTwoButton);

        // set the device adapter
        this.mbtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mbtAdapter == null) {
            Log.v(TAG, "BT not supported");
            finish();
        }
    }
    /**
     * On Start
     * Set up BTService
     * Query paired devices
     */
    @Override
    protected void onStart() {
        Log.v(TAG, "On Start");
        super.onStart();

        // Request enabling Bluetooth and query paired devices if it is on
        if(!mbtAdapter.isEnabled()) {
            // Should always be enabled on Glass!
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.v(TAG, "Bluetooth already enabled"); // usually on Glass
            // find device Glass should be paired to
            mbtService = new BluetoothService(mHandler);
            mbtService.queryDevices();
        }
    }
    /**
     * On Resume
     * Start the connection
     * Set up Button Click Listener
     * Write out to glass
     */
    @Override
    public void onResume() {
        Log.v(TAG,"On Resume");
        super.onResume();

        // start the accept thread to listen to incoming connection requests
        mbtService.start();
    }
    /**
     * On Stop (Activity not in visible)
     */
    @Override
    public void onStop() {
        Log.v(TAG, "On Stop");

        sendToGlass(THIS_STOPPED); // tell Glass that this application has stopped
        super.onStop();
    }
    /**
     * On Destroy (Activity canceled)
     * Cancel connections
     */
    @Override
    public void onDestroy() {

        Log.v(TAG, "On Destroy");
        super.onDestroy();

        // close threads and sockets
        mbtService.disconnect();
        //TODO disconnect in onStop, maybe
    }


    /**
     * Util methods
     */

    /**
     * Set Button Listeners
     * Determine what happens on Short Click and Long Click
     */
    public void setButtonListeners() {

        mSelectButton = (Button) findViewById(R.id.selectButton);

        // set button click listener
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on short click OK gets send
                msgToGlass = GLASS_OK;
                sendToGlass(msgToGlass);
            }
        });

        // set a long click listener
        mSelectButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // on long click Back gets send
                msgToGlass = GLASS_BACK;
                sendToGlass(msgToGlass);
                return true;
            }
        });
    }
    /**
     * Send to Glass
     * Send command via BT to Glass
     * @param command Command that is being send to Glass
     */
    public void sendToGlass(int command) {
        msgToGlass = command;

        // convert int to byte array (just 3 bits needed 1 = 001 , 2 = 010)
        msgBytes = ByteBuffer.allocate(4).putInt(msgToGlass).array();
        if(msgBytes == null) Log.v(TAG, "msgBytes NULL");
        mbtService.write(msgBytes);
        Log.v(TAG, "Message: " + msgToGlass);
    }
    /**
     * Set Handler
     * @return Handler that gets messages from BTService and ConnectedThread
     */
    public Handler setHandler() {

        Handler btHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                switch(msg.what) {

                    // connection state changed
                    case BluetoothMethods.MESSAGE_STATE_CHANGE:
                        Toast.makeText(getApplicationContext(),
                                "Connection state changed", Toast.LENGTH_SHORT).show();
                        switch (msg.arg1) {
                            case BluetoothMethods.STATE_CONNECTED:
                                Toast.makeText(getApplicationContext(),
                                        "Connected", Toast.LENGTH_SHORT).show();
                                break;
                            case BluetoothMethods.STATE_CONNECTING:
                                Toast.makeText(getApplicationContext(),
                                        "Connecting", Toast.LENGTH_SHORT).show();
                                break;
                            case BluetoothMethods.STATE_LISTENING:
                                Toast.makeText(getApplicationContext(),
                                        "Listening", Toast.LENGTH_SHORT).show();
                                break;
                            case BluetoothMethods.STATE_NONE:
                                Toast.makeText(getApplicationContext(),
                                        "Doing Nothing", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;
                    case BluetoothMethods.MESSAGE_WRITE:
                        Toast.makeText(getApplicationContext(),
                                "Write to OutStream", Toast.LENGTH_SHORT).show();
                        break;
                    case MESSAGE_RESTART:
                        Toast.makeText(getApplicationContext(),
                                "Restart Listening", Toast.LENGTH_SHORT).show();
                        mbtService.restart();
                        break;
                    case WAIT_FOR_CONNECTION:
                        Toast.makeText(getApplicationContext(),
                                "Wait for Connection", Toast.LENGTH_SHORT).show();
                        break;
                    case GLASS_STOPPED:
                        Toast.makeText(getApplicationContext(),
                                "Restart Listening", Toast.LENGTH_SHORT).show();
                        mbtService.restart();
                        break;
                }
            }
        };
        return btHandler;
    }
    /**
     * On Button Clicked
     * Determine what to do when a button is clicked
     * Except one button menu --> has it's own Listeners
     */
    public void OnButtonClick(View v) {

        switch (v.getId()) {

            case R.id.buttonOneButton:
                Log.v(TAG, "One Button Menu");
                setContentView(R.layout.activity_main); // One button menu
                if(!CONNECTED) mbtService.restart(); // restart listening to incoming connections
                setButtonListeners();
                break;
            case R.id.buttonTwoButton:
                Log.v(TAG, "Two Button Menu");
                setContentView(R.layout.two_button_menu);
                if(!CONNECTED) mbtService.restart();
                break;
            case R.id.buttonSelect: // two button menu
                Log.v(TAG, "2 Buttons OK");
                sendToGlass(GLASS_OK);
                break;
            case R.id.buttonBack:
                Log.v(TAG, "2 Buttons Back");
                sendToGlass(GLASS_BACK);
                break;
        }
    }
}
