package cn.com.heaton.advertisersdk.callback;

/**
 * Created by LiuLei on 2017/10/23.
 */

public abstract class AdvertiserScanCallback<T> {
    /**
     *  Start the scan
     */
    public void onStart(){};

    /**
     *  Stop scanning
     */
    public void onStop(){};

    /**
     * Scan to device
     * @param device ble device object
     * @param rssi rssi
     * @param scanRecord Bluetooth radio package
     */
    public void onLeScan(T device, int rssi, byte[] scanRecord){};

    public void onParsedData(T device, byte[] parsedData){};

    public void onScanFailed(int errorCode){};
}
