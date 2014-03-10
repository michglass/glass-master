package com.abq.gracecommunication;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Oliver
 * Date: 02/20/2014
 * Class that holds information about Persons: Friends, Family
 * can be used to populate Cards, use for messaging
 */
public class Persons {
    //TODO might not be the best way...maybe just a person class which has info about the person
    //TODO and set up the lists differently

    // Debug
    private static final String TAG = "Persons";

    // Member Fields
    private ArrayList<Person> mFamilyList;
    private ArrayList<Person> mFriendsList;
    private ArrayList<String> mFamilyNames;
    private ArrayList<String> mFriendNames;

    // Constructor initializes the message lists
    public Persons() {
        this.mFamilyList = new ArrayList<Person>();
        this.mFriendsList = new ArrayList<Person>();
        this.mFamilyNames = new ArrayList<String>();
        this.mFriendNames = new ArrayList<String>();
    }

    // add new person to different lists
    public void addPersonToFamily(Person p) { this.mFamilyList.add(p); }
    public void addPersonToFriends(Person p) { this.mFriendsList.add(p); }
    // add name to name list
    public void addNameToFamily(String n) { this.mFamilyNames.add(n); }
    public void addNameToFriends(String n) { this.mFriendNames.add(n); }

    // get person lists
    public ArrayList<Person> getFamilyList() { return this.mFamilyList; }
    public ArrayList<Person> getFriendsList() { return this.mFriendsList; }

    // get persons names
    public ArrayList<String> getFamilyNames() { return this.mFamilyNames; }
    public ArrayList<String> getFriendNames() { return this.mFriendNames; }

    // set current persons
    public void setCurrentPersons() {
        // Family members
        Person mom = new Person("Mom", "1234");
        Person dad = new Person("Dad", "1234");
        this.addPersonToFamily(mom);
        this.addPersonToFamily(dad);
        this.addNameToFamily("Mom");
        this.addNameToFamily("Dad");

        // Friends messages
        Person james = new Person("James", "1234");
        Person ann = new Person("Ann", "1234");
        this.addPersonToFriends(james);
        this.addPersonToFriends(ann);
        this.addNameToFriends("James");
        this.addNameToFriends("Ann");
    }

    /**
     * private person class
     */
    private class Person {

        private String name;
        private String phoneNumber;

        // Constructor
        public Person(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }
    }
}
