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

    private boolean scanning;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private ScanSettings scanSettings;
    private List<ScanFilter> filters;
    private AdvertiserScanCallback<T> scanCallback;
    private ArrayList<T> scanDevices = new ArrayList<>();
    private ParseRequest<T> parseRequest;
    private Handler handler;

    protected ScanRequest() {
        handler = AdvertiserHandler.getHandler();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //scanner will be null if Bluetooth has been closed
            scanner = bluetoothAdapter.getBluetoothLeScanner();
            scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<>();
            filters.add(new ScanFilter.Builder()
                    .setServiceUuid(null)//8.0以上手机后台扫描，必须开启
                    .build());
        }
        parseRequest = Rproxy.getProxy().getRequest(ParseRequest.class);
    }

    //扫描广播包
    public void startScan(AdvertiserScanCallback<T> callback) {
        if(scanning)return;
        if(callback != null){
            scanCallback = callback;
        }
        //必须再次清空arraylist   停止时清空一直没有清除干净  始终剩一个size（严重BUG）
        scanDevices.clear();
        scanning = true;
        // Stops scanning after a pre-defined scan period.
        handler.postDelayed(stopRunnble, AdvertiserConfig.config().getScanPeriod());
//        bluetoothAdapter.startLeScan(mLeScanCallback);
        if (bluetoothAdapter.isEnabled()){
            scanner.startScan(filters, scanSettings, mScannerCallback);
            if(callback != null){
                scanCallback.onStart();
            }
        }else {
            if(callback != null){
                //未开启蓝牙
                scanCallback.onScanFailed(-1);
            }
        }

    }

    public void startScan(){
        startScan(null);
    }

    public void stopScan() {
        if (!scanning) return;
        handler.removeCallbacks(stopRunnble);
        scanDevices.clear();
        scanning = false;
        if (bluetoothAdapter.isEnabled()){
            scanner.stopScan(mScannerCallback);
            if(scanCallback != null){
                scanCallback.onStop();
            }
        }
    }

    private Runnable stopRunnble = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };

    public boolean isScanning() {
        return scanning;
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
                scanDevices.add(avertiseDevice);
            }
            parseRequest.parseScanRecord(avertiseDevice, scanRecord, scanCallback);
            if (scanCallback != null) {
                scanCallback.onLeScan(avertiseDevice, result.getRssi(), scanRecord);
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
            if (scanCallback != null) {
                scanCallback.onScanFailed(errorCode);
            }
        }
    };

    private T getDevice(String address) {
        for (T device : scanDevices) {
            if (device.getBleAddress().equals(address)) {
                return device;
            }
        }
        return null;
    }

}
