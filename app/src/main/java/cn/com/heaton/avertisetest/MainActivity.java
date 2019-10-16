package cn.com.heaton.avertisetest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.heaton.advertisersdk.AdvertiserClient;
import cn.com.heaton.advertisersdk.AdvertiserDevice;
import cn.com.heaton.advertisersdk.AdvertiserLog;
import cn.com.heaton.advertisersdk.callback.AdvertiserScanCallback;
import cn.com.heaton.advertisersdk.utils.ByteUtils;
import cn.com.heaton.avertisetest.adapter.SendAdvertiserAdapter;
import cn.com.heaton.avertisetest.app.LogIntercept;
import cn.com.heaton.avertisetest.base.BaseActivity;
import cn.com.heaton.avertisetest.model.SendRecord;

/**
 * description $desc$
 * created by jerry on 2018/11/27.
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_ENABLE_BT = 1;
    private Handler mHandler = new Handler();
    private EditText et_delay, et_duration, et_address_code, et_payload, et_filter;
    private Button btn_send;
    private ImageView iv_scan;
    private ListView lv_send, lv_receive;
    private SendAdvertiserAdapter sendAdvertiserAdapter;
    private List<SendRecord> sendRecords;

    private SendAdvertiserAdapter receiveAdvertiserAdapter;
    private List<SendRecord> receiveAllRecords;
    private List<SendRecord> receiveFilterRecords;

    private AdvertiserClient<AdvertiserDevice> mClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initLinsenter();

        checkBLEStatus();

    }

    private void initData() {
        mClient = AdvertiserClient.getDefault();
        sendRecords = new ArrayList<>();
        sendAdvertiserAdapter = new SendAdvertiserAdapter(this, sendRecords);
        lv_send.setAdapter(sendAdvertiserAdapter);

        receiveAllRecords = new ArrayList<>();
        receiveFilterRecords = new ArrayList<>();
        receiveAdvertiserAdapter = new SendAdvertiserAdapter(this, receiveFilterRecords);
        lv_receive.setAdapter(receiveAdvertiserAdapter);
    }

    private void initLinsenter() {
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClient.startScan(scanCallback);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String payload = et_payload.getText().toString();
                if (!TextUtils.isEmpty(payload)){
                    String hexPayload = payload.replaceAll(" ","");
                    if (hexPayload.length() != 32){
                        Toast.makeText(MainActivity.this, "长度必须是16个byte", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    byte[] bytes = ByteUtils.hexStrToByteArray(hexPayload);
                    mClient.startAdvertising(bytes);
                    mClient.stopAdvertising(Long.parseLong(et_duration.getText().toString()));
                    addSendRecordToList(payload);
                }
            }
        });

        et_filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String filter = editable.toString().replaceAll(" ", "");
                filterReceive(filter);
            }
        });
    }

    //添加一条广播记录到list列表
    private void addSendRecordToList(String payload) {
        SendRecord sendRecord = new SendRecord();
        sendRecord.setHexPayload(payload);
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        sendRecord.setTime(df.format(new Date()));
        sendRecords.add(sendRecord);
        sendAdvertiserAdapter.notifyDataSetChanged();
    }

    //添加一条广播扫描到的记录到list列表
    private void addReceiveRecordToList(String payload) {
        SendRecord sendRecord = new SendRecord();
        sendRecord.setHexPayload(payload);
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        sendRecord.setTime(df.format(new Date()));
        //添加到所有列表
        receiveAllRecords.add(sendRecord);
        //添加到adapter
        receiveFilterRecords.add(sendRecord);
        receiveAdvertiserAdapter.notifyDataSetChanged();
    }

    private void filterReceive(String filterBytes){
        Log.e(TAG, "filterReceive: "+filterBytes);
        receiveFilterRecords.clear();
        for (SendRecord sendRecord : receiveAllRecords){
            String str = sendRecord.getHexPayload().replaceAll(" ", "");
            if (str.contains(filterBytes)){
                receiveFilterRecords.add(sendRecord);
            }
        }
        receiveAdvertiserAdapter.notifyDataSetChanged();
    }

    private void initView() {
        et_delay = findViewById(R.id.et_delay);
        et_duration = findViewById(R.id.et_duration);
        et_address_code = findViewById(R.id.et_address_code);
        et_payload = findViewById(R.id.et_payload);
        et_filter = findViewById(R.id.et_filter);
        iv_scan = findViewById(R.id.iv_scan);
        btn_send = findViewById(R.id.btn_send);
        lv_send = findViewById(R.id.lv_send);
        lv_receive = findViewById(R.id.lv_receive);
    }

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
                .setAvertiseProductCode(new byte[]{0x54, 0x00, 0x16})
//                .setAddressPrefix("54:52:22")
                .setConnectTimeout(8 * 1000)
                .setScanPeriod(30 * 1000)
                .setRetryConnect(3)
                .setInterceptor(new LogIntercept())
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

    private AdvertiserScanCallback<AdvertiserDevice> scanCallback = new AdvertiserScanCallback<AdvertiserDevice>() {

        @Override
        public void onParsedData(AdvertiserDevice device, byte[] parsedData) {
            super.onParsedData(device, parsedData);
            Log.e(TAG, "onParsedData: >>>>");
            String hexString = ByteUtils.BinaryToHexString(parsedData);
            addReceiveRecordToList(hexString);
        }

        @Override
        public void onStop() {
            super.onStop();
        }
    };
}
