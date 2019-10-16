package cn.com.heaton.advertisersdk.interceptor;

public interface Interceptor {

    void request(Request request);

    void response(Response response);


    interface InterceptorHandler {
        Request request(byte[] payload);

        Response response(byte[] payload);
    }
}
