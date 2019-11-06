package com.heaton.advertisersdk.proxy;

import com.heaton.advertisersdk.callback.AdvertiserConnectCallback;
import com.heaton.advertisersdk.callback.AdvertiserDiscoverCallback;
import com.heaton.advertisersdk.callback.AdvertiserScanCallback;
import com.heaton.advertisersdk.AdvertiserDevice;

/**
 *
 * Created by LiuLei on 2017/10/30.
 */

public interface RequestLisenter<T> {

    void startScan(AdvertiserScanCallback<T> callback);

    void startScan();

    void stopScan();

    void startAvertise(byte[] payload);

    void startAvertise(byte[] payload, int bleChannel);

    void stopAvertise();

    void preparePair(AdvertiserDiscoverCallback lisenter);

    void startPair(AdvertiserDevice device, AdvertiserConnectCallback lisenter);

}
