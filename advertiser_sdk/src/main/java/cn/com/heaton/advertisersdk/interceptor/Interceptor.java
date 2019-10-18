package cn.com.heaton.advertisersdk.interceptor;

public interface Interceptor {

    void request(Payload request);

    void response(Payload response);


    interface InterceptorHandler {
        Payload request(byte[] payload);

        Payload response(byte[] payload);
    }
}
