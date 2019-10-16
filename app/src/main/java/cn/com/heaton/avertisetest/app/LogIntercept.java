package cn.com.heaton.avertisetest.app;

import android.util.Log;

import cn.com.heaton.advertisersdk.interceptor.Interceptor;
import cn.com.heaton.advertisersdk.interceptor.Request;
import cn.com.heaton.advertisersdk.interceptor.Response;

public class LogIntercept implements Interceptor {
    private static final String TAG = "LogIntercept";

    @Override
    public void request(Request request) {
        Log.e(TAG, "request: "+request.toString());
    }

    @Override
    public void response(Response response) {
        Log.e(TAG, "response: "+response.toString());
    }
}
