package cn.com.heaton.advertisersdk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import cn.com.heaton.advertisersdk.callback.AdvertiserDiscoverCallback;
import cn.com.heaton.advertisersdk.callback.AdvertiserScanCallback;
import cn.com.heaton.advertisersdk.callback.AdvertiserConnectCallback;
import cn.com.heaton.advertisersdk.config.AdvertiserConfig;
import cn.com.heaton.advertisersdk.exception.AdvertiserException;
import cn.com.heaton.advertisersdk.proxy.RequestImpl;
import cn.com.heaton.advertisersdk.proxy.RequestLisenter;
import cn.com.heaton.advertisersdk.proxy.RequestProxy;
import cn.com.heaton.advertisersdk.request.AdvertiserRequest;
import cn.com.heaton.advertisersdk.request.Rproxy;
import cn.com.heaton.advertisersdk.request.ScanRequest;

/**
 * @author Jerry
 * @data 2019/07/16
 * @version V1.0.1
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class AdvertiserClient<T extends AdvertiserDevice>{
    private static final String TAG = "AdvertiserClient";
    @SuppressLint("StaticFieldLeak")
    private static AdvertiserClient sInstance;
    private Context mContext;
    private RequestLisenter<T> mRequest;
    public static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;

    private AdvertiserClient() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * @see  AdvertiserClient
     * @return Get the singleton object of the class
     */
    public static AdvertiserClient<AdvertiserDevice> getDefault() {
        if (sInstance == null) {
            synchronized (AdvertiserClient.class) {
                if (sInstance == null) {
                    sInstance = new AdvertiserClient();
                }
            }
        }
        return sInstance;
    }

    private void proxy(Context context){
        if(null == context){
            try {
                throw new AdvertiserException("Context object cannot be null!");
            } catch (AdvertiserException e) {
                e.printStackTrace();
            }
        }
        this.mContext = context;
        AdvertiserLog.init();
        //设置动态代理
        mRequest = (RequestLisenter) RequestProxy
                .getRequestProxy()
                .bindProxy(context, RequestImpl.getRequestImpl());
    }

    /**
     *
     * @param context Context
     * @return Initialize sdk and get the singleton object of the class
     */
    public static AdvertiserClient<AdvertiserDevice> create(Context context){
        AdvertiserClient<AdvertiserDevice> client = getDefault();
        client.proxy(context);
        return client;
    }

    /**
     * @see AdvertiserConfig
     * @return Get the configuration class
     */
    public static AdvertiserConfig config(){
        return AdvertiserConfig.config();
    }

    /**
     * Send code command
     * @param callback Scanning lisenter after code
     */
    public void searchDevice(AdvertiserDiscoverCallback callback){
        mRequest.preparePair(callback);
    }

    /**
     * Request confirmation for successful code
     * @param device 2.4G device obj
     * @param callback Whether the connection lisenter is successful or not
     */
    public void connectDevice(AdvertiserDevice device, AdvertiserConnectCallback callback){
        mRequest.startPair(device, callback);
    }

    /**
     * start advertise
     * @param payload Load data
     */
    public void startAdvertising(final byte[] payload) {
        mRequest.startAvertise(payload);
    }

    /**
     * start advertise
     * @param payload Load data
     * @param bleChannel channel value  37/38/39
     */
    public void startAdvertising(final byte[] payload, int bleChannel) {
        mRequest.startAvertise(payload, bleChannel);
    }

    /**
     * stop advertise
     */
    public void stopAdvertising() {
        mRequest.stopAvertise();
    }

    public void stopAdvertising(Long delay) {
        AdvertiserRequest<T> request = Rproxy.getProxy().getRequest(AdvertiserRequest.class);
        request.stopAdvertising(delay);
    }

    /**
     * Scanning broadcast packets have callbacks
     * @param callback Scanned callback
     */
    public void startScan(AdvertiserScanCallback<T> callback) {
        mRequest.startScan(callback);
    }

    /**
     * Scan broadcast package without callback
     */
    public void startScan() {
        mRequest.startScan();
    }

    /**
     * Stop scanning
     */
    public void stopScan() {
        mRequest.stopScan();
    }

    /**
     * Is scanning
     * @return Is scanning
     */
    public boolean isScanning() {
        ScanRequest<T> request = Rproxy.getProxy().getRequest(ScanRequest.class);
        return request.isScanning();
    }

    /**
     * Get context
     * @return Context
     */
    public Context getContext(){
        return mContext;
    }

    /**
     *
     * @return Bluetooth is on
     */
    public boolean isBleEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 是否支持2.4G蓝牙
     * @return
     */
    public boolean isSupportAdvertiser(){
        return mBluetoothAdapter.getBluetoothLeAdvertiser() != null;
    }

    /**
     * Turn on Bluetooth
     *
     * @param activity Activity object
     */
    public void turnOnBlueTooth(Activity activity) {
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!isBleEnable()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * Turn off Bluetooth
     */
    public boolean turnOffBlueTooth() {
        return !mBluetoothAdapter.isEnabled() || mBluetoothAdapter.disable();
    }
}
