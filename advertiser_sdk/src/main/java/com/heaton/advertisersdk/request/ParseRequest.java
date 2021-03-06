package com.heaton.advertisersdk.request;

import android.os.Handler;
import android.util.SparseArray;

import com.heaton.advertisersdk.AdvertiserClient;
import com.heaton.advertisersdk.AdvertiserConfig;
import com.heaton.advertisersdk.AdvertiserDevice;
import com.heaton.advertisersdk.AdvertiserHandler;
import com.heaton.advertisersdk.AdvertiserLog;
import com.heaton.advertisersdk.AdvertiserStates;
import com.heaton.advertisersdk.callback.AdvertiserConnectCallback;
import com.heaton.advertisersdk.callback.AdvertiserDiscoverCallback;
import com.heaton.advertisersdk.callback.AdvertiserScanCallback;
import com.heaton.advertisersdk.interceptor.RealInterceptorHandler;
import com.heaton.advertisersdk.utils.ByteUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wireless.algorithm.io.cshsoft.a2_4gcrytonlib.WirelessEncoder;

/**
 * description $desc$
 * created by jerry on 2018/11/27.
 */
@Implement(ParseRequest.class)
public class ParseRequest<T extends AdvertiserDevice> {
    private static final String TAG = "ParseRequest";
    public static final int BLE_GAP_AD_TYPE_FLAGS = 0x01;
    public static final int BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE = 0x03;//< Complete list of 16 bit service UUIDs.
    private static final int BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;//< Manufacturer Specific Data.
    private final static long SINGLE_CONNECT_TIME = 2000L;

    private AdvertiserDiscoverCallback advertiserDiscoverCallback;
    private AdvertiserConnectCallback advertiserConnectCallback;
    private final SparseArray<byte[]> records = new SparseArray<>();
    private List<AdvertiserDevice> devicePool = new ArrayList<>();//2.4G设备池
    private byte[] avertiseProductCode;
    private byte[] randomCode;
    private int retryCount = 0;//重试次数
    private Handler handler;

    protected ParseRequest() {
        handler = AdvertiserHandler.getHandler();
        avertiseProductCode = AdvertiserConfig.config().getAvertiseProductCode();
        randomCode = AdvertiserConfig.generateRandom();
    }

    void setAvertiseDiscoverCallback(AdvertiserDiscoverCallback callback) {
        this.advertiserDiscoverCallback = callback;
        if (advertiserDiscoverCallback != null) {
            advertiserDiscoverCallback.onDiscoverStart();
        }
        handler.removeCallbacks(discoverStopRunnable);
        handler.postDelayed(discoverStopRunnable, AdvertiserConfig.config().getScanPeriod());
    }

    private Runnable discoverStopRunnable = new Runnable() {
        @Override
        public void run() {
            //Stop scanning device (stop broadcasting)
            AdvertiserClient.getDefault().stopAdvertising();
            if (advertiserDiscoverCallback != null) {
                advertiserDiscoverCallback.onDiscoverStop();
            }
        }
    };

    void setAvertiseConnectCallback(AdvertiserConnectCallback callback) {
        if (null != callback) {
            retryCount = 0;
            AdvertiserHandler.getHandler().removeCallbacks(retryRunnable);
            this.advertiserConnectCallback = callback;
            //分次进行连接（根据重连次数）
            handler.postDelayed(retryRunnable, SINGLE_CONNECT_TIME);
        }
    }

    private Runnable retryRunnable = new Runnable() {
        @Override
        public void run() {
            if (retryCount < AdvertiserConfig.config().getConnectTimeout() / SINGLE_CONNECT_TIME) {
                retryCount++;
                AdvertiserLog.e(TAG, "正在第" + retryCount + "次重连");
                AdvertiserRequest request = Rproxy.getProxy().getRequest(AdvertiserRequest.class);
                request.retryConnect();
                handler.postDelayed(this, SINGLE_CONNECT_TIME);
            } else {
                handler.removeCallbacks(retryRunnable);
                advertiserConnectCallback.onAvertiseConnectTimeOut();
            }
        }
    };

