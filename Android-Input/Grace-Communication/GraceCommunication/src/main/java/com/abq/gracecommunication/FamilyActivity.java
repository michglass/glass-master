package com.abq.gracecommunication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

/*
    Created by Oliver
    Date: 02/19/2014
 */
public class FamilyActivity extends BaseMenuActivity {

    // Debug
    private static final String TAG = "Family Activity";

    // Glass Vars
    private CardScrollView mCardScrollView;
    private CardScroller mCardScrollAdapter;

    // Context for this class, to start message activity
    private final Context mContext = this;

    /*
        Family Activity lifetime cycle
     */
    // Create Family
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // create person list
        //TODO find better way to create person list
        ArrayList<Person> family = new ArrayList<Person>();
        Person mom = new Person("Mom", "1234");
        Person dad = new Person("Dad", "1234");
        family.add(mom); family.add(dad);

        // make cards out of persons and add to adapter
        mCardScrollAdapter = new CardScroller();
        mCardScrollAdapter.createCards(family, this);

        // create card scroll view and add adapter
        mCardScrollView = new CardScrollView(this);
        mCardScrollView.setAdapter(mCardScrollAdapter);

        // set up and display card scroll view
        mCardScrollView.activate();
        setContentView(mCardScrollView);

        // listens to "card taps"
        createCardClickListener(mContext);

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
    private void createCardClickListener(Context context) {
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private final Context context = mContext;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Start Message Activity
                Intent messageIntent = new Intent(context, MessagesActivity.class);
                startActivity(messageIntent);
                Card clickedCard = (Card) mCardScrollAdapter.getItem(i);
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
