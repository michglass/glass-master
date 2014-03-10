package com.abq.gracecommunication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;

/**
 * Created by Oliver
 * Date: 02/23/2014
 */
public class MessagePersons extends Activity {

    // Debug
    private static final String TAG = "Message Persons";

    // Glass vars
    private CardScrollView mCardScrollView;
    private CardScroller mCardScrollAdapter;

    // messages
    private ArrayList<String> mMessages = new ArrayList<String>();

    /**
     * onCreate gets a list of specific messages she can send to her family
     * from the message class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // get the messages from the category intent
        mMessages = getIntent().getStringArrayListExtra(CategoryActivity.EXTRA_MESSAGES);

        // set up card scroll adapter
        mCardScrollAdapter = new CardScroller();
        mCardScrollAdapter.createCards(mMessages, this);

        // init the card scroll view
        mCardScrollView = new CardScrollView(this);
        mCardScrollView.setAdapter(mCardScrollAdapter);

        // set up and display card scroll view
        mCardScrollView.activate();
        setContentView(mCardScrollView);

        // set up the listener
        createCardClickListener();

        Log.v(TAG, "On Create");
    }
    /**
     * activity gets destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "On Destroy");
    }

    /**
     * create an listener that initiates sending the message when
     * a message card is being clicked
     */
    public void createCardClickListener() {
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Card clickedMessage = (Card) mCardScrollAdapter.getItem(position);
                Log.v(TAG, "Message: " + clickedMessage.getText());
            }
        });
    }
}
