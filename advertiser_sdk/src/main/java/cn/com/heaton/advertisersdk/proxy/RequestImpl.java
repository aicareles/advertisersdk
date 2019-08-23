package cn.com.heaton.advertisersdk.proxy;

import cn.com.heaton.advertisersdk.AdvertiserDevice;
import cn.com.heaton.advertisersdk.callback.AdvertiserScanCallback;
import cn.com.heaton.advertisersdk.callback.AdvertiserDiscoverCallback;
import cn.com.heaton.advertisersdk.callback.AdvertiserConnectCallback;
import cn.com.heaton.advertisersdk.request.AdvertiserRequest;
import cn.com.heaton.advertisersdk.request.Rproxy;
import cn.com.heaton.advertisersdk.request.ScanRequest;

/**
 *
 * Created by LiuLei on 2017/10/30.
 */

public class RequestImpl<T extends AdvertiserDevice> implements RequestLisenter<T>{

    private static RequestImpl instance = new RequestImpl();

    public static RequestImpl getRequestImpl(){
        return instance;
    }

    @Override
    public void startScan(AdvertiserScanCallback<T> callback) {
        ScanRequest<T> request = Rproxy.getProxy().getRequest(ScanRequest.class);
        request.startScan(callback);
    }

    @Override
    public void startScan() {
        ScanRequest<T> request = Rproxy.getProxy().getRequest(ScanRequest.class);
        request.startScan();
    }

    @Override
    public void stopScan() {
        ScanRequest request = Rproxy.getProxy().getRequest(ScanRequest.class);
        request.stopScan();
    }

    @Override
    public void startAvertise(byte[] payload) {
        AdvertiserRequest request = Rproxy.getProxy().getRequest(AdvertiserRequest.class);
        request.startAdvertising(payload);
    }

    @Override
    public void startAvertise(byte[] payload, int bleChannel) {
        AdvertiserRequest request = Rproxy.getProxy().getRequest(AdvertiserRequest.class);
        request.startAdvertising(payload, bleChannel);
    }

    @Override
    public void stopAvertise() {
        AdvertiserRequest request = Rproxy.getProxy().getRequest(AdvertiserRequest.class);
        request.stopAdvertising();
    }

    @Override
    public void preparePair(AdvertiserDiscoverCallback lisenter) {
        AdvertiserRequest request = Rproxy.getProxy().getRequest(AdvertiserRequest.class);
        request.discoverDevice(lisenter);
    }

    @Override
    public void startPair(AdvertiserDevice device, AdvertiserConnectCallback lisenter) {
        AdvertiserRequest request = Rproxy.getProxy().getRequest(AdvertiserRequest.class);
        request.connectDevice(device, lisenter);
    }
}
