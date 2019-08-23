package cn.com.heaton.advertisersdk.callback;

import cn.com.heaton.advertisersdk.AdvertiserDevice;

/**
 * description $desc$
 * created by jerry on 2018/11/27.
 */

public interface AdvertiserConnectCallback {

    void onAvertiseConnected(AdvertiserDevice device);

    void onAvertiseConnectTimeOut();
}
