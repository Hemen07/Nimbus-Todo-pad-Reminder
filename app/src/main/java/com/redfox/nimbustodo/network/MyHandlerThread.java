package com.redfox.nimbustodo.network;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.redfox.nimbustodo.weather.weather_util.UtilNetworkDetect;


public class MyHandlerThread extends HandlerThread {
    private final static String TAG = MyHandlerThread.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;

    public Handler mhtHandler;

    private MyHandlerCallBack myHandlerCallBack;
    private Context context;


    public MyHandlerThread(MyHandlerCallBack myHandlerCallBack, Context context) {
        super("MyHandlerThread");
        if (LOG_DEBUG)
            Log.e(TAG, "MyHandlerThread");
        this.myHandlerCallBack = myHandlerCallBack;
        this.context = context;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (LOG_DEBUG)
                System.out.println("HT " + "UI");
        } else {
            if (LOG_DEBUG)
                System.out.println("HT " + "NON UI");
        }

        final boolean netStatus = checkNet();
        myHandlerCallBack.isNetAvailable(netStatus);

        mhtHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (LOG_DEBUG)
                    Log.w(TAG, "inside handleMessage()");
                return true;
            }
        }) {
        };
    }

    private boolean checkNet() {

        boolean status = UtilNetworkDetect.isOnline(context.getApplicationContext());
        if (status == true) {

            if (LOG_DEBUG)
                Log.w(TAG, " Online  ........... ");

        } else {

            if (LOG_DEBUG)
                Log.w(TAG, " -----------  Offline");

        }
        return status;
    }


}