    //广播包解析,每一个进入的都是新的device对象
    void parseScanRecord(T newDevice, byte[] scanRecord, final AdvertiserScanCallback<T> scanCallback) {
        if (scanRecord != null) {
            int index = 0;
            while (index < scanRecord.length) {
                final int length = scanRecord[index++];
                //Done once we run out of records
                if (length == 0) break;
                final int type = scanRecord[index] & 0xFF;
                //Done if our record isn't a valid type
                if (type == 0) break;
                final byte[] data = Arrays.copyOfRange(scanRecord, index + 1, index + length);
                records.put(type, data);
                //Advance
                index += length;
            }
            byte[] data = records.get(BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA);
            if (data == null || data.length <= 0) return;
            byte[] buf = Arrays.copyOfRange(data, 2, data.length);
            if (buf.length != 16) return;
            //解密
            WirelessEncoder.cipher(buf, false);
            if (scanCallback != null){
                scanCallback.onParsedData(newDevice, buf);
            }
            AdvertiserLog.e(TAG, "parseScanRecord>>>>>: "+ ByteUtils.byteArrayToHexStr(buf));
            RealInterceptorHandler.getInstance().response(buf);
            if (isOwnProduct(buf)) {
                AdvertiserDevice device = takeDevicePool(newDevice);
                if (buf[4] == 0x42) {//2.4G回复对码(对码模式)
                    if (advertiserDiscoverCallback != null) {
                        setDeviceValue(device, buf);
                        device.setPairState(AdvertiserStates.StatusCode.UNPAIR);
                        advertiserDiscoverCallback.onAvertiseScan(device);
                        AdvertiserLog.e(TAG, "扫描到新设备" + device.toString());
                    }
                } else if (buf[4] == 0x45) {
                    if (advertiserDiscoverCallback != null) {
                        setDeviceValue(device, buf);
                        device.setPairState(AdvertiserStates.StatusCode.PAIRED);
                        advertiserDiscoverCallback.onAvertiseScan(device);
                        AdvertiserLog.e(TAG, "设备已在线>>>>>" + device.toString());
                    }
                } else if (buf[4] == 0x44) {
                    if (advertiserConnectCallback != null){
                        handler.removeCallbacks(retryRunnable);
                        setDeviceValue(device, buf);
                        device.setPairState(AdvertiserStates.StatusCode.PAIRED);
                        advertiserConnectCallback.onAvertiseConnected(device);
                        AdvertiserLog.e(TAG, "设备配对完成"+device.toString());
                    }
                }
            }
        }
    }

    private boolean isOwnProduct(byte[] buf){
        return buf[1] == avertiseProductCode[0]
                && buf[2] == avertiseProductCode[1]
                && buf[3] == avertiseProductCode[2]
                && buf[13] == randomCode[0]
                && buf[14] == randomCode[1]
                && buf[15] == randomCode[2];
    }

    //给扫描到的设备赋值
    private void setDeviceValue(AdvertiserDevice device, byte[] buf){
        byte[] randomCode = AdvertiserConfig.generateRandom();
        byte[] rolling_code = new byte[3];
        System.arraycopy(buf, 6, rolling_code, 0, rolling_code.length);
        device.setRollingCode(rolling_code);
        device.setRandomCode(randomCode);
        device.setOn(buf[10] == 0x01);
        String suffix = ByteUtils.BinaryToHexString(rolling_code).replaceAll(" ", "");
        device.setBleName(AdvertiserConfig.config().getPrefixAvertiseName() + suffix);
    }

    //如果设备池中没有该对象地址，把设备对象先放入2.4G设备池中，如果有则返回设备池中的设备对象
    private AdvertiserDevice takeDevicePool(AdvertiserDevice device) {
        for (AdvertiserDevice d : devicePool) {
            if (device.getBleAddress().equals(d.getBleAddress())) {
                return d;
            }
        }
        devicePool.add(device);
        return device;
    }


}
