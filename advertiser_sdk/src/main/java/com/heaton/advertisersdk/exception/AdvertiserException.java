package com.heaton.advertisersdk.exception;

import java.io.Serializable;


/**
 *
 * Created by LiuLei on 2017/10/19.
 */

public class AdvertiserException extends Exception implements Serializable{

    private static final long serialVersionUID = -3677084962477320584L;

    public static final int ERROR_CODE_NOTSUPPORT = 400;
    public static final int ERROR_CODE_PERMISSION = 401;
//    public static final int ERROR_CODE_OTHER = 402;
//    public static final int ERROR_CODE_NOT_FOUND_DEVICE = 403;
//    public static final int ERROR_CODE_BLUETOOTH_NOT_ENABLE = 404;
//    public static final int ERROR_CODE_SCAN_FAILED = 405;

    private Throwable ex;

    public AdvertiserException(){
        super();
    }

    public AdvertiserException(String message){
        super(message);
    }

    public AdvertiserException(String s, Throwable ex) {
        super(s, null);  //  Disallow initCause
        this.ex = ex;
    }

    public AdvertiserException(Throwable cause) {
        super(cause);
    }

    public Throwable getException() {
        return ex;
    }

    public Throwable getCause() {
        return ex;
    }
}
