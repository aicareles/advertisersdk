package com.heaton.advertisersdk.interceptor;

import com.heaton.advertisersdk.AdvertiserConfig;

public class RealInterceptorHandler implements Interceptor.InterceptorHandler {

    private Interceptor interceptor;
    private ParseStrategy strategy;
    private Payload payload = new Payload();

    private RealInterceptorHandler(){
        AdvertiserConfig config = AdvertiserConfig.config();
        interceptor = config.getInterceptor();
        strategy = config.getParseStrategy();
    }

    public final static ThreadLocal<RealInterceptorHandler> sInterceptorHandler = new ThreadLocal<>();

    public static RealInterceptorHandler getInstance() {
        if (sInterceptorHandler.get() == null) {
            sInterceptorHandler.set(new RealInterceptorHandler());
        }
        return sInterceptorHandler.get();
    }

    @Override
    public Payload request(byte[] payload) {
        if (interceptor != null){
            this.payload.parse(payload, strategy==null ? null : strategy.reqStrategy());
            interceptor.request(this.payload);
        }
        return this.payload;
    }

    @Override
    public Payload response(byte[] payload) {
        if (interceptor != null){
            this.payload.parse(payload, strategy==null ? null : strategy.resStrategy());
            interceptor.response(this.payload);
        }
        return this.payload;
    }

}
