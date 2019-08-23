package cn.com.heaton.advertisersdk.proxy;

import cn.com.heaton.advertisersdk.AdvertiserDevice;
import cn.com.heaton.advertisersdk.callback.AdvertiserScanCallback;
import cn.com.heaton.advertisersdk.callback.AdvertiserDiscoverCallback;
import cn.com.heaton.advertisersdk.callback.AdvertiserConnectCallback;

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
