package cn.com.heaton.advertisersdk.interceptor;

import cn.com.heaton.advertisersdk.config.AdvertiserConfig;

public class RealInterceptorHandler implements Interceptor.InterceptorHandler {

    private Interceptor interceptor;
    private Request request = new Request();
    private Response response = new Response();
    private static RealInterceptorHandler interceptorHandler;

    private RealInterceptorHandler(){
        interceptor = AdvertiserConfig.config().getInterceptor();
    }

    public static RealInterceptorHandler getInstance(){
        if (interceptorHandler == null){
            interceptorHandler = new RealInterceptorHandler();
        }
        return interceptorHandler;
    }

    @Override
    public Request request(byte[] payload) {
        if (interceptor != null){
            request.parse(payload);
            interceptor.request(request);
        }
        return request;
    }

    @Override
    public Response response(byte[] payload) {
        if (interceptor != null){
            response.parse(payload);
            interceptor.response(response);
        }
        return response;
    }

}
