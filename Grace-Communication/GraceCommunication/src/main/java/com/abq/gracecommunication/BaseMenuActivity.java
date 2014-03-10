package com.abq.gracecommunication;

import android.app.Activity;
import android.content.Intent;
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

    // Intent to start the category activity
    private Intent mCategoryIntent;

    // Extra variable that indicates if family or friends was pressed
    public static final String EXTRA_CATEGORY = "com.abq.gracecommunication.EXTRA_CATEGORY";

    /*
        Option Menu Lifecycle methods
     */

    /**
     * method to show the menu, called from openOptionsMenu()
     * @param menu Menu
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

        // set up Intent to start the CategoryActivity
        mCategoryIntent = new Intent(this, CategoryActivity.class);

        int itemId = item.getItemId();
        switch(itemId) {

            case R.id.familyItem:
                // if family item is selected put the extra sign for family
                mCategoryIntent.putExtra(EXTRA_CATEGORY, "family");
                startActivity(mCategoryIntent);
                Log.v(TAG, "Family Item Selected");
                return true;
            case R.id.friendsItem:
                // if friends item is selected put extra sign for friends
                mCategoryIntent.putExtra(EXTRA_CATEGORY, "friends");
                startActivity(mCategoryIntent);
                Log.v(TAG, "Friends Item Selected");
                return true;
        }
        return false;
    }

    /**
     * When menu gets closed by swiping down
     * @param menu that gets closed
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
