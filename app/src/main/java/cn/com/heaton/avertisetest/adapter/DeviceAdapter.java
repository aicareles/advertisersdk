package cn.com.heaton.avertisetest.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.com.heaton.advertisersdk.AdvertiserDevice;
import cn.com.heaton.avertisetest.R;
import cn.com.heaton.avertisetest.base.BaseAdapter;

/**
 *
 * Created by LiuLei on 2017/12/26.
 */

public class DeviceAdapter extends BaseAdapter<AdvertiserDevice> {

    public DeviceAdapter(Context context, List<AdvertiserDevice> list) {
        super(context, list);
    }

    @Override
    public int getContentView() {
        return R.layout.item_device;
    }

    @Override
    public void onInitView(View view, int position) {
        AdvertiserDevice device = getItem(position);

        TextView tv_name = getAdapterView(view, R.id.tv_name);
        TextView tv_status = getAdapterView(view, R.id.tv_status);
        if(device.isPaired()){
            tv_status.setText("已连接");
        }else {
            tv_status.setText("");
        }
        tv_name.setText(device.getBleName());
    }
}
