package com.heaton.advertisersdk.proxy;

import android.content.Context;

import com.heaton.advertisersdk.request.Rproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.heaton.advertisersdk.AdvertiserLog;

/**
 *
 * Created by LiuLei on 2017/9/1.
 */

public class RequestProxy implements InvocationHandler{
    private static final String TAG = "RequestProxy";

    private Object receiver;

    private static RequestProxy instance = new RequestProxy();


    public static RequestProxy getRequestProxy(){
        return instance;
    }

    //Bind the delegate object and return the proxy class
    public Object bindProxy(Context context, Object tar){
        this.receiver = tar;
        //绑定委托对象，并返回代理类
        AdvertiserLog.e(TAG, "bindProxy: "+"Binding agent successfully");
        Rproxy.getProxy().init(context);
        return Proxy.newProxyInstance(
                tar.getClass().getClassLoader(),
                tar.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(receiver, args);
    }
}
