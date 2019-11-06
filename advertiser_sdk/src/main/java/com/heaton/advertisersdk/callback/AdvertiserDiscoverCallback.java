package com.heaton.advertisersdk.callback;

import com.heaton.advertisersdk.AdvertiserDevice;

/**
 * description $desc$
 * created by jerry on 2018/11/27.
 */

public abstract class AdvertiserDiscoverCallback {

    public void onDiscoverStart(){}
    public abstract void onAvertiseScan(AdvertiserDevice device);
    public void onDiscoverStop(){}

}
