package com.MessageTest.app;

import android.content.Intent;
import android.net.Uri;


import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by vbganesh on 3/16/14.
 */
public class Message {
    private String Number;
    private String Message;
    Message(String num, String mess){
        Number = num;
        Message = mess;
    }

}
