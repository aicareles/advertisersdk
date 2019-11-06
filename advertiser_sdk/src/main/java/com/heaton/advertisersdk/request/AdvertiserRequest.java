package com.heaton.advertisersdk.request;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Handler;

import com.heaton.advertisersdk.AdvertiserConfig;
import com.heaton.advertisersdk.AdvertiserDevice;
import com.heaton.advertisersdk.AdvertiserHandler;
import com.heaton.advertisersdk.AdvertiserLog;
import com.heaton.advertisersdk.callback.AdvertiserConnectCallback;
import com.heaton.advertisersdk.callback.AdvertiserDiscoverCallback;
import com.heaton.advertisersdk.exception.AdvertiserUnsupportException;
import com.heaton.advertisersdk.interceptor.RealInterceptorHandler;
import com.heaton.advertisersdk.utils.ByteUtils;
import com.heaton.advertisersdk.utils.TaskExecutor;

import java.util.Random;

import wireless.algorithm.io.cshsoft.a2_4gcrytonlib.WirelessEncoder;

/**
 * description $desc$
 * created by jerry on 2018/11/27.
 */

@Implement(AdvertiserRequest.class)
public class AdvertiserRequest<T extends AdvertiserDevice> {
    private static final String TAG = "AdvertiserRequest";

    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser advertiser;
    private AdvertiseSettings advertiseSettings;
    private AdvertiseData advertiseData;
    private final static int DEFAULT_CHANNEL = 37;
    private byte[] calculatedPayload = new byte[24];//最大24个byte
    private byte[] pairload = new byte[16];//对码装载数据  16byte

    protected AdvertiserRequest() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        handler = AdvertiserHandler.getHandler();
        if (advertiser == null) {
            try {
                throw new AdvertiserUnsupportException("Device does not support Avertise!");
            } catch (AdvertiserUnsupportException e) {
                e.printStackTrace();
            }
        }
        //设置频率:  ADVERTISE_MODE_LOW_LATENCY 100ms     ADVERTISE_MODE_LOW_POWER 1s     ADVERTISE_MODE_BALANCED  250ms
        advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)//设置广播间隔100ms
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .build();
    }

    public void startAdvertising(final byte[] payload, final int bleChannel){
        handler.removeCallbacks(stopAvertiseRunnable);
        if(advertiser != null && bluetoothAdapter.isEnabled()){
            TaskExecutor.executeTask(new Runnable() {
                @Override
                public void run() {
                    advertiser.stopAdvertising(mAdvertiseCallback);
                    byte[] tempPayload = new byte[16];
                    System.arraycopy(payload, 0, tempPayload, 0, tempPayload.length);
                    AdvertiserLog.e(TAG, "加密前的数据: "+ ByteUtils.byteArrayToHexStr(tempPayload));
                    RealInterceptorHandler.getInstance().request(tempPayload);
                    WirelessEncoder.cipher(tempPayload, true);
                    WirelessEncoder.payload(bleChannel, tempPayload, calculatedPayload);
                    advertiseData = new AdvertiseData.Builder()
                            .addManufacturerData(65520, calculatedPayload)
                            .build();
                    advertiser.startAdvertising(advertiseSettings, advertiseData, mAdvertiseCallback);
                }
            });
        }
    }

    public void startAdvertising(byte[] payload) {
        startAdvertising(payload, DEFAULT_CHANNEL);
    }

    public void stopAdvertising() {
        if(advertiser != null){
            TaskExecutor.executeTask(new Runnable() {
                @Override
                public void run() {
                    advertiser.stopAdvertising(mAdvertiseCallback);
                    AdvertiserLog.e(TAG, "广播已关闭");
                }
            });
        }
    }

    public void stopAdvertising(Long delay){
        handler.postDelayed(stopAvertiseRunnable, delay);
    }

    private Runnable stopAvertiseRunnable = new Runnable() {
        @Override
        public void run() {
            stopAdvertising();
        }
    };

    private void resetDiscover(AdvertiserDevice device){
        pairload[0] = (byte) (new Random().nextInt(256) & 0xff);//随机数
        byte[] avertiseProductCode = AdvertiserConfig.config().getAvertiseProductCode();
        System.arraycopy(avertiseProductCode, 0, pairload, 1, avertiseProductCode.length);
        if(null != device){
            if(null != device.getRollingCode()){
                byte[] rolling_code = device.getRollingCode();
                System.arraycopy(rolling_code, 0, pairload, 6, rolling_code.length);
            }
        }
        System.arraycopy(AdvertiserConfig.generateRandom(), 0, pairload, 13, 3);
    }

    public void discoverDevice(AdvertiserDiscoverCallback lisenter){
        resetDiscover(null);
        pairload[4] = (byte) 0xC1;
        ParseRequest<T> request = Rproxy.getProxy().getRequest(ParseRequest.class);
        request.setAvertiseDiscoverCallback(lisenter);
        AdvertiserLog.e(TAG, "discoverDevice: "+ ByteUtils.BinaryToHexString(pairload));
        startAdvertising(pairload);
    }

    public void connectDevice(AdvertiserDevice device, AdvertiserConnectCallback callback){
        resetDiscover(device);
        pairload[4] = (byte) 0xC3;
        ParseRequest<T> request = Rproxy.getProxy().getRequest(ParseRequest.class);
        request.setAvertiseConnectCallback(callback);
        AdvertiserLog.e(TAG, "connect: "+ ByteUtils.BinaryToHexString(pairload));
        startAdvertising(pairload);
    }

    void retryConnect(){
        startAdvertising(pairload);
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            AdvertiserLog.e(TAG, "onStartSuccess: 开启广播成功");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                AdvertiserLog.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                AdvertiserLog.e(TAG, "Failed to start advertising because no advertising instance is available.");
            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                AdvertiserLog.e(TAG, "Failed to start advertising as the advertising is already started");
            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                AdvertiserLog.e(TAG, "Operation failed due to an internal error");
            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                AdvertiserLog.e(TAG, "This feature is not supported on this platform");
            }
        }
    };
}
