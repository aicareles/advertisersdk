package cn.com.heaton.advertisersdk;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This class sets various static property values for Bluetooth
 * Created by liulei on 2016/11/29.
 */

public class AdvertiserStates {

    /**
     *  注释
     *  防止重复定义的常量值
     */
    @IntDef({
            StatusCode.UNPAIR,
            StatusCode.PAIRED,
            StatusCode.PAIRING,
            StatusCode.PAIRTIMEOUT
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusCode{
        int UNPAIR = 2502;//未配对
        int PAIRED = 2503;//配对成功
        int PAIRING = 2504;//配对中
        int PAIRTIMEOUT = 2505;//配对超时
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ResultCode{
        int WriteFailed = -1;
        int WriteSuccess = 0;
        int WriteCancel = -2;
        int BleNotOpen = -3;
        int PermissionError = -4;
        int Processing = -5;
        int NotFindDevice = -6;
        int Data_Command_Error = -7;
    }

}
