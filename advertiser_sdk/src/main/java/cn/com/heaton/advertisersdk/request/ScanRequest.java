package cn.com.heaton.advertisersdk.request;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.heaton.advertisersdk.AdvertiserDevice;
import cn.com.heaton.advertisersdk.AdvertiserFactory;
import cn.com.heaton.advertisersdk.AdvertiserHandler;
import cn.com.heaton.advertisersdk.AdvertiserLog;
import cn.com.heaton.advertisersdk.callback.AdvertiserScanCallback;
import cn.com.heaton.advertisersdk.annotation.Implement;
import cn.com.heaton.advertisersdk.config.AdvertiserConfig;

/**
 * description $desc$
 * created by jerry on 2018/11/27.
 */

@Implement(ScanRequest.class)
public class ScanRequest<T extends AdvertiserDevice> {

    private static final String TAG = "ScanRequest";

    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mScanner;
    private ScanSettings mScannerSetting;
    private List<ScanFilter> mFilters;
    private AdvertiserScanCallback<T> mScanCallback;
    private ArrayList<T> mScanDevices = new ArrayList<>();
    private ParseRequest<T> mParseRequest;
    private Handler mHandler;

    protected ScanRequest() {
        mHandler = AdvertiserHandler.getHandler();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //mScanner will be null if Bluetooth has been closed
            mScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mScannerSetting = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            mFilters = new ArrayList<>();
        }
        mParseRequest = Rproxy.getProxy().getRequest(ParseRequest.class);
    }

    //扫描广播包
    public void startScan(AdvertiserScanCallback<T> callback) {
        if(mScanning)return;
        if(callback != null){
            mScanCallback = callback;
        }
        //必须再次清空arraylist   停止时清空一直没有清除干净  始终剩一个size（严重BUG）
        mScanDevices.clear();
        mScanning = true;
        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(stopRunnble, AdvertiserConfig.config().getScanPeriod());
//        mBluetoothAdapter.startLeScan(mLeScanCallback);
        mScanner.startScan(mFilters, mScannerSetting, mScannerCallback);
        if(callback != null){
            mScanCallback.onStart();
        }
    }

    public void startScan(){
        startScan(null);
    }

    public void stopScan() {
        if (!mScanning) return;
        mHandler.removeCallbacks(stopRunnble);
        mScanDevices.clear();
        mScanning = false;
        mScanner.stopScan(mScannerCallback);
        if(mScanCallback != null){
            mScanCallback.onStop();
        }
    }

    private Runnable stopRunnble = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };

    public boolean isScanning() {
        return mScanning;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback mScannerCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            byte[] scanRecord = result.getScanRecord().getBytes();
            if (device == null || scanRecord == null) return;
            //根据mac地址前缀过滤目标设备（不然iOS广播包会影响）
            String addressPrefix = AdvertiserConfig.config().getAddressPrefix();
            if (!TextUtils.isEmpty(addressPrefix)){
                //过滤指定设备
                if (!device.getAddress().startsWith(addressPrefix))return;
            }
            AdvertiserLog.i("onScanResult >>>>>", device.getAddress());
            T avertiseDevice = getDevice(device.getAddress());
            if (avertiseDevice == null) {
//                avertiseDevice = (T) AdvertiserFactory.create(AdvertiserDevice.class, device);
                avertiseDevice = AdvertiserFactory.newDevice(device);
                mScanDevices.add(avertiseDevice);
            }
            mParseRequest.parseScanRecord(avertiseDevice, scanRecord, mScanCallback);
            if (mScanCallback != null) {
                mScanCallback.onLeScan(avertiseDevice, result.getRssi(), scanRecord);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                AdvertiserLog.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            AdvertiserLog.e("Scan Failed", "Error Code: " + errorCode);
            if (mScanCallback != null) {
                mScanCallback.onScanFailed(errorCode);
            }
        }
    };

    private T getDevice(String address) {
        for (T device : mScanDevices) {
            if (device.getBleAddress().equals(address)) {
                return device;
            }
        }
        return null;
    }

}
