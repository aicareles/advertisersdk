package com.heaton.advertisersdk;

import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

/**
 * 蓝牙日志类
 * Created by LiuLei on 2017/5/16.
 */

public class AdvertiserLog {

    public static String TAG = "AdvertiserSDK";

    private static boolean isDebug;

    public static void init(){
        AdvertiserConfig config = AdvertiserConfig.config();
        isDebug = config.isLogAvertiseExceptions();
        String logTAG = config.getLogTAG();
        if (!TextUtils.isEmpty(logTAG))
            TAG = logTAG;
    }

    private static String getSubTag(Object o){
        String tag = "";
        if(o instanceof String){
            tag = (String) o;
        }else if(o instanceof Number){
            tag = String.valueOf(o);
        }else {
            tag = o.getClass().getSimpleName();
        }
        return tag;
    }

    public static void e(Object o, String msg){
        if(isDebug){
            Log.e(TAG,buildMessge(getSubTag(o), msg));
        }
    }

    public static void i(Object o, String msg){
        if(isDebug){
            Log.i(TAG,buildMessge(getSubTag(o), msg));
        }
    }

    public static void w(Object o, String msg){
        if(isDebug){
            Log.w(TAG,buildMessge(getSubTag(o), msg));
        }
    }

    public static void d(Object o, String msg){
        if(isDebug){
            Log.d(TAG,buildMessge(getSubTag(o), msg));
        }
    }

    private static String buildMessge(String subTag, String msg){
        return String.format(Locale.CHINA, "[%d] %s: %s",
                Thread.currentThread().getId(), subTag, msg);
    }

}
