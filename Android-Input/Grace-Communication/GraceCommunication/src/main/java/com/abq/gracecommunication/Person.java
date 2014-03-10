package com.abq.gracecommunication;

/**
 * Created by Oliver
 * Date: 02/20/2014
 * Class that holds information about Persons: Friends, Family
 * can be used to populate Cards, use for messaging
 */
public class Person {

    // Member Fields
    private String mName;
    private String mPhoneNumber;
    // Image variable, and a couple of others

    /**
     * Constructor initializes the member variables
     */
    public Person(String name, String phoneNumber) {
        this.mName = name;
        this.mPhoneNumber = phoneNumber;
    }
    // getters and setters
    public String getName() {
        return mName;
    }
    public String getPhoneNumber() {
        return mPhoneNumber;
    }
}
