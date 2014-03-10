package com.abq.gracecommunication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;

/**
 *  Created by Oliver on 2/23/14.
 *
 *  When an option item gets selected this Activity is started
 *  Depending on which option item has been selected, the Cards get populated differently
 */
public class CategoryActivity extends BaseMenuActivity {

    // Debug
    private static final String TAG = "Category Activity";

    // variables to compare to extra passed by BaseMenuActivity
    private static final String IS_FAMILY = "family";
    private static final String IS_FRIENDS = "friends";

    // variable to hold the names of persons, later displayed on cards
    private ArrayList<String> personNames = new ArrayList<String>();

    // ArrayList of messages that gets passed to the MessagePersons Intent
    private ArrayList<String> mMessages = new ArrayList<String>();
    // extra value
    public static final String EXTRA_MESSAGES = "com.abq.gracecommunication.EXTRA_MESSAGES";

    // Glass Vars
    private CardScrollView mCardScrollView;
    private CardScroller mCardScrollAdapter;

    // Context for this class, to start message activity
    private final Context mContext = this;

    /*
        CategoryActivity lifetime cycle methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // get the extra sign from the intent
        String extraSign = getIntent().getStringExtra(EXTRA_CATEGORY);

        // set the persons
        Persons persons = new Persons();
        persons.setCurrentPersons();

        // set the messages that are being passed to MessagePersons intent
        Messages messages = new Messages();
        messages.setCurrentMessages();

        // depending on which menu item was selected, the corresponding
        // names are being set
        if(extraSign.compareTo(IS_FAMILY) == 0) {
            personNames = persons.getFamilyNames();
            mMessages = messages.getFamilyMessages();
        } else if(extraSign.compareTo(IS_FRIENDS) == 0) {
            personNames = persons.getFriendNames();
            mMessages = messages.getFriendsMessages();
        }

        // make cards out of persons and add to adapter
        mCardScrollAdapter = new CardScroller();
        mCardScrollAdapter.createCards(personNames, this, true);

        // create card scroll view and add adapter
        mCardScrollView = new CardScrollView(this);
        mCardScrollView.setAdapter(mCardScrollAdapter);

        // set up and display card scroll view
        mCardScrollView.activate();
        setContentView(mCardScrollView);

        // listens to "card taps"
        createCardClickListener();

        Log.v(TAG, "On Create");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "On Destroy");
    }

    /**
     * create listener for cards clicked in the view
     */
    private void createCardClickListener() {
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private final Context context = mContext;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Start Message Activity
                Intent messagePersonsIntent = new Intent(context, MessagePersons.class);
                messagePersonsIntent.putStringArrayListExtra(EXTRA_MESSAGES, mMessages);
                startActivity(messagePersonsIntent);

                Card clickedCard = (Card) mCardScrollAdapter.getItem(position);
                String cardText = clickedCard.getText();
                Log.v(TAG, "Card Name: " + cardText);
            }
        });
    }
    /**
     * when the user swipes down the option menu is opened again
     * and this activity gets closed
     */
    @Override
    public boolean onKeyDown(int keycode, KeyEvent keyevent) {
        if(keycode == KeyEvent.KEYCODE_BACK) {
            // open options menu if user wants to go back
            Intent mainActIntent = new Intent(this, MenuActivity.class);
            startActivity(mainActIntent);
            finish();
            return true;
        }
        return false;
    }
}
