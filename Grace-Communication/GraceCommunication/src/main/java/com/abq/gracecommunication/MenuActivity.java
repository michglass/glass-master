package com.abq.gracecommunication;


import android.os.Bundle;
import android.util.Log;

/*
    Created By Oliver
    02/19/2014
 */
public class MenuActivity extends BaseMenuActivity {

    // Debug
    private static final String TAG = "Main Activity";
    /*
        Activity Lifecycle Methods
     */
    // Create Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // open the options menu from the base class
        super.openOptionsMenu();

        Log.v(TAG, "On Create");
    }

    // Activity in foreground
    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "On Resume");
    }
    // Activity not in foreground
    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "On Pause");
    }
    // Activity not visible
    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "On Stop");
    }
    // Destroy Activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "On Destroy");
    }
}
