package cn.com.heaton.avertisetest.app;

import android.util.Log;

import com.heaton.advertisersdk.interceptor.Interceptor;
import com.heaton.advertisersdk.interceptor.Payload;

public class LogIntercept implements Interceptor {
    private static final String TAG = "LogIntercept";

    @Override
    public void request(Payload request) {
        Log.e(TAG, "request: "+request.toString());
    }

    @Override
    public void response(Payload response) {
        Log.e(TAG, "response: "+response.toString());
    }
}
