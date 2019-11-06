package com.heaton.advertisersdk;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 *
 * Created by LiuLei on 2017/10/30.
 */

public class AdvertiserHandler extends Handler {
    private static final String TAG = "AdvertiserHandler";
    private static AdvertiserHandler sHandler;//Handler for manipulating the Ble state

    public static AdvertiserHandler getHandler(){
        synchronized (AdvertiserHandler.class){
            if(sHandler == null){
                HandlerThread handlerThread = new HandlerThread("handler thread");
                handlerThread.start();
                sHandler = new AdvertiserHandler(handlerThread.getLooper());
            }
            return sHandler;
        }
    }

    private AdvertiserHandler(Looper looper){
        super(Looper.myLooper());
    }

}
