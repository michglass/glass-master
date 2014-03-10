package com.abq.gracecommunication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

import java.util.LinkedList;
import java.util.Queue;

/*
    Created by Oliver
    02/21/2014
 */
public class MessagesActivity extends Activity {

    // Debug
    private static final String TAG = "Messages Activity";

    // Glass Vars
    private CardScrollView mCardScrollView;
    private CardScroller mCardScrollAdapter;

    // Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Create messages to be displayed
        Messages messages = new Messages();
        messages.addMessage("I need your help");
        messages.addMessage("I am hungry");

        // new CardScrollAdapter
        mCardScrollAdapter = new CardScroller();
        mCardScrollAdapter.createCards(messages.getMessages(), this);

        // init card scroll view
        mCardScrollView = new CardScrollView(this);
        mCardScrollView.setAdapter(mCardScrollAdapter);

        // set up and display card scroll view
        mCardScrollView.activate();
        setContentView(mCardScrollView);

        // init on click listener
        createCardClickListener(this);

        Log.v(TAG, "On create");
    }

    /**
     * create an listener that initiates sending the message when
     * a message card is being clicked
     */
    public void createCardClickListener(Context co) {
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Card clickedMessage = (Card) mCardScrollAdapter.getItem(position);
                Log.v(TAG, "Message: " + clickedMessage.getText());
            }
        });
    }

    /**
     * Private Class that holds messages
     */
    private class Messages {

        // Debug
        private static final String TAG = "Messages Class";

        // Message Queue
        Queue<String> messages;

        // Constructor initializes the message list
        public Messages() {
            this.messages = new LinkedList<String>();
        }
        // add new message
        public void addMessage(String msg) {
            this.messages.add(msg);
        }
        // get message list
        public Queue<String> getMessages() {
            return this.messages;
        }
    }
}
