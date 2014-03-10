package com.abq.gracecommunication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/*
    Created by Oliver
    Date: 02/19/2014

    This class implements the
    Option Menu methods. Multiple activities can use this class to show the same menu
 */
public class BaseMenuActivity extends Activity {

    // Debug
    private static final String TAG = "Base Activity";

    // Intents for the activities that get started by the menu
    private Intent familyIntent;
    private Intent friendsIntent;

    /*
        Option Menu Lifecycle methods
     */

    /**
     * method to show the menu, called from openOptionsMenu()
     * @param menu
     * @return boolean indicates if menu got inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.common_menu, menu);

        Log.v(TAG, "On Opt Menu");
        return true;
    }
    /**
     * when a menu item gets pressed this method is called
     * @param item menu item that the user pressed
     * @return boolean indicates if click has been processed successfully
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "On Options Item Selected");
        int itemId = item.getItemId();
        switch(itemId) {

            case R.id.familyItem:
                familyIntent = new Intent(this, FamilyActivity.class);
                startActivity(familyIntent);
                Log.v(TAG, "Family Item Selected");
                return true;
            case R.id.friendsItem:
                friendsIntent = new Intent(this, FriendsActivity.class);
                startActivity(friendsIntent);
                Log.v(TAG, "Friends Item Selected");
                return true;
        }
        return false;
    }

    /**
     * When menu gets closed by swiping down
     * @param menu that gets closed
     * @return void
     */
    // Close Menu
    @Override
    public void onOptionsMenuClosed(Menu menu) {

        super.onOptionsMenuClosed(menu);
        Log.v(TAG, "On Opt Menu Closed");

        // when menu is closed, close activity as well
        // because the first thing of the activity is the menu
        finish();
    }
}
