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

/*
    Created by Oliver
    Date: 02/19/2014
 */
public class FriendsActivity extends BaseMenuActivity {

    // Debug
    private static final String TAG = "Friends Activity";

    // Glass Var
    private CardScrollView mCardScrollView;
    private CardScroller mCardScrollAdapter;

    // Context passed to intent to start message activity
    Context mContext = this;

    /*
        Activity Lifecycle Methods
     */
    // Create Friends Activity
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // create person list
        //TODO find better way to create person list
        ArrayList<Person> friends = new ArrayList<Person>();
        Person mom = new Person("James", "1234");
        Person dad = new Person("Ann", "1234");
        friends.add(mom); friends.add(dad);

        // make cards out of persons and add to adapter
        mCardScrollAdapter = new CardScroller();
        mCardScrollAdapter.createCards(friends, this);

        // create card scroll view and add adapter
        mCardScrollView = new CardScrollView(this);
        mCardScrollView.setAdapter(mCardScrollAdapter);

        // set up and display card scroll view
        mCardScrollView.activate();
        setContentView(mCardScrollView);

        // listen to "card taps"
        createCardClickListener(this);

        Log.v(TAG, "On Create");
    }
    // Destroy Friends ACt
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "On Destroy");
    }

    /**
     * create listener for cards clicked in the view
     */
    private void createCardClickListener(Context context) {
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Context context = mContext;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Start Message Activity
                Intent messageIntent = new Intent(context, MessagesActivity.class);
                startActivity(messageIntent);
                Card clickedCard = (Card) mCardScrollAdapter.getItem(position);
                String cardText = clickedCard.getText();
                Log.v(TAG, "Card Name: " + cardText);
            }
        });
    }
    /**
     * when the user swipes down the option menu is opened again
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
