package com.redfox.nimbustodo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.redfox.nimbustodo.network.MyHandlerCallBack;
import com.redfox.nimbustodo.network.MyHandlerThread;
import com.redfox.nimbustodo.network.NetworkObserverCallBack;


public class ConstantNetMonitorReceiver extends BroadcastReceiver implements MyHandlerCallBack {

    private final static String TAG = ConstantNetMonitorReceiver.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    //A Continuous net change detector
    private NetworkObserverCallBack mObserverCallBack;

    private MyHandlerThread myHandlerThread;


    public ConstantNetMonitorReceiver(NetworkObserverCallBack mObserverCallBack) {
        this.mObserverCallBack = mObserverCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (LOG_DEBUG)
            Log.i(TAG, " onReceive()");
        myHandlerThread = new MyHandlerThread(this, context.getApplicationContext());
        myHandlerThread.start();

    }


    @Override
    public void isNetAvailable(boolean isAvailable) {

        if (LOG_DEBUG)
            Log.i(TAG, " isNetAvailable()");

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (LOG_DEBUG)
                System.out.println(TAG + " : UI ");
        } else {
            if (LOG_DEBUG)
                System.out.println(TAG + " : NON UI");
        }
        mObserverCallBack.isContinuousNetCheck(isAvailable);
    }

    public void quitHandlerThread() {
        if (myHandlerThread.mhtHandler != null) {
            myHandlerThread.mhtHandler.removeCallbacks(null);
            myHandlerThread.mhtHandler.getLooper().quit();
            Thread thread = myHandlerThread;
            thread.interrupt();
            myHandlerThread = null;
        }
    }
}