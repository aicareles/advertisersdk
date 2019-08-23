package cn.com.heaton.avertisetest;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.heaton.advertisersdk.AdvertiserClient;
import cn.com.heaton.advertisersdk.AdvertiserDevice;
import cn.com.heaton.advertisersdk.AdvertiserLog;
import cn.com.heaton.advertisersdk.callback.AdvertiserScanCallback;
import cn.com.heaton.advertisersdk.callback.AdvertiserDiscoverCallback;
import cn.com.heaton.advertisersdk.callback.AdvertiserConnectCallback;
import cn.com.heaton.avertisetest.app.Protocol;
import cn.com.heaton.avertisetest.base.BaseDialog;

/**
 * 设备扫描连接诓
 * Created by LiuLei on 2018/12/04.
 */

public class ConnectDialog extends BaseDialog {
    private static final String TAG = "ConnectDialog";
    private DeviceAdapter mAdapter;
    private List<AdvertiserDevice> mList = new ArrayList<>();
    private ListView mListView;
    private Button scan;
    private Handler mHandler;
    private AdvertiserClient<AdvertiserDevice> mClient;

    ConnectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    ConnectDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_connect;
    }

    @Override
    protected void onInitView() {
        mListView = findViewById(R.id.listView);
        scan = findViewById(R.id.tv_scan);
    }

    @Override
    protected void onInitData() {
        mClient = AdvertiserClient.getDefault();
        mHandler = new Handler();
        mAdapter = new DeviceAdapter(getContext(), mList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void initLinsenter() {
        super.initLinsenter();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                connect(mAdapter.getItem(i));
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
    }

    private void connect(final AdvertiserDevice device) {
        if (null == device)return;
        if(device.isPaired())return;
        //停止扫描
        mClient.stopScan();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mClient.startScan();
                mClient.connectDevice(device, advertiserConnectCallback);
            }
        },100);//500
        showDialog();
    }

    /**
     * 开始连接的回调
     */
    private AdvertiserConnectCallback advertiserConnectCallback = new AdvertiserConnectCallback() {
        @Override
        public void onAvertiseConnected(AdvertiserDevice device) {
            AdvertiserLog.e(TAG, "onAvertiseConnected:"+device.toString());
            toast(R.string.connect_success);
            mClient.stopScan();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onAvertiseConnectTimeOut() {
            toast(R.string.connect_timeout);
        }
    };

    void scan() {
        if (mClient.isScanning()) return;
        mList.clear();
        mAdapter.notifyDataSetChanged();
        mClient.startScan(scanCallback);
        mClient.searchDevice(advertiserDiscoverCallback);
    }

    /**
     * 2.4G对码的回调（2.4G扫描结果）
     */
    private AdvertiserDiscoverCallback advertiserDiscoverCallback = new AdvertiserDiscoverCallback() {
        @Override
        public void onAvertiseScan(AdvertiserDevice device) {
            for (AdvertiserDevice d : mList) {
                if (d.getBleAddress().equals(device.getBleAddress())) {
                    return;
                }
            }
            mList.add(device);
            AdvertiserLog.e(TAG, "onReceiveNormalMode: "+device.toString());
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * BLE蓝牙扫描回调
     */
    private AdvertiserScanCallback<AdvertiserDevice> scanCallback = new AdvertiserScanCallback<AdvertiserDevice>() {
        @Override
        public void onStart() {
            super.onStart();
            scan.setText("扫描中...");
        }

        @Override
        public void onStop() {
            super.onStop();
            //停止广播
            AdvertiserLog.e(TAG, "onStop: 停止扫描/广播");
            //6、停止广播
            mClient.stopAdvertising();
            scan.setText("扫描");
        }
    };

}
