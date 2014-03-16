package com.abq.servicetest.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import junit.framework.Test;

/**
 * Created by Oliver on 3/12/14.
 */
public class TestService extends Service {

    // Debug
    public static final String TAG = "Test Service";

    /**
     * Service Stuff
     */
    // number of components bound to service
    public static int BOUND_COUNT = 0;

    // Messages From Client
    public static final int GLASS_OK = 1;
    public static final int GLASS_BACK = 2;
    public static final int REGISTER_CLIENT = 3;
    public static final int UNREGISTER_CLIENT = 4;

    // Messages to Client
    public static final int TEXT_GLASS = 5;
    public static final int PICTURE_GLASS = 6;

    /**
     * Service Stuff
     */
    // Messenger that gets published to client
    private final Messenger serviceMessenger = new Messenger(new ClientHandler());

    /**
     * Service Stuff
     */
    // Client Messenger for sending messages to client
    private Messenger mClientMessenger;

    /**
     * Service Stuff
     */
    @Override
    public void onCreate() {
        Log.v(TAG, "Create Service");
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "On Bind");
        return serviceMessenger.getBinder();
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "On Unbind");

        return true;
    }
    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "On Rebind");
        super.onRebind(intent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "On Start Command: " + startId);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroy Service");
        super.onDestroy();
    }

    /**
     * Service Stuff
     */
    private class ClientHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case REGISTER_CLIENT:
                    Log.v(TAG, "Register Client");
                    TestService.BOUND_COUNT++;
                    Log.v(TAG, "Bound Clients: " + TestService.BOUND_COUNT);
                    mClientMessenger = msg.replyTo;

                    sendMessageToClient(TestService.TEXT_GLASS);
                    break;
                case UNREGISTER_CLIENT:
                    Log.v(TAG, "Unregister Client");
                    TestService.BOUND_COUNT--;
                    Log.v(TAG, "Bound Clients: " + TestService.BOUND_COUNT);
                    break;
                case GLASS_OK:
                    Log.v(TAG, "Glass OK: " + GLASS_OK);
                    // send sth to glass
                    sendMessageToClient(TestService.PICTURE_GLASS);
                    break;
                case GLASS_BACK:
                    Log.v(TAG, "Glass Back: " + GLASS_BACK);
                    // send sth to glass;
                    break;
            }
        }
    }

    /**
     * Service Stuff
     */
    public void sendMessageToClient(int clientMsg) {
        Message msg = new Message();
        msg.what = clientMsg;

        try {
            mClientMessenger.send(msg);
        } catch (RemoteException remE) {
            Log.e(TAG, "Couldn't contact Client");
        }
    }

    public void method1() {
        Log.v(TAG, "Method 1");
    }
    public void method2() {
        Log.v(TAG, "Method 2");
    }



}
