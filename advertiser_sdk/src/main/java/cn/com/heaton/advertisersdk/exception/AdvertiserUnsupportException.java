package cn.com.heaton.advertisersdk.exception;

/**
 * Created by LiuLei on 2017/10/19.
 */

public class AdvertiserUnsupportException extends AdvertiserException {


    private static final long serialVersionUID = 3871013343556227444L;

    public AdvertiserUnsupportException() {
    }

    public AdvertiserUnsupportException(String message) {
        super(message);
    }

    public AdvertiserUnsupportException(String s, Throwable ex) {
        super(s, ex);
    }
}
