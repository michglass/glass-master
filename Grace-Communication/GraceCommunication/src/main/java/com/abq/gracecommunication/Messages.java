package com.abq.gracecommunication;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Oliver
 * 02/22/2014
 *
 * This class holds specified messages that Grace
 * can send to different categories of people
 * (friends, family) --> maybe later split further
 * family --> parents, grandparents, siblings
 */
public class Messages {
     /*
        TODO Create response option! e.g. Mom sends message: "how was school?"
        TODO Some version of this activity pops up and she can reply stuff like
        TODO "good","bad","yes","no","at 10.30"

        TODO Down the road add option: "Add message" so Grace and her family can add a new message themselves
    */

    // Debug
    private static final String TAG = "Messages Class";

    // Message Queues for different contact categories
    ArrayList<String> mFamilyMessages;
    ArrayList<String> mFriendsMessages;

    // Constructor initializes the message lists
    public Messages() {
        this.mFamilyMessages = new ArrayList<String>();
        this.mFriendsMessages = new ArrayList<String>();
    }
    // add new message to different lists
    public void addMessageToFamily(String msg) {
        this.mFamilyMessages.add(msg);
    }
    public void addMessageToFriends(String msg) {
        this.mFriendsMessages.add(msg);
    }

    // get messages lists
    public ArrayList<String> getFamilyMessages() { return this.mFamilyMessages; }
    public ArrayList<String> getFriendsMessages() { return this.mFriendsMessages; }

    // current predefined messages
    public void setCurrentMessages() {
        // Family messages
        this.addMessageToFamily("I need your help");
        this.addMessageToFamily("I am hungry");
        this.addMessageToFamily("Can we go outside");
        this.addMessageToFamily("I need to go to the bathroom");
        this.addMessageToFamily("Can you help me with some homework");

        // Friends messages
        this.addMessageToFriends("Do you want to meet up later?");
        this.addMessageToFriends("Do you want to do something after school?");
        this.addMessageToFriends("What do you want to play?");
    }
}
