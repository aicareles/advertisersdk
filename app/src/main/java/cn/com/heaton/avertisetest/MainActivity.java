package cn.com.heaton.avertisetest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import cn.com.heaton.advertisersdk.AdvertiserClient;
import cn.com.heaton.avertisetest.app.Protocol;
import cn.com.heaton.avertisetest.base.BaseActivity;

/**
 * description $desc$
 * created by jerry on 2018/11/27.
 */

public class MainActivity extends BaseActivity {

    public static final int REQUEST_ENABLE_BT = 1;
    private ConnectDialog mConnectDialog;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        checkBLEStatus();

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConnectDialog == null){
                    mConnectDialog = new ConnectDialog(MainActivity.this, R.style.dialog);
                    mConnectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            AdvertiserClient.getDefault().stopScan();
                        }
                    });
                    mConnectDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            mConnectDialog.scan();
                        }
                    });
                }
                mConnectDialog.show();
            }
        });

        findViewById(R.id.btn_send).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: //手指按下
                        Protocol.getInstance().sendCommand(null,new byte[]{}, 37);
                        break;
                    case MotionEvent.ACTION_UP: //手指抬起
                    case MotionEvent.ACTION_CANCEL: //手指抬起  取消
//                        Protocol.getInstance().stopCommand(37);
                        AdvertiserClient.getDefault().stopAdvertising();
                        break;
                }
                return false;
            }
        });

    }

    private void initView() {

    }

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            AdvertiserClient.getDefault().stopAdvertising();
        }
    };

    private void checkBLEStatus() {
        // 检查设备是否打开蓝牙
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null){
            if(!bluetoothAdapter.isEnabled()){
                //4、若未打开，则请求打开蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }else {//已打开,请求蓝牙相关权限
                requestBLE();
            }
        }
    }

    private void requestBLE(){
        requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                "请求蓝牙相关权限", new GrantedResult(){
                    @Override
                    public void onResult(boolean granted) {
                        if (granted){
                            //初始化sdk
                            initSDK();
                        }else {
                            finish();
                        }
                    }
                });
    }

    /**
     * 1、初始化SDK
     */
    private void initSDK() {
        AdvertiserClient
                .config()
                .setThroAvertiseException(true)
                .setLogAvertiseExceptions(true)
                .setPrefixAvertiseName("BXC-")
                .setAvertiseProductCode(new byte[]{0x54, 0x00, 0x11})
                .setAddressPrefix("54:52:22")
                .setConnectTimeout(8 * 1000)
                .setScanPeriod(8 * 1000)
                .setRetryConnect(3)
                .create(getApplication());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            requestBLE();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
